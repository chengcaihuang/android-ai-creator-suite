package com.aicreator.suite.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicreator.suite.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 首页 ViewModel
 *
 * 管理首页数据、快捷入口、推荐内容
 */
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    // UI状态
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // 用户信息
    private val _user = MutableStateFlow(DashboardUser())
    val user: StateFlow<DashboardUser> = _user.asStateFlow()

    // 今日统计
    private val _todayStats = MutableStateFlow(TodayStats())
    val todayStats: StateFlow<TodayStats> = _todayStats.asStateFlow()

    // 快捷入口
    private val _quickActions = MutableStateFlow<List<QuickAction>>(getDefaultQuickActions())
    val quickActions: StateFlow<List<QuickAction>> = _quickActions.asStateFlow()

    // 今日推荐
    private val _recommendations = MutableStateFlow<List<Recommendation>>(getDefaultRecommendations())
    val recommendations: StateFlow<List<Recommendation>> = _recommendations.asStateFlow()

    // 近期活动
    private val _recentActivities = MutableStateFlow<List<Activity>>(getDefaultActivities())
    val recentActivities: StateFlow<List<Activity>> = _recentActivities.asStateFlow()

    // 待办事项
    private val _todos = MutableStateFlow<List<Todo>>(getDefaultTodos())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    // 收益趋势
    private val _earningsTrend = MutableStateFlow(getDefaultEarningsTrend())
    val earningsTrend: StateFlow<List<EarningsPoint>> = _earningsTrend.asStateFlow()

    // 系统公告
    private val _announcements = MutableStateFlow<List<Announcement>>(getDefaultAnnouncements())
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    init {
        loadDashboard()
    }

    /**
     * 加载仪表盘数据
     */
    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            
            // 模拟加载
            kotlinx.coroutines.delay(500)
            
            _uiState.value = HomeUiState.Success
        }
    }

    /**
     * 刷新数据
     */
    fun refresh() {
        loadDashboard()
    }

    /**
     * 添加待办
     */
    fun addTodo(todo: Todo) {
        _todos.value = _todos.value + todo
    }

    /**
     * 切换待办状态
     */
    fun toggleTodo(todoId: String) {
        _todos.value = _todos.value.map { todo ->
            if (todo.id == todoId) {
                todo.copy(isCompleted = !todo.isCompleted)
            } else {
                todo
            }
        }
    }

    /**
     * 删除待办
     */
    fun deleteTodo(todoId: String) {
        _todos.value = _todos.value.filter { it.id != todoId }
    }

    /**
     * 获取默认快捷入口
     */
    private fun getDefaultQuickActions(): List<QuickAction> = listOf(
        QuickAction(
            id = "create_text",
            title = "AI文案",
            description = "生成营销文案",
            icon = "edit",
            color = "#4CAF50",
            route = "create"
        ),
        QuickAction(
            id = "create_image",
            title = "AI图片",
            description = "生成配图封面",
            icon = "image",
            color = "#2196F3",
            route = "create"
        ),
        QuickAction(
            id = "publish",
            title = "一键发布",
            description = "多平台同步",
            icon = "share",
            color = "#FF9800",
            route = "publish"
        ),
        QuickAction(
            id = "monetize",
            title = "变现学院",
            description = "学习赚钱技巧",
            icon = "school",
            color = "#E91E63",
            route = "monetize"
        )
    )

    /**
     * 获取默认推荐
     */
    private fun getDefaultRecommendations(): List<Recommendation> = listOf(
        Recommendation(
            id = "1",
            type = RecommendationType.TUTORIAL,
            title = "新手入门指南",
            description = "5分钟快速上手AI创作",
            thumbnail = "",
            actionLabel = "开始学习"
        ),
        Recommendation(
            id = "2",
            type = RecommendationType.TEMPLATE,
            title = "爆款标题模板",
            description = "20个高点击率标题公式",
            thumbnail = "",
            actionLabel = "查看模板"
        ),
        Recommendation(
            id = "3",
            type = RecommendationType.TOOL,
            title = "今日选题推荐",
            description = "根据热点推荐今日选题",
            thumbnail = "",
            actionLabel = "查看选题"
        )
    )

    /**
     * 获取默认活动
     */
    private fun getDefaultActivities(): List<Activity> = listOf(
        Activity(
            id = "1",
            type = ActivityType.CONTENT_CREATED,
            title = "生成文案「AI工具推荐」",
            timestamp = System.currentTimeMillis() - (2 * 60 * 60 * 1000)
        ),
        Activity(
            id = "2",
            type = ActivityType.CONTENT_PUBLISHED,
            title = "发布到小红书成功",
            timestamp = System.currentTimeMillis() - (5 * 60 * 60 * 1000)
        ),
        Activity(
            id = "3",
            type = ActivityType.EARNINGS,
            title = "获得收益 +¥58.00",
            timestamp = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
        )
    )

    /**
     * 获取默认待办
     */
    private fun getDefaultTodos(): List<Todo> = listOf(
        Todo(
            id = "1",
            title = "完成今日内容更新",
            isCompleted = false,
            priority = Priority.HIGH
        ),
        Todo(
            id = "2",
            title = "回复粉丝评论",
            isCompleted = false,
            priority = Priority.MEDIUM
        ),
        Todo(
            id = "3",
            title = "学习变现课程",
            isCompleted = true,
            priority = Priority.LOW
        )
    )

    /**
     * 获取默认收益趋势
     */
    private fun getDefaultEarningsTrend(): List<EarningsPoint> {
        val now = System.currentTimeMillis()
        return (0..6).map { day ->
            EarningsPoint(
                date = now - ((6 - day) * 24 * 60 * 60 * 1000),
                amount = (50..200).random().toDouble()
            )
        }
    }

    /**
     * 获取默认公告
     */
    private fun getDefaultAnnouncements(): List<Announcement> = listOf(
        Announcement(
            id = "1",
            title = "🎉 新功能上线",
            content = "AI图片生成功能已开放，快去体验吧！",
            timestamp = System.currentTimeMillis() - (2 * 60 * 60 * 1000),
            type = AnnouncementType.FEATURE
        ),
        Announcement(
            id = "2",
            title = "📚 变现学院更新",
            content = "新增「抖音带货全攻略」课程，点击学习",
            timestamp = System.currentTimeMillis() - (24 * 60 * 60 * 1000),
            type = AnnouncementType.UPDATE
        )
    )
}

/**
 * Home UI状态
 */
sealed class HomeUiState {
    object Loading : HomeUiState()
    object Success : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

/**
 * 仪表盘用户
 */
data class DashboardUser(
    val name: String = "内容创作者",
    val avatar: String = "",
    val streak: Int = 7, // 连续天数
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
    TUTORIAL,   // 教程
    TEMPLATE,   // 模板
    TOOL,       // 工具
    TREND       // 趋势
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
    CONTENT_CREATED,       // 内容创建
    CONTENT_PUBLISHED,     // 内容发布
    CONTENT_LIKED,        // 获得点赞
    EARNINGS,             // 获得收益
    MILESTONE            // 达成里程碑
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
    LOW,      // 低
    MEDIUM,   // 中
    HIGH      // 高
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
    FEATURE,      // 新功能
    UPDATE,       // 更新
    TIP,          // 技巧
    CAMPAIGN      // 活动
}
