package com.fl0xin.floxinvpn.vpn

import android.net.VpnService
import android.content.Intent

/**
 * Ownership boundary for the OpenVPN 3 MPL-2.0 native engine.
 *
 * This service deliberately does not establish a dummy TUN interface. A VPN must not
 * claim to be connected until the real OpenVPN 3 session reports an established tunnel.
 */
class FloxinVpnService : VpnService() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stopSelfResult(startId)
        return START_NOT_STICKY
    }
}
