package com.aicreator.suite.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * 个人中心 ViewModel
 *
 * 管理用户设置、账号信息、统计数据
 */
@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    // UI状态
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // 用户信息
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // 统计数据
    private val _statistics = MutableStateFlow(UserStatistics())
    val statistics: StateFlow<UserStatistics> = _statistics.asStateFlow()

    // 设置
    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    // AI配置
    private val _aiConfig = MutableStateFlow(AIConfig())
    val aiConfig: StateFlow<AIConfig> = _aiConfig.asStateFlow()

    // 可用平台列表
    private val _availablePlatforms = MutableStateFlow(getDefaultPlatforms())
    val availablePlatforms: StateFlow<List<PlatformInfo>> = _availablePlatforms.asStateFlow()

    init {
        loadProfile()
    }

    /**
     * 加载用户信息
     */
    private fun loadProfile() {
        viewModelScope.launch {
            // 模拟加载
            kotlinx.coroutines.delay(500)
            _uiState.value = ProfileUiState.Success
        }
    }

    /**
     * 更新用户名
     */
    fun updateUsername(username: String) {
        viewModelScope.launch {
            _userProfile.value = _userProfile.value.copy(username = username)
        }
    }

    /**
     * 更新签名
     */
    fun updateBio(bio: String) {
        viewModelScope.launch {
            _userProfile.value = _userProfile.value.copy(bio = bio)
        }
    }

    /**
     * 更新头像
     */
    fun updateAvatar(avatarUrl: String) {
        viewModelScope.launch {
            _userProfile.value = _userProfile.value.copy(avatarUrl = avatarUrl)
        }
    }

    /**
     * 更新设置
     */
    fun updateSettings(settings: UserSettings) {
        viewModelScope.launch {
            _settings.value = settings
        }
    }

    /**
     * 更新AI配置
     */
    fun updateAIConfig(config: AIConfig) {
        viewModelScope.launch {
            _aiConfig.value = config
        }
    }

    /**
     * 设置AI提供商
     */
    fun setAIProvider(provider: AIProvider) {
        viewModelScope.launch {
            _aiConfig.value = _aiConfig.value.copy(
                provider = provider,
                baseUrl = when (provider) {
                    AIProvider.OLLAMA -> "http://localhost:11434"
                    AIProvider.OPENAI -> "https://api.openai.com/v1"
                    AIProvider.ANTHROPIC -> "https://api.anthropic.com/v1"
                    AIProvider.CUSTOM -> _aiConfig.value.baseUrl
                }
            )
        }
    }

    /**
     * 设置API密钥
     */
    fun setAPIKey(apiKey: String) {
        viewModelScope.launch {
            _aiConfig.value = _aiConfig.value.copy(apiKey = apiKey)
        }
    }

    /**
     * 连接/断开平台
     */
    fun togglePlatform(platformId: String, connected: Boolean) {
        viewModelScope.launch {
            _availablePlatforms.value = _availablePlatforms.value.map { platform ->
                if (platform.id == platformId) {
                    platform.copy(isConnected = connected)
                } else {
                    platform
                }
            }
        }
    }

    /**
     * 保存设置
     */
    fun saveSettings() {
        viewModelScope.launch {
            // 保存到本地存储
            _uiState.value = ProfileUiState.Success
        }
    }

    /**
     * 清除缓存
     */
    fun clearCache() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            // 模拟清除
            kotlinx.coroutines.delay(1000)
            _uiState.value = ProfileUiState.Success
        }
    }

    /**
     * 导出数据
     */
    fun exportData() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Exporting
            // 模拟导出
            kotlinx.coroutines.delay(2000)
            _uiState.value = ProfileUiState.Success
        }
    }

    /**
     * 重置设置
     */
    fun resetSettings() {
        viewModelScope.launch {
            _settings.value = UserSettings()
            _aiConfig.value = AIConfig()
        }
    }

    /**
     * 获取默认平台
     */
    private fun getDefaultPlatforms(): List<PlatformInfo> = listOf(
        PlatformInfo(
            id = "xiaohongshu",
            name = "小红书",
            icon = "📕",
            color = "#FF2442",
            isConnected = false,
            features = listOf("图文笔记", "短视频", "直播")
        ),
        PlatformInfo(
            id = "douyin",
            name = "抖音",
            icon = "🎵",
            color = "#00F2EA",
            isConnected = false,
            features = listOf("短视频", "直播", "商品橱窗")
        ),
        PlatformInfo(
            id = "bilibili",
            name = "B站",
            icon = "📺",
            color = "#FB7299",
            isConnected = false,
            features = listOf("长视频", "短视频", "直播")
        ),
        PlatformInfo(
            id = "zhihu",
            name = "知乎",
            icon = "💬",
            color = "#0084FF",
            isConnected = false,
            features = listOf("问答", "文章", "付费咨询")
        ),
        PlatformInfo(
            id = "wechat",
            name = "微信公众号",
            icon = "💚",
            color = "#07C160",
            isConnected = false,
            features = listOf("图文", "小程序", "广告")
        ),
        PlatformInfo(
            id = "toutiao",
            name = "今日头条",
            icon = "📰",
            color = "#F85959",
            isConnected = false,
            features = listOf("图文", "视频", "广告分成")
        )
    )
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

/**
 * 用户资料
 */
data class UserProfile(
    val id: String = UUID.randomUUID().toString(),
    val username: String = "内容创作者",
    val bio: String = "用AI辅助创作，让内容创作更高效",
    val avatarUrl: String = "",
    val email: String = "",
    val phone: String = "",
    val joinedAt: Long = System.currentTimeMillis(),
    val isPro: Boolean = false,
    val proExpiresAt: Long? = null
)

/**
 * 用户统计
 */
data class UserStatistics(
    val totalContent: Int = 0,
    val totalViews: Int = 0,
    val totalLikes: Int = 0,
    val totalEarnings: Double = 0.0,
    val streakDays: Int = 0,
    val total创作时长: Long = 0 // 分钟
)

/**
 * 用户设置
 */
data class UserSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: String = "zh-CN",
    val notificationsEnabled: Boolean = true,
    val autoSave: Boolean = true,
    val defaultPlatform: String = "小红书",
    val darkMode: Boolean = false
)

/**
 * 应用主题
 */
enum class AppTheme {
    LIGHT,      // 浅色
    DARK,       // 深色
    SYSTEM      // 跟随系统
}

/**
 * AI配置
 */
data class AIConfig(
    val provider: AIProvider = AIProvider.OLLAMA,
    val baseUrl: String = "http://localhost:11434",
    val apiKey: String = "",
    val model: String = "qwen2.5",
    val temperature: Float = 0.7f,
    val maxTokens: Int = 2000
)

/**
 * AI提供商
 */
enum class AIProvider(val displayName: String) {
    OLLAMA("Ollama (本地)"),
    OPENAI("OpenAI (GPT)"),
    ANTHROPIC("Anthropic (Claude)"),
    CUSTOM("自定义")
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
