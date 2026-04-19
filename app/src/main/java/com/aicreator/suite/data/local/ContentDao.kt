package com.aicreator.suite.data.local

import androidx.room.*
import com.aicreator.suite.data.model.*
import kotlinx.coroutines.flow.Flow

/**
 * 鍐呭鏁版嵁璁块棶瀵硅薄
 */
@Dao
interface ContentDao {
    
    @Query("SELECT * FROM contents ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecentContent(limit: Int = 50): List<Content>
    
    @Query("SELECT * FROM contents WHERE type = :type ORDER BY createdAt DESC")
    suspend fun getContentByType(type: ContentType): List<Content>
    
    @Query("SELECT * FROM contents WHERE platform = :platform ORDER BY createdAt DESC")
    suspend fun getContentByPlatform(platform: String): List<Content>
    
    @Query("SELECT * FROM contents WHERE id = :id")
    suspend fun getContentById(id: String): Content?
    
    @Query("SELECT * FROM contents WHERE status = :status ORDER BY createdAt DESC")
    suspend fun getContentByStatus(status: ContentStatus): List<Content>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: Content)
    
    @Update
    suspend fun updateContent(content: Content)
    
    @Delete
    suspend fun deleteContent(content: Content)
    
    @Query("DELETE FROM contents WHERE id = :id")
    suspend fun deleteContent(id: String)
    
    @Query("SELECT COUNT(*) FROM contents")
    suspend fun getTotalCount(): Int
    
    @Query("SELECT COUNT(*) FROM contents WHERE createdAt >= :startTime AND createdAt <= :endTime")
    suspend fun getCountByDateRange(startTime: Long, endTime: Long): Int
}

/**
 * 妯℃澘鏁版嵁璁块棶瀵硅薄
 */
@Dao
interface TemplateDao {
    
    @Query("SELECT * FROM templates ORDER BY usageCount DESC")
    suspend fun getAllTemplates(): List<Template>
    
    @Query("SELECT * FROM templates WHERE category = :category ORDER BY usageCount DESC")
    suspend fun getTemplatesByCategory(category: String): List<Template>
    
    @Query("SELECT * FROM templates WHERE id = :id")
    suspend fun getTemplateById(id: String): Template?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: Template)
    
    @Update
    suspend fun updateTemplate(template: Template)
    
    @Delete
    suspend fun deleteTemplate(template: Template)
    
    @Query("UPDATE templates SET usageCount = usageCount + 1 WHERE id = :id")
    suspend fun incrementUsageCount(id: String)
}

/**
 * 鐢ㄦ埛璁剧疆鏁版嵁璁块棶瀵硅薄
 */
@Dao
interface UserSettingsDao {
    
    @Query("SELECT * FROM user_settings WHERE id = 'default'")
    suspend fun getSettings(): UserSettings?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: UserSettings)
    
    @Query("UPDATE user_settings SET preferredStyle = :style WHERE id = 'default'")
    suspend fun updatePreferredStyle(style: String)
    
    @Query("UPDATE user_settings SET aiProvider = :provider WHERE id = 'default'")
    suspend fun updateAiProvider(provider: String)
    
    @Query("UPDATE user_settings SET apiKey = :key WHERE id = 'default'")
    suspend fun updateApiKey(key: String?)
}

/**
 * 鏀剁泭鏁版嵁璁块棶瀵硅薄
 */
@Dao
interface EarningDao {
    
    @Query("SELECT * FROM earnings ORDER BY date DESC")
    suspend fun getAllEarnings(): List<Earning>
    
    @Query("SELECT * FROM earnings WHERE date >= :startTime AND date <= :endTime ORDER BY date DESC")
    suspend fun getEarningsByDateRange(startTime: Long, endTime: Long): List<Earning>
    
    @Query("SELECT SUM(amount) FROM earnings WHERE date >= :startTime AND date <= :endTime")
    suspend fun getTotalEarnings(startTime: Long, endTime: Long): Double?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEarning(earning: Earning)
    
    @Delete
    suspend fun deleteEarning(earning: Earning)
    
    @Query("SELECT COUNT(*) FROM earnings")
    suspend fun getEarningsCount(): Int
}
