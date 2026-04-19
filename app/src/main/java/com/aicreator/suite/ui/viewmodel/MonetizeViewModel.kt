package com.aicreator.suite.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicreator.suite.data.local.Earning
import com.aicreator.suite.data.model.*
import com.aicreator.suite.data.repository.AIContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * 变现学院 ViewModel
 *
 * 管理变现教程、案例分析、收益统计
 */
@HiltViewModel
class MonetizeViewModel @Inject constructor(
    private val repository: AIContentRepository
) : ViewModel() {

    // UI状态
    private val _uiState = MutableStateFlow<MonetizeUiState>(MonetizeUiState.Loading)
    val uiState: StateFlow<MonetizeUiState> = _uiState.asStateFlow()

    // 当前选中的Tab
    private val _selectedTab = MutableStateFlow(MonetizeTab.TUTORIALS)
    val selectedTab: StateFlow<MonetizeTab> = _selectedTab.asStateFlow()

    // 教程列表
    private val _tutorials = MutableStateFlow<List<MonetizeTutorial>>(getDefaultTutorials())
    val tutorials: StateFlow<List<MonetizeTutorial>> = _tutorials.asStateFlow()

    // 案例列表
    private val _caseStudies = MutableStateFlow<List<CaseStudy>>(getDefaultCaseStudies())
    val caseStudies: StateFlow<List<CaseStudy>> = _caseStudies.asStateFlow()

    // 收益记录
    private val _earnings = MutableStateFlow<List<EarningRecord>>(emptyList())
    val earnings: StateFlow<List<EarningRecord>> = _earnings.asStateFlow()

    // 总收益
    private val _totalEarnings = MutableStateFlow(0.0)
    val totalEarnings: StateFlow<Double> = _totalEarnings.asStateFlow()

    // 本月收益
    private val _monthEarnings = MutableStateFlow(0.0)
    val monthEarnings: StateFlow<Double> = _monthEarnings.asStateFlow()

    // 收益趋势
    private val _earningsTrend = MutableStateFlow<List<EarningsDataPoint>>(emptyList())
    val earningsTrend: StateFlow<List<EarningsDataPoint>> = _earningsTrend.asStateFlow()

    init {
        loadData()
    }

    /**
     * 加载数据
     */
    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = MonetizeUiState.Success
            calculateEarnings()
        }
    }

    /**
     * 选择Tab
     */
    fun selectTab(tab: MonetizeTab) {
        _selectedTab.value = tab
    }

    /**
     * 添加收益记录
     */
    fun addEarning(earning: EarningRecord) {
        viewModelScope.launch {
            _earnings.value = _earnings.value + earning
            calculateEarnings()
        }
    }

    /**
     * 删除收益记录
     */
    fun deleteEarning(earningId: String) {
        viewModelScope.launch {
            _earnings.value = _earnings.value.filter { it.id != earningId }
            calculateEarnings()
        }
    }

    /**
     * 计算收益统计
     */
    private fun calculateEarnings() {
        val now = System.currentTimeMillis()
        val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)

        // 总收益
        _totalEarnings.value = _earnings.value.sumOf { it.amount }

        // 本月收益
        _monthEarnings.value = _earnings.value
            .filter { it.date >= thirtyDaysAgo }
            .sumOf { it.amount }

        // 计算趋势 (最近7天)
        _earningsTrend.value = calculateTrend()
    }

    /**
     * 计算收益趋势
     */
    private fun calculateTrend(): List<EarningsDataPoint> {
        val result = mutableListOf<EarningsDataPoint>()
        val now = System.currentTimeMillis()

        for (i in 6 downTo 0) {
            val dayStart = now - ((i + 1) * 24L * 60 * 60 * 1000)
            val dayEnd = now - (i * 24L * 60 * 60 * 1000)

            val dayEarnings = _earnings.value
                .filter { it.date in dayStart until dayEnd }
                .sumOf { it.amount }

            result.add(EarningsDataPoint(
                date = Date(dayStart),
                amount = dayEarnings
            ))
        }

        return result
    }

    /**
     * 获取默认教程
     */
    private fun getDefaultTutorials(): List<MonetizeTutorial> = listOf(
        MonetizeTutorial(
            id = "1",
            title = "小红书变现入门",
            description = "从0开始教你如何在小红书赚取第一桶金",
            category = TutorialCategory.PLATFORM_GUIDE,
            platform = "小红书",
            difficulty = Difficulty.BEGINNER,
            duration = "30分钟",
            icon = "book",
            content = TutorialContent(
                steps = listOf(
                    TutorialStep("了解平台规则", "熟悉小红书的社区规范和变现政策"),
                    TutorialStep("账号定位", "选择一个有变现潜力的细分领域"),
                    TutorialStep("内容规划", "制定可持续的内容生产计划"),
                    TutorialStep("粉丝积累", "通过优质内容吸引精准粉丝"),
                    TutorialStep("变现方式", "广告、带货、直播等多种变现路径")
                ),
                tips = listOf(
                    "坚持更新，保持账号活跃度",
                    "注重内容质量，不要盲目追热点",
                    "积极与粉丝互动，建立信任感"
                )
            )
        ),
        MonetizeTutorial(
            id = "2",
            title = "AI辅助创作技巧",
            description = "利用AI工具提升内容生产效率10倍",
            category = TutorialCategory.AI_TOOLS,
            platform = "通用",
            difficulty = Difficulty.INTERMEDIATE,
            duration = "45分钟",
            icon = "auto_awesome",
            content = TutorialContent(
                steps = listOf(
                    TutorialStep("选择合适的AI工具", "根据内容类型选择最适合的工具"),
                    TutorialStep("提示词工程", "学习如何写出高效的AI提示词"),
                    TutorialStep("内容优化", "AI生成内容的二次加工技巧"),
                    TutorialStep("效率提升", "建立AI辅助的内容生产流水线")
                ),
                tips = listOf(
                    "AI是助手，不是替代者",
                    "保持个人风格和创意",
                    "持续学习最新的AI工具和方法"
                )
            )
        ),
        MonetizeTutorial(
            id = "3",
            title = "短视频带货全攻略",
            description = "从选品到拍摄到变现的完整流程",
            category = TutorialCategory.MARKETING,
            platform = "抖音",
            difficulty = Difficulty.ADVANCED,
            duration = "60分钟",
            icon = "video_camera",
            content = TutorialContent(
                steps = listOf(
                    TutorialStep("选品策略", "如何选择高转化率的商品"),
                    TutorialStep("脚本策划", "写出让人忍不住下单的带货脚本"),
                    TutorialStep("拍摄技巧", "手机拍摄高质量带货视频"),
                    TutorialStep("剪辑发布", "剪映剪辑和发布时间技巧"),
                    TutorialStep("数据优化", "根据数据持续优化内容")
                ),
                tips = listOf(
                    "选品大于努力",
                    "真实体验最能打动人心",
                    "数据是最客观的反馈"
                )
            )
        ),
        MonetizeTutorial(
            id = "4",
            title = "私域流量变现",
            description = "把粉丝变成持续付费客户",
            category = TutorialCategory.MARKETING,
            platform = "通用",
            difficulty = Difficulty.ADVANCED,
            duration = "50分钟",
            icon = "group",
            content = TutorialContent(
                steps = listOf(
                    TutorialStep("引流策略", "如何把公域粉丝导入私域"),
                    TutorialStep("价值提供", "在私域持续提供高价值内容"),
                    TutorialStep("信任建立", "通过互动和交付建立深度信任"),
                    TutorialStep("变现设计", "设计高客单价的变现产品")
                ),
                tips = listOf(
                    "私域的核心是信任",
                    "提供超过预期的价值",
                    "建立个人品牌比卖货更重要"
                )
            )
        )
    )

    /**
     * 获取默认案例
     */
    private fun getDefaultCaseStudies(): List<CaseStudy> = listOf(
        CaseStudy(
            id = "1",
            title = "3个月从0到10万粉丝",
            author = "小红书博主小美",
            platform = "小红书",
            category = "美妆护肤",
            initialFollowers = 0,
            currentFollowers = 105000,
            monthlyIncome = 35000,
            timeline = "3个月",
            keyStrategies = listOf(
                "垂直细分领域定位",
                "AI辅助内容生产",
                "固定更新时间表",
                "积极回复评论"
            ),
            contentSamples = listOf(
                "【AI护肤】敏感肌也能用的平价好物",
                "【干货】正确护肤步骤分享",
                "【测评】5款热门精华液对比"
            )
        ),
        CaseStudy(
            id = "2",
            title = "副业月入2万的带货之路",
            author = "抖音博主老王",
            platform = "抖音",
            category = "数码科技",
            initialFollowers = 500,
            currentFollowers = 88000,
            monthlyIncome = 21000,
            timeline = "6个月",
            keyStrategies = listOf(
                "专注数码测评细分领域",
                "AI脚本生成+真人出镜",
                "选品注重性价比",
                "短视频+直播结合"
            ),
            contentSamples = listOf(
                "【开箱】iPhone 15 Pro Max上手体验",
                "【推荐】200元以内最值得买的蓝牙耳机",
                "【教程】如何用手机拍出大片感"
            )
        ),
        CaseStudy(
            id = "3",
            title = "知识付费从0到100万",
            author = "B站UP主阿杰",
            platform = "B站",
            category = "职场技能",
            initialFollowers = 1000,
            currentFollowers = 450000,
            monthlyIncome = 80000,
            timeline = "12个月",
            keyStrategies = listOf(
                "打造系统化课程体系",
                "免费内容引流+付费转化",
                "社群陪伴式交付",
                "持续更新迭代课程"
            ),
            contentSamples = listOf(
                "【合集】职场沟通技巧大全",
                "【免费课】如何做好职业规划",
                "【付费课】职场晋升必修课"
            )
        )
    )
}

/**
 * 变现UI状态
 */
sealed class MonetizeUiState {
    object Loading : MonetizeUiState()
    object Success : MonetizeUiState()
    data class Error(val message: String) : MonetizeUiState()
}

/**
 * 变现Tab
 */
enum class MonetizeTab {
    TUTORIALS,      // 教程
    CASE_STUDIES,   // 案例
    EARNINGS        // 收益
}

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
 * 教程分类
 */
enum class TutorialCategory {
    PLATFORM_GUIDE,     // 平台指南
    AI_TOOLS,          // AI工具
    CONTENT_CREATE,    // 内容创作
    MARKETING,         // 营销推广
    PRODUCT_DELIVERY   // 产品交付
}

/**
 * 难度等级
 */
enum class Difficulty {
    BEGINNER,       // 入门
    INTERMEDIATE,   // 进阶
    ADVANCED        // 高级
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
    val source: String,         // 来源: 广告、带货、付费内容等
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)

/**
 * 收益数据点 (用于图表)
 */
data class EarningsDataPoint(
    val date: Date,
    val amount: Double
)
