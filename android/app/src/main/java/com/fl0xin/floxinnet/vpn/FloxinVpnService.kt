package com.fl0xin.floxinnet.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.fl0xin.floxinnet.R
import com.fl0xin.floxinnet.data.BlocklistStore
import com.fl0xin.floxinnet.data.FloxinDatabase
import com.fl0xin.floxinnet.dns.DnsConfig
import com.fl0xin.floxinnet.dns.DnsException
import com.fl0xin.floxinnet.dns.DnsResolver
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FloxinVpnService : VpnService() {
    @Inject lateinit var database: FloxinDatabase
    private var vpnInterface: android.os.ParcelFileDescriptor? = null
    private var worker: ExecutorService? = null
    private val running = AtomicBoolean(false)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) stopVpn() else startVpn()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = super.onBind(intent)

    private fun startVpn() {
        if (running.getAndSet(true)) return
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notification())
        try {
            vpnInterface = Builder()
                .setSession("FLOXIN NET")
                .setMtu(1500)
                .addAddress("10.8.0.2", 32)
                // Only the virtual DNS subnet enters the VPN. Normal app
                // traffic remains on the physical network.
                .addRoute("10.8.0.0", 24)
                .addDnsServer("10.8.0.2")
                .establish()
            if (vpnInterface == null) throw IllegalStateException("VPN permission was not granted")
            worker = Executors.newSingleThreadExecutor { runnable ->
                Thread(runnable, "floxin-vpn-loop").apply { isDaemon = true }
            }.also { it.execute(::runLoop) }
            isRunning = true
        } catch (_: Exception) {
            isRunning = false
            running.set(false)
            vpnInterface?.close()
            vpnInterface = null
            stopSelf()
        }
    }

    private fun runLoop() {
        val descriptor = vpnInterface ?: return
        val blocklist = BlocklistStore.load(this, database)
        val resolver = DnsResolver(
            DnsConfig.load(this),
            blocklist = blocklist,
            protectDatagram = { protect(it) },
            protectSocket = { protect(it) },
        )
        FileInputStream(descriptor.fileDescriptor).use { input ->
            FileOutputStream(descriptor.fileDescriptor).use { output ->
                val buffer = ByteBuffer.allocate(32767)
                while (running.get()) {
                    buffer.clear()
                    val count = input.read(buffer.array())
                    if (count <= 0) continue
                    val query = DnsPacket.parse(buffer.array(), count) ?: continue
                    val response = try {
                        resolver.resolve(query.name, query.dnsPayload)
                    } catch (_: DnsException) {
                        servfail(query.dnsPayload)
                    }
                    output.write(DnsPacket.buildResponse(query, response))
                    output.flush()
                }
            }
        }
    }

    private fun servfail(query: ByteArray): ByteArray = query.copyOf().also {
        it[2] = 0x81.toByte()
        it[3] = 0x82.toByte()
        it[6] = 0
        it[7] = 0
    }

    private fun stopVpn() {
        if (!running.getAndSet(false)) {
            isRunning = false
            stopSelf()
            return
        }
        isRunning = false
        worker?.shutdownNow()
        worker = null
        vpnInterface?.close()
        vpnInterface = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }

    private fun notification(): Notification = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("FLOXIN NET")
        .setContentText(getString(R.string.status_running))
        .setSmallIcon(R.drawable.ic_floxin_notification)
        .setOngoing(true)
        .setCategory(NotificationCompat.CATEGORY_SERVICE)
        .build()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "FLOXIN NET DNS", NotificationManager.IMPORTANCE_LOW)
            )
        }
    }

    companion object {
        const val ACTION_START = "com.fl0xin.floxinnet.vpn.START"
        const val ACTION_STOP = "com.fl0xin.floxinnet.vpn.STOP"
        private const val CHANNEL_ID = "floxin_dns"
        private const val NOTIFICATION_ID = 5353
        @Volatile var isRunning: Boolean = false
            private set
    }
}
