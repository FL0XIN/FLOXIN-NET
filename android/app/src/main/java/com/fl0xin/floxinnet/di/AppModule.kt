package com.fl0xin.floxinnet.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.room.Room
import com.fl0xin.floxinnet.data.FloxinApi
import com.fl0xin.floxinnet.data.FloxinDatabase
import com.fl0xin.floxinnet.data.TokenStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking

class TermuxBridge(private val context: Context) {
    fun isTermuxInstalled() = runCatching { context.packageManager.getPackageInfo("com.termux", 0) }.isSuccess
    fun isTermuxApiInstalled() = runCatching { context.packageManager.getPackageInfo("com.termux.api", 0) }.isSuccess
    fun setupMessage() = if (!isTermuxInstalled()) "Install Termux from F-Droid, then run the FLOXIN setup command." else "Open Termux and run: FLOXIN api start"
}

@Module @InstallIn(SingletonComponent::class) object AppModule {
    @Provides @Singleton fun tokenStore(@ApplicationContext context: Context) = TokenStore(context)
    @Provides @Singleton fun api(tokens: TokenStore): FloxinApi {
        val auth = Interceptor { chain ->
            val token = runBlocking { tokens.get() }
            val request = chain.request().newBuilder().apply { if (token.isNotBlank()) addHeader("Authorization", "Bearer $token") }.build()
            chain.proceed(request)
        }
        val client = OkHttpClient.Builder().addInterceptor(auth).build()
        return Retrofit.Builder().baseUrl("http://127.0.0.1:8080/").client(client).addConverterFactory(MoshiConverterFactory.create()).build().create(FloxinApi::class.java)
    }
    @Provides @Singleton fun database(@ApplicationContext context: Context): FloxinDatabase = Room.databaseBuilder(context, FloxinDatabase::class.java, "floxin_cache.db").build()
    @Provides @Singleton fun termux(@ApplicationContext context: Context) = TermuxBridge(context)
}
