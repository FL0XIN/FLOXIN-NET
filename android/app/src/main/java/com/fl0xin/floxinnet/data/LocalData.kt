package com.fl0xin.floxinnet.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

val Context.settingsDataStore by preferencesDataStore("floxin_settings")
object SettingKeys { val provider = stringPreferencesKey("provider"); val theme = stringPreferencesKey("theme"); val network = stringPreferencesKey("network"); val rtl = booleanPreferencesKey("rtl"); val apiToken = stringPreferencesKey("api_token") }
class TokenStore(private val context: Context) {
    suspend fun get(): String = context.settingsDataStore.data.first()[SettingKeys.apiToken].orEmpty()
    suspend fun set(value: String) { context.settingsDataStore.edit { it[SettingKeys.apiToken] = value } }
}
@Entity(tableName = "stats_cache") data class StatsCache(@PrimaryKey val key: String, val value: String, val savedAt: Long = System.currentTimeMillis())
@Dao interface StatsDao { @Query("SELECT * FROM stats_cache WHERE `key` = :key LIMIT 1") suspend fun get(key: String): StatsCache?; @Insert suspend fun put(item: StatsCache) }
@Database(entities = [StatsCache::class], version = 1, exportSchema = false) abstract class FloxinDatabase : androidx.room.RoomDatabase() { abstract fun statsDao(): StatsDao }
