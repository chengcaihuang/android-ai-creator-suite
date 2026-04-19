package com.aicreator.suite.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aicreator.suite.data.model.*

/**
 * 搴旂敤鏁版嵁搴? */
@Database(
    entities = [
        Content::class,
        Template::class,
        UserSettings::class,
        Earning::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun templateDao(): TemplateDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun earningDao(): EarningDao
}

/**
 * Room绫诲瀷杞崲鍣? */
class RoomTypeConverters {
    
    @androidx.room.TypeConverter
    fun fromContentType(value: ContentType): String {
        return value.name
    }
    
    @androidx.room.TypeConverter
    fun toContentType(value: String): ContentType {
        return ContentType.valueOf(value)
    }
    
    @androidx.room.TypeConverter
    fun fromContentStatus(value: ContentStatus): String {
        return value.name
    }
    
    @androidx.room.TypeConverter
    fun toContentStatus(value: String): ContentStatus {
        return ContentStatus.valueOf(value)
    }
}
