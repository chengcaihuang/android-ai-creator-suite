package com.aicreator.suite.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aicreator.suite.data.model.*
import com.aicreator.suite.ui.viewmodel.ProfileUiState
import com.aicreator.suite.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val aiConfig by viewModel.aiConfig.collectAsState()
    val availablePlatforms by viewModel.availablePlatforms.collectAsState()

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showAIConfigDialog by remember { mutableStateOf(false) }

    when (uiState) {
        is ProfileUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 用户信息卡片
                item {
                    UserProfileCard(
                        profile = userProfile,
                        onEditClick = { showSettingsDialog = true }
                    )
                }

                // AI配置
                item {
                    AIConfigCard(
                        config = aiConfig,
                        onConfigureClick = { showAIConfigDialog = true }
                    )
                }

                // 已连接平台
                item {
                    Text(
                        "已连接平台",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(availablePlatforms) { platform ->
                    PlatformConnectionCard(
                        platform = platform,
                        onToggle = { viewModel.togglePlatform(platform.id, !platform.isConnected) }
                    )
                }

                // 功能设置
                item {
                    Text(
                        "功能设置",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    SettingsCard(settings = settings, viewModel = viewModel)
                }

                // 数据管理
                item {
                    Text(
                        "数据管理",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    DataManagementCard(
                        onExport = { viewModel.exportData() },
                        onClearCache = { viewModel.clearCache() },
                        onReset = { viewModel.resetSettings() }
                    )
                }

                // 底部间距
                item {
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }

    // 设置对话框
    if (showSettingsDialog) {
        EditProfileDialog(
            profile = userProfile,
            onDismiss = { showSettingsDialog = false },
            onSave = { newProfile ->
                viewModel.updateUsername(newProfile.username)
                viewModel.updateBio(newProfile.bio)
                showSettingsDialog = false
            }
        )
    }

    // AI配置对话框
    if (showAIConfigDialog) {
        AIConfigDialog(
            config = aiConfig,
            onDismiss = { showAIConfigDialog = false },
            onSave = { newConfig ->
                viewModel.updateAIConfig(newConfig)
                showAIConfigDialog = false
            },
            onProviderChange = { viewModel.setAIProvider(it) }
        )
    }
}

@Composable
fun UserProfileCard(profile: Profile, onEditClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (profile.avatarUrl.isNotEmpty()) {
                        // TODO: 加载头像图片
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    profile.username,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    profile.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (profile.isPro) {
                    Spacer(Modifier.height(4.dp))
                    AssistChip(
                        onClick = {},
                        label = { Text("Pro会员") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Star,
                                null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, "编辑资料")
            }
        }
    }
}

@Composable
fun AIConfigCard(config: AIConfiguration, onConfigureClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onConfigureClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Psychology,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("AI 配置", fontWeight = FontWeight.Bold)
                Text(
                    "${config.provider.name} · ${config.model}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    if (config.apiKey.isNotEmpty()) "API Key: 已设置" else "API Key: 未设置",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Icon(Icons.Default.ChevronRight, null)
        }
    }
}

@Composable
fun PlatformConnectionCard(platform: PlatformInfo, onToggle: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(platform.icon, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(platform.name, fontWeight = FontWeight.Bold)
                Text(
                    platform.features.joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = platform.isConnected,
                onCheckedChange = { onToggle() }
            )
        }
    }
}

@Composable
fun SettingsCard(settings: UserSettings, viewModel: ProfileViewModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(8.dp)) {
            SettingsItem(
                icon = Icons.Default.DarkMode,
                title = "深色模式",
                trailing = {
                    Switch(
                        checked = settings.darkMode,
                        onCheckedChange = {
                            viewModel.updateSettings(settings.copy(darkMode = it))
                        }
                    )
                }
            )
            HorizontalDivider()
            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "通知推送",
                trailing = {
                    Switch(
                        checked = settings.notificationsEnabled,
                        onCheckedChange = {
                            viewModel.updateSettings(settings.copy(notificationsEnabled = it))
                        }
                    )
                }
            )
            HorizontalDivider()
            SettingsItem(
                icon = Icons.Default.Save,
                title = "自动保存",
                trailing = {
                    Switch(
                        checked = settings.autoSave,
                        onCheckedChange = {
                            viewModel.updateSettings(settings.copy(autoSave = it))
                        }
                    )
                }
            )
            HorizontalDivider()
            SettingsItem(
                icon = Icons.Default.Language,
                title = "默认平台",
                trailing = {
                    Text(
                        settings.defaultPlatform,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f))
        trailing()
    }
}

@Composable
fun DataManagementCard(
    onExport: () -> Unit,
    onClearCache: () -> Unit,
    onReset: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(8.dp)) {
            SettingsItem(
                icon = Icons.Default.Upload,
                title = "导出数据",
                trailing = {
                    Icon(Icons.Default.ChevronRight, null)
                }
            )
            HorizontalDivider()
            SettingsItem(
                icon = Icons.Default.DeleteSweep,
                title = "清除缓存",
                trailing = {
                    Icon(Icons.Default.ChevronRight, null)
                }
            )
            HorizontalDivider()
            SettingsItem(
                icon = Icons.Default.RestartAlt,
                title = "重置设置",
                trailing = {
                    Icon(Icons.Default.ChevronRight, null)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    profile: Profile,
    onDismiss: () -> Unit,
    onSave: (Profile) -> Unit
) {
    var username by remember { mutableStateOf(profile.username) }
    var bio by remember { mutableStateOf(profile.bio) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑资料") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("用户名") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("个人简介") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(profile.copy(username = username, bio = bio)) }) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIConfigDialog(
    config: AIConfiguration,
    onDismiss: () -> Unit,
    onSave: (AIConfiguration) -> Unit,
    onProviderChange: (AIProviderType) -> Unit
) {
    var selectedProvider by remember { mutableStateOf(config.provider) }
    var baseUrl by remember { mutableStateOf(config.baseUrl) }
    var apiKey by remember { mutableStateOf(config.apiKey) }
    var model by remember { mutableStateOf(config.model) }
    var temperature by remember { mutableStateOf(config.temperature) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("AI 配置") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    Text("AI 提供商", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(8.dp))
                    Column {
                        AIProviderType.values().forEach { provider ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedProvider == provider,
                                    onClick = {
                                        selectedProvider = provider
                                        onProviderChange(provider)
                                    }
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(provider.description)
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = baseUrl,
                        onValueChange = { baseUrl = it },
                        label = { Text("API 地址") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = selectedProvider != AIProviderType.OLLAMA
                    )
                }

                item {
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = { Text("API Key") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Text("模型: ${model}", style = MaterialTheme.typography.bodyMedium)
                }

                item {
                    Text("Temperature: ${String.format("%.1f", temperature)}", style = MaterialTheme.typography.bodyMedium)
                    Slider(
                        value = temperature,
                        onValueChange = { temperature = it },
                        valueRange = 0f..1f,
                        steps = 9
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(config.copy(
                    provider = selectedProvider,
                    baseUrl = baseUrl,
                    apiKey = apiKey,
                    model = model,
                    temperature = temperature
                ))
            }) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
