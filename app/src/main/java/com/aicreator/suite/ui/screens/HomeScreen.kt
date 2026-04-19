package com.aicreator.suite.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aicreator.suite.data.model.*
import com.aicreator.suite.ui.viewmodel.HomeViewModel

/**
 * 首页
 *
 * 快速入口 + 每日推荐 + 数据概览
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToCreate: () -> Unit = {},
    onNavigateToPublish: () -> Unit = {},
    onNavigateToMonetize: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val user by viewModel.user.collectAsState()
    val todayStats by viewModel.todayStats.collectAsState()
    val quickActions by viewModel.quickActions.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val recentActivities by viewModel.recentActivities.collectAsState()
    val todos by viewModel.todos.collectAsState()
    val announcements by viewModel.announcements.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 欢迎区域
        item {
            WelcomeSection(user = user, streak = user.streak)
        }

        // 系统公告
        if (announcements.isNotEmpty()) {
            item {
                AnnouncementBanner(announcements.first())
            }
        }

        // 今日数据卡片
        item {
            TodayStatsCard(stats = todayStats)
        }

        // 快捷入口
        item {
            Text(
                text = "快捷入口",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            QuickActionsGrid(
                actions = quickActions,
                onCreateClick = onNavigateToCreate,
                onPublishClick = onNavigateToPublish,
                onMonetizeClick = onNavigateToMonetize
            )
        }

        // 待办事项
        if (todos.isNotEmpty()) {
            item {
                TodosSection(
                    todos = todos,
                    onToggle = { viewModel.toggleTodo(it) },
                    onDelete = { viewModel.deleteTodo(it) }
                )
            }
        }

        // 近期活动
        item {
            RecentActivitiesSection(activities = recentActivities)
        }

        // 推荐内容
        item {
            Text(
                text = "今日推荐",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            RecommendationsRow(recommendations = recommendations)
        }

        // 底部间距
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun WelcomeSection(user: DashboardUser, streak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "你好，${user.name}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "持续创作第 $streak 天 💪",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Lv.${user.level}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "${user.points} 积分",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun AnnouncementBanner(announcement: Announcement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        onClick = { /* 查看公告详情 */ }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (announcement.type) {
                    AnnouncementType.FEATURE -> Icons.Default.AutoAwesome
                    AnnouncementType.UPDATE -> Icons.Default.Update
                    AnnouncementType.TIP -> Icons.Default.Lightbulb
                    AnnouncementType.CAMPAIGN -> Icons.Default.Campaign
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = announcement.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = announcement.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
                    maxLines = 1
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
fun TodayStatsCard(stats: TodayStats) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "今日数据",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "📅 ${java.text.SimpleDateFormat("MM/dd", java.util.Locale.getDefault()).format(java.util.Date())}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.Edit,
                    value = stats.contentCreated.toString(),
                    label = "创作",
                    color = MaterialTheme.colorScheme.primary
                )
                StatItem(
                    icon = Icons.Default.Send,
                    value = stats.contentPublished.toString(),
                    label = "发布",
                    color = MaterialTheme.colorScheme.secondary
                )
                StatItem(
                    icon = Icons.Default.Visibility,
                    value = formatNumber(stats.views),
                    label = "浏览",
                    color = MaterialTheme.colorScheme.tertiary
                )
                StatItem(
                    icon = Icons.Default.AttachMoney,
                    value = "¥${stats.earnings}",
                    label = "收益",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun QuickActionsGrid(
    actions: List<QuickAction>,
    onCreateClick: () -> Unit,
    onPublishClick: () -> Unit,
    onMonetizeClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            actions.take(2).forEach { action ->
                QuickActionCard(
                    action = action,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        when (action.id) {
                            "create_text", "create_image" -> onCreateClick()
                            "publish" -> onPublishClick()
                            "monetize" -> onMonetizeClick()
                        }
                    }
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            actions.drop(2).take(2).forEach { action ->
                QuickActionCard(
                    action = action,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        when (action.id) {
                            "create_text", "create_image" -> onCreateClick()
                            "publish" -> onPublishClick()
                            "monetize" -> onMonetizeClick()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun QuickActionCard(
    action: QuickAction,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(100.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = when (action.icon) {
                    "edit" -> Icons.Default.Edit
                    "image" -> Icons.Default.Image
                    "share" -> Icons.Default.Share
                    "school" -> Icons.Default.School
                    else -> Icons.Default.AutoAwesome
                },
                contentDescription = action.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = action.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = action.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TodosSection(
    todos: List<Todo>,
    onToggle: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "待办事项",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))

            todos.take(3).forEach { todo ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = todo.isCompleted,
                        onCheckedChange = { onToggle(todo.id) }
                    )
                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    PriorityBadge(priority = todo.priority)
                }
            }
        }
    }
}

@Composable
fun PriorityBadge(priority: Priority) {
    val (color, text) = when (priority) {
        Priority.HIGH -> MaterialTheme.colorScheme.error to "高"
        Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary to "中"
        Priority.LOW -> MaterialTheme.colorScheme.outline to "低"
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun RecentActivitiesSection(activities: List<Activity>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "近期活动",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))

            activities.take(3).forEach { activity ->
                ActivityItem(activity = activity)
            }
        }
    }
}

@Composable
fun ActivityItem(activity: Activity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = when (activity.type) {
                ActivityType.CONTENT_CREATED -> Icons.Default.Edit
                ActivityType.CONTENT_PUBLISHED -> Icons.Default.Send
                ActivityType.CONTENT_LIKED -> Icons.Default.Favorite
                ActivityType.EARNINGS -> Icons.Default.AttachMoney
                ActivityType.MILESTONE -> Icons.Default.EmojiEvents
            },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = formatTimeAgo(activity.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RecommendationsRow(recommendations: List<Recommendation>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(recommendations) { recommendation ->
            RecommendationCard(recommendation = recommendation)
        }
    }
}

@Composable
fun RecommendationCard(recommendation: Recommendation) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(140.dp),
        onClick = { /* 打开推荐 */ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = when (recommendation.type) {
                    RecommendationType.TUTORIAL -> Icons.Default.School
                    RecommendationType.TEMPLATE -> Icons.Default.Description
                    RecommendationType.TOOL -> Icons.Default.Build
                    RecommendationType.TREND -> Icons.Default.TrendingUp
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = recommendation.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = recommendation.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
            TextButton(
                onClick = { },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(recommendation.actionLabel)
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private fun formatNumber(number: Int): String {
    return when {
        number >= 1000000 -> "${number / 1000000}M"
        number >= 1000 -> "${number / 1000}K"
        else -> number.toString()
    }
}

private fun formatTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = diff / (1000 * 60)
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 60 -> "${minutes}分钟前"
        hours < 24 -> "${hours}小时前"
        days < 7 -> "${days}天前"
        else -> "${days / 7}周前"
    }
}
