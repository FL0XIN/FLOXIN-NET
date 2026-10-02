package com.fl0xin.floxinnet.data

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class Status(val running: Boolean, val pid: Int?, val mode: String, val provider: String, val uptime_seconds: Long, val port: Int)
data class Stats(val total_queries: Int, val blocked: Int, val cached: Int, val forwarded: Int, val blocklist_size: Int, val cache_size: Int)
data class Provider(val name: String, val primary: String, val secondary: String, val region: String)
data class Scenario(val code: String, val type: String, val provider: String, val network: String, val vpn: String, val description: String)
data class ScenarioRequest(val code: String)
interface FloxinApi {
    @GET("api/v1/status") suspend fun status(): Status
    @GET("api/v1/stats") suspend fun stats(): Stats
    @GET("api/v1/providers") suspend fun providers(): List<Provider>
    @GET("api/v1/scenarios") suspend fun scenarios(): List<Scenario>
    @POST("api/v1/scenario") suspend fun applyScenario(@retrofit2.http.Body request: ScenarioRequest): Map<String, Any>
    @POST("api/v1/start") suspend fun start(): Map<String, Any>
    @POST("api/v1/stop") suspend fun stop(): Map<String, Any>
}
