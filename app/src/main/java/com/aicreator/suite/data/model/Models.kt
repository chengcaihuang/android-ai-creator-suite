package com.aicreator.suite.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * 内容类型
 */
enum class ContentType {
    TEXT,       // 文案
    IMAGE,      // 图片
    VIDEO,      // 视频
    SCRIPT      // 脚本
}

/**
 * 内容状态
 */
enum class ContentStatus {
    DRAFT,      // 草稿
    PUBLISHED,  // 已发布
    ARCHIVED    // 已归档
}

/**
 * 内容实体
 */
@Entity(tableName = "contents")
data class Content(
    @PrimaryKey
    val id: String,
    val type: ContentType,
    val title: String,
    val body: String,
    val platform: String,
    val createdAt: Long,
    val status: ContentStatus,
    val tags: String = "",
    val wordCount: Int = 0,
    val thumbnailUrl: String? = null
)

/**
 * 模板
 */
@Entity(tableName = "templates")
data class Template(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val promptTemplate: String,
    val usageCount: Long = 0,
    val isPremium: Boolean = false,
    val previewImage: String? = null
)

/**
 * 用户设置
 */
@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: String = "default",
    val preferredStyle: String = "小红书",
    val defaultLength: Int = 500,
    val aiProvider: String = "ollama", // ollama, openai, claude
    val apiKey: String? = null,
    val ollamaEndpoint: String = "http://localhost:11434",
    val theme: String = "system",
    val language: String = "zh-CN"
)

/**
 * 变现记录
 */
@Entity(tableName = "earnings")
data class Earning(
    @PrimaryKey
    val id: String,
    val amount: Double,
    val source: String,
    val platform: String,
    val date: Long,
    val note: String = ""
)

// ==================== HomeScreen Types ====================

/**
 * 仪表盘用户
 */
data class DashboardUser(
    val name: String = "内容创作者",
    val avatar: String = "",
    val streak: Int = 7,
    val level: Int = 3,
    val points: Int = 1250
)

/**
 * 今日统计
 */
data class TodayStats(
    val contentCreated: Int = 3,
    val contentPublished: Int = 2,
    val views: Int = 1250,
    val earnings: Double = 58.5,
    val engagement: Float = 5.8f
)

/**
 * 快捷入口
 */
data class QuickAction(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val color: String,
    val route: String
)

/**
 * 推荐内容
 */
data class Recommendation(
    val id: String,
    val type: RecommendationType,
    val title: String,
    val description: String,
    val thumbnail: String,
    val actionLabel: String
)

/**
 * 推荐类型
 */
enum class RecommendationType {
    TUTORIAL,
    TEMPLATE,
    TOOL,
    TREND
}

/**
 * 活动记录
 */
data class Activity(
    val id: String,
    val type: ActivityType,
    val title: String,
    val timestamp: Long
)

/**
 * 活动类型
 */
enum class ActivityType {
    CONTENT_CREATED,
    CONTENT_PUBLISHED,
    CONTENT_LIKED,
    EARNINGS,
    MILESTONE
}

/**
 * 待办事项
 */
data class Todo(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM
)

/**
 * 优先级
 */
enum class Priority {
    LOW, MEDIUM, HIGH
}

/**
 * 收益数据点
 */
data class EarningsPoint(
    val date: Long,
    val amount: Double
)

/**
 * 系统公告
 */
data class Announcement(
    val id: String,
    val title: String,
    val content: String,
    val timestamp: Long,
    val type: AnnouncementType
)

/**
 * 公告类型
 */
enum class AnnouncementType {
    FEATURE, UPDATE, TIP, CAMPAIGN
}

// ==================== PublishScreen Types ====================

/**
 * 发布Tab
 */
enum class PublishTab {
    CONTENT, DRAFTS, STATS
}

/**
 * 草稿类型
 */
enum class ContentDraftType {
    TEXT, VIDEO, LIVE
}

/**
 * 内容草稿
 */
data class ContentDraft(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val platform: String,
    val contentType: ContentDraftType,
    val tags: List<String> = emptyList(),
    val coverImage: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastModifiedAt: Long = System.currentTimeMillis()
)

/**
 * 已发布内容
 */
data class PublishedItem(
    val id: String,
    val title: String,
    val content: String,
    val platform: String,
    val publishedAt: Long,
    val views: Int,
    val likes: Int,
    val comments: Int,
    val shares: Int,
    val earnings: Double,
    val status: PublishStatus
)

/**
 * 发布状态
 */
enum class PublishStatus {
    DRAFT, SCHEDULED, PUBLISHING, PUBLISHED, FAILED
}

/**
 * 平台统计
 */
data class PlatformStats(
    val platform: String,
    val icon: String = "📱",
    val color: String = "#2196F3",
    val publishedCount: Int,
    val totalViews: Int,
    val totalLikes: Int,
    val avgEngagementRate: Float,
    val totalEarnings: Double
)

/**
 * 总体统计
 */
data class OverallStats(
    val totalPublished: Int = 0,
    val totalViews: Int = 0,
    val totalLikes: Int = 0,
    val totalComments: Int = 0,
    val totalEarnings: Double = 0.0
)

/**
 * 发布进度
 */
data class PublishProgress(
    val status: PublishStatus,
    val platforms: List<String>,
    val currentPlatform: String?,
    val progress: Float
)

// ==================== MonetizeScreen Types ====================

/**
 * 变现Tab
 */
enum class MonetizeTab {
    TUTORIALS, CASE_STUDIES, EARNINGS
}

/**
 * 教程分类
 */
enum class TutorialCategory {
    PLATFORM_GUIDE,
    AI_TOOLS,
    CONTENT_CREATE,
    MARKETING,
    PRODUCT_DELIVERY
}

/**
 * 难度等级
 */
enum class Difficulty {
    BEGINNER, INTERMEDIATE, ADVANCED
}

/**
 * 教程内容
 */
data class TutorialContent(
    val steps: List<TutorialStep>,
    val tips: List<String>,
    val resources: List<String> = emptyList()
)

/**
 * 教程步骤
 */
data class TutorialStep(
    val title: String,
    val description: String
)

/**
 * 变现教程
 */
data class MonetizeTutorial(
    val id: String,
    val title: String,
    val description: String,
    val category: TutorialCategory,
    val platform: String,
    val difficulty: Difficulty,
    val duration: String,
    val icon: String,
    val content: TutorialContent,
    val isCompleted: Boolean = false,
    val progress: Float = 0f
)

/**
 * 案例分析
 */
data class CaseStudy(
    val id: String,
    val title: String,
    val author: String,
    val platform: String,
    val category: String,
    val initialFollowers: Int,
    val currentFollowers: Int,
    val monthlyIncome: Int,
    val timeline: String,
    val keyStrategies: List<String>,
    val contentSamples: List<String>
)

/**
 * 收益记录
 */
data class EarningRecord(
    val id: String = UUID.randomUUID().toString(),
    val platform: String,
    val source: String,
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)

/**
 * 收益数据点
 */
data class EarningsDataPoint(
    val date: Date,
    val amount: Double
)

// ==================== ProfileScreen Types ====================

/**
 * 用户资料
 */
data class Profile(
    val id: String = UUID.randomUUID().toString(),
    val username: String = "内容创作者",
    val bio: String = "用AI辅助创作",
    val avatarUrl: String = "",
    val email: String = "",
    val phone: String = "",
    val joinedAt: Long = System.currentTimeMillis(),
    val isPro: Boolean = false
)

/**
 * 用户统计数据
 */
data class UserStatistics(
    val totalContent: Int = 0,
    val totalViews: Int = 0,
    val totalLikes: Int = 0,
    val totalEarnings: Double = 0.0,
    val streakDays: Int = 0,
    val totalMinutes: Long = 0
)

/**
 * 应用主题
 */
enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

/**
 * AI配置
 */
data class AIConfiguration(
    val provider: AIProviderType = AIProviderType.OLLAMA,
    val baseUrl: String = "http://localhost:11434",
    val apiKey: String = "",
    val model: String = "qwen2.5",
    val temperature: Float = 0.7f,
    val maxTokens: Int = 2000
)

/**
 * AI提供商
 */
enum class AIProviderType {
    OLLAMA("Ollama (本地)"),
    OPENAI("OpenAI (GPT)"),
    ANTHROPIC("Anthropic (Claude)"),
    CUSTOM("自定义");

    constructor(description: String) {
        this.description = description
    }
    val description: String
}

/**
 * 平台信息
 */
data class PlatformInfo(
    val id: String,
    val name: String,
    val icon: String,
    val color: String,
    val isConnected: Boolean,
    val features: List<String>
)

// ==================== UI States ====================

/**
 * Home UI状态
 */
sealed class HomeUiState {
    object Loading : HomeUiState()
    object Success : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

/**
 * Publish UI状态
 */
sealed class PublishUiState {
    object Loading : PublishUiState()
    object Success : PublishUiState()
    data class Error(val message: String) : PublishUiState()
}

/**
 * Monetize UI状态
 */
sealed class MonetizeUiState {
    object Loading : MonetizeUiState()
    object Success : MonetizeUiState()
    data class Error(val message: String) : MonetizeUiState()
}

/**
 * Profile UI状态
 */
sealed class ProfileUiState {
    object Loading : ProfileUiState()
    object Success : ProfileUiState()
    object Exporting : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}
