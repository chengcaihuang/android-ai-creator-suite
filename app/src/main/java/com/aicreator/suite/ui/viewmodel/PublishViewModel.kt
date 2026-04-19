package com.aicreator.suite.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicreator.suite.data.model.*
import com.aicreator.suite.data.repository.AIContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * 发布中心 ViewModel
 *
 * 管理内容发布、平台管理、数据统计
 */
@HiltViewModel
class PublishViewModel @Inject constructor(
    private val repository: AIContentRepository
) : ViewModel() {

    // UI状态
    private val _uiState = MutableStateFlow<PublishUiState>(PublishUiState.Loading)
    val uiState: StateFlow<PublishUiState> = _uiState.asStateFlow()

    // 当前选中的Tab
    private val _selectedTab = MutableStateFlow(PublishTab.CONTENT)
    val selectedTab: StateFlow<PublishTab> = _selectedTab.asStateFlow()

    // 草稿箱
    private val _drafts = MutableStateFlow<List<ContentDraft>>(getDefaultDrafts())
    val drafts: StateFlow<List<ContentDraft>> = _drafts.asStateFlow()

    // 已发布内容
    private val _publishedContent = MutableStateFlow<List<PublishedItem>>(emptyList())
    val publishedContent: StateFlow<List<PublishedItem>> = _publishedContent.asStateFlow()

    // 平台统计
    private val _platformStats = MutableStateFlow<List<PlatformStats>>(getDefaultPlatformStats())
    val platformStats: StateFlow<List<PlatformStats>> = _platformStats.asStateFlow()

    // 总体统计
    private val _overallStats = MutableStateFlow(OverallStats())
    val overallStats: StateFlow<OverallStats> = _overallStats.asStateFlow()

    // 选中的平台
    private val _selectedPlatform = MutableStateFlow<String?>(null)
    val selectedPlatform: StateFlow<String?> = _selectedPlatform.asStateFlow()

    // 当前编辑的草稿
    private val _currentDraft = MutableStateFlow<ContentDraft?>(null)
    val currentDraft: StateFlow<ContentDraft?> = _currentDraft.asStateFlow()

    // 发布进度
    private val _publishProgress = MutableStateFlow<PublishProgress?>(null)
    val publishProgress: StateFlow<PublishProgress?> = _publishProgress.asStateFlow()

    init {
        loadData()
    }

    /**
     * 加载数据
     */
    private fun loadData() {
        viewModelScope.launch {
            calculateStats()
            _uiState.value = PublishUiState.Success
        }
    }

    /**
     * 选择Tab
     */
    fun selectTab(tab: PublishTab) {
        _selectedTab.value = tab
    }

    /**
     * 选择平台
     */
    fun selectPlatform(platform: String?) {
        _selectedPlatform.value = platform
        if (platform != null) {
            filterByPlatform(platform)
        } else {
            loadData()
        }
    }

    /**
     * 按平台筛选
     */
    private fun filterByPlatform(platform: String) {
        viewModelScope.launch {
            val filteredPublished = _publishedContent.value
                .filter { it.platform == platform }
            _publishedContent.value = filteredPublished
        }
    }

    /**
     * 创建新草稿
     */
    fun createDraft(draft: ContentDraft) {
        viewModelScope.launch {
            _drafts.value = _drafts.value + draft
        }
    }

    /**
     * 更新草稿
     */
    fun updateDraft(draft: ContentDraft) {
        viewModelScope.launch {
            _drafts.value = _drafts.value.map {
                if (it.id == draft.id) draft else it
            }
        }
    }

    /**
     * 删除草稿
     */
    fun deleteDraft(draftId: String) {
        viewModelScope.launch {
            _drafts.value = _drafts.value.filter { it.id != draftId }
        }
    }

    /**
     * 发布内容
     */
    fun publishContent(
        content: String,
        title: String,
        platforms: List<String>,
        scheduleTime: Long? = null
    ) {
        viewModelScope.launch {
            _publishProgress.value = PublishProgress(
                status = PublishStatus.PUBLISHING,
                platforms = platforms,
                currentPlatform = platforms.firstOrNull(),
                progress = 0f
            )

            platforms.forEachIndexed { index, platform ->
                _publishProgress.value = _publishProgress.value?.copy(
                    currentPlatform = platform,
                    progress = (index.toFloat() / platforms.size)
                )

                // 模拟发布过程
                kotlinx.coroutines.delay(500)

                // 创建已发布记录
                val publishedItem = PublishedItem(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    content = content,
                    platform = platform,
                    publishedAt = System.currentTimeMillis(),
                    views = 0,
                    likes = 0,
                    comments = 0,
                    shares = 0,
                    earnings = 0.0,
                    status = PublishStatus.PUBLISHED
                )

                _publishedContent.value = _publishedContent.value + publishedItem
            }

            _publishProgress.value = _publishProgress.value?.copy(
                status = PublishStatus.COMPLETED,
                progress = 1f
            )

            // 从草稿箱移除
            val currentDraft = _currentDraft.value
            if (currentDraft != null) {
                deleteDraft(currentDraft.id)
            }

            // 更新统计
            calculateStats()

            // 重置状态
            kotlinx.coroutines.delay(2000)
            _publishProgress.value = null
        }
    }

    /**
     * 刷新数据
     */
    fun refreshData() {
        loadData()
    }

    /**
     * 计算统计数据
     */
    private fun calculateStats() {
        val published = _publishedContent.value

        // 总体统计
        _overallStats.value = OverallStats(
            totalPublished = published.size,
            totalViews = published.sumOf { it.views },
            totalLikes = published.sumOf { it.likes },
            totalComments = published.sumOf { it.comments },
            totalEarnings = published.sumOf { it.earnings }
        )

        // 平台统计
        val platformMap = published.groupBy { it.platform }
        _platformStats.value = platformMap.map { (platform, items) ->
            PlatformStats(
                platform = platform,
                publishedCount = items.size,
                totalViews = items.sumOf { it.views },
                totalLikes = items.sumOf { it.likes },
                avgEngagementRate = if (items.sumOf { it.views } > 0)
                    items.sumOf { it.likes + it.comments }.toFloat() / items.sumOf { it.views }
                    else 0f,
                totalEarnings = items.sumOf { it.earnings }
            )
        }
    }

    /**
     * 获取默认草稿
     */
    private fun getDefaultDrafts(): List<ContentDraft> = listOf(
        ContentDraft(
            id = "1",
            title = "AI工具推荐合集",
            content = "今天给大家推荐几款我常用的AI工具...",
            platform = "小红书",
            contentType = ContentDraftType.TEXT,
            tags = listOf("AI", "工具推荐", "效率"),
            createdAt = System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000),
            lastModifiedAt = System.currentTimeMillis() - (1 * 24 * 60 * 60 * 1000)
        ),
        ContentDraft(
            id = "2",
            title = "副业赚钱经验分享",
            content = "作为一个在副业路上摸爬滚打2年的人...",
            platform = "知乎",
            contentType = ContentDraftType.TEXT,
            tags = listOf("副业", "赚钱", "经验"),
            createdAt = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000),
            lastModifiedAt = System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000)
        )
    )

    /**
     * 获取默认平台统计
     */
    private fun getDefaultPlatformStats(): List<PlatformStats> = listOf(
        PlatformStats(
            platform = "小红书",
            icon = "📕",
            color = "#FF2442",
            publishedCount = 45,
            totalViews = 125000,
            totalLikes = 8500,
            avgEngagementRate = 6.8f,
            totalEarnings = 12500.0
        ),
        PlatformStats(
            platform = "抖音",
            icon = "🎵",
            color = "#00F2EA",
            publishedCount = 32,
            totalViews = 520000,
            totalLikes = 28000,
            avgEngagementRate = 5.4f,
            totalEarnings = 8500.0
        ),
        PlatformStats(
            platform = "B站",
            icon = "📺",
            color = "#FB7299",
            publishedCount = 18,
            totalViews = 89000,
            totalLikes = 6200,
            avgEngagementRate = 7.0f,
            totalEarnings = 5000.0
        ),
        PlatformStats(
            platform = "知乎",
            icon = "💬",
            color = "#0084FF",
            publishedCount = 25,
            totalViews = 45000,
            totalLikes = 3200,
            avgEngagementRate = 7.1f,
            totalEarnings = 3000.0
        )
    )
}

/**
 * 发布UI状态
 */
sealed class PublishUiState {
    object Loading : PublishUiState()
    object Success : PublishUiState()
    data class Error(val message: String) : PublishUiState()
}

/**
 * 发布Tab
 */
enum class PublishTab {
    CONTENT,    // 内容管理
    DRAFTS,     // 草稿箱
    STATS       // 数据统计
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
 * 草稿类型
 */
enum class ContentDraftType {
    TEXT,       // 图文
    VIDEO,      // 视频
    LIVE        // 直播
}

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
    DRAFT,          // 草稿
    SCHEDULED,      // 定时发布
    PUBLISHING,     // 发布中
    PUBLISHED,      // 已发布
    FAILED          // 发布失败
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
