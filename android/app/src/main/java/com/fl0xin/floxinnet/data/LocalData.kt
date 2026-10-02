package com.fl0xin.floxinnet.data
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.first

val Context.settingsDataStore by preferencesDataStore("floxin_settings")

object SettingKeys {
    val provider = stringPreferencesKey("provider")
    val theme = stringPreferencesKey("theme")
    val network = stringPreferencesKey("network")
    val rtl = booleanPreferencesKey("rtl")
    val apiToken = stringPreferencesKey("api_token")
}

class TokenStore(private val context: Context) {
    suspend fun get(): String = context.settingsDataStore.data.first()[SettingKeys.apiToken].orEmpty()
    suspend fun set(value: String) { context.settingsDataStore.edit { it[SettingKeys.apiToken] = value } }
}

@Entity(tableName = "stats_cache")
data class StatsCache(@PrimaryKey val key: String, val value: String, val savedAt: Long = System.currentTimeMillis())

@Entity(tableName = "blocklist")
data class BlocklistEntry(@PrimaryKey val domain: String)

@Dao
interface StatsDao {
    @Query("SELECT * FROM stats_cache WHERE `key` = :key LIMIT 1") suspend fun get(key: String): StatsCache?
    @Insert suspend fun put(item: StatsCache)
}

@Dao
interface BlocklistDao {
    @Query("SELECT COUNT(*) FROM blocklist") fun count(): Int
    @Query("SELECT domain FROM blocklist") fun allDomains(): List<String>
    @Query("DELETE FROM blocklist") fun clear()
    @Insert(onConflict = OnConflictStrategy.IGNORE) fun insertAll(items: List<BlocklistEntry>)
}

@Database(entities = [StatsCache::class, BlocklistEntry::class], version = 2, exportSchema = false)
abstract class FloxinDatabase : androidx.room.RoomDatabase() {
    abstract fun statsDao(): StatsDao
    abstract fun blocklistDao(): BlocklistDao
}
