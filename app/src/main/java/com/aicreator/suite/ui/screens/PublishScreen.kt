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
import com.aicreator.suite.ui.viewmodel.PublishTab
import com.aicreator.suite.ui.viewmodel.PublishUiState
import com.aicreator.suite.ui.viewmodel.PublishViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishScreen(
    viewModel: PublishViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val drafts by viewModel.drafts.collectAsState()
    val publishedContent by viewModel.publishedContent.collectAsState()
    val platformStats by viewModel.platformStats.collectAsState()
    val overallStats by viewModel.overallStats.collectAsState()
    val publishProgress by viewModel.publishProgress.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab.ordinal) {
            PublishTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { viewModel.selectTab(tab) },
                    text = {
                        Text(
                            when (tab) {
                                PublishTab.CONTENT -> "内容"
                                PublishTab.DRAFTS -> "草稿箱 (${drafts.size})"
                                PublishTab.STATS -> "数据"
                            }
                        )
                    }
                )
            }
        }

        when (uiState) {
            is PublishUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is PublishUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("错误: ${(uiState as PublishUiState.Error).message}")
                }
            }
            else -> {
                when (selectedTab) {
                    PublishTab.CONTENT -> ContentTab(publishedContent)
                    PublishTab.DRAFTS -> DraftsTab(drafts, viewModel)
                    PublishTab.STATS -> StatsTab(overallStats, platformStats)
                }
            }
        }

        // 发布进度弹窗
        publishProgress?.let { progress ->
            PublishProgressDialog(progress = progress)
        }
    }
}

@Composable
fun ContentTab(content: List<PublishedItem>) {
    if (content.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(16.dp))
                Text("暂无已发布内容")
                Text("去创作页面生成内容并发布吧", style = MaterialTheme.typography.bodySmall)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(content) { item ->
                PublishedContentCard(item)
            }
        }
    }
}

@Composable
fun PublishedContentCard(item: PublishedItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = getPlatformEmoji(item.platform),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(item.title, fontWeight = FontWeight.Bold)
                        Text(
                            formatDate(item.publishedAt),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                AssistChip(
                    onClick = {},
                    label = { Text(item.status.name) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (item.status) {
                            PublishStatus.PUBLISHED -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatChip(Icons.Default.Visibility, "${item.views}")
                StatChip(Icons.Default.Favorite, "${item.likes}")
                StatChip(Icons.Default.Comment, "${item.comments}")
                StatChip(Icons.Default.Share, "${item.shares}")
            }

            if (item.earnings > 0) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "💰 收益 ¥${item.earnings}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun StatChip(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun DraftsTab(drafts: List<ContentDraft>, viewModel: PublishViewModel) {
    var showNewDraftDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            FilledTonalButton(onClick = { showNewDraftDialog = true }) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(4.dp))
                Text("新建草稿")
            }
        }

        if (drafts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Drafts, contentDescription = null, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("草稿箱为空")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(drafts) { draft ->
                    DraftCard(
                        draft = draft,
                        onPublish = { viewModel.publishContent(draft.content, draft.title, listOf(draft.platform)) },
                        onDelete = { viewModel.deleteDraft(draft.id) }
                    )
                }
            }
        }
    }

    if (showNewDraftDialog) {
        NewDraftDialog(onDismiss = { showNewDraftDialog = false })
    }
}

@Composable
fun DraftCard(draft: ContentDraft, onPublish: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(getPlatformEmoji(draft.platform))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(draft.title, fontWeight = FontWeight.Bold)
                        Text(
                            "编辑于 ${formatTimeAgo(draft.lastModifiedAt)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "删除")
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                draft.content.take(100) + if (draft.content.length > 100) "..." else "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )

            if (draft.tags.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    draft.tags.take(3).forEach { tag ->
                        SuggestionChip(onClick = {}, label = { Text(tag) })
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(onClick = onPublish, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Send, null)
                Spacer(Modifier.width(8.dp))
                Text("发布到 ${draft.platform}")
            }
        }
    }
}

@Composable
fun NewDraftDialog(onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf("小红书") }
    val platforms = listOf("小红书", "抖音", "B站", "知乎", "公众号")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新建草稿") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("标题") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("内容") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )
                Spacer(Modifier.height(8.dp))
                Text("选择平台", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    platforms.take(3).forEach { p ->
                        FilterChip(
                            selected = platform == p,
                            onClick = { platform = p },
                            label = { Text(p) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text("创建")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun StatsTab(stats: OverallStats, platformStats: List<PlatformStats>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OverallStatsCard(stats)
        }
        item {
            Text("各平台数据", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        items(platformStats) { platform ->
            PlatformStatsCard(platform)
        }
    }
}

@Composable
fun OverallStatsCard(stats: OverallStats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("总体数据", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumn("发布", "${stats.totalPublished}", Icons.Default.Send)
                StatColumn("浏览", formatNumber(stats.totalViews), Icons.Default.Visibility)
                StatColumn("点赞", formatNumber(stats.totalLikes), Icons.Default.Favorite)
                StatColumn("收益", "¥${stats.totalEarnings}", Icons.Default.AttachMoney)
            }
        }
    }
}

@Composable
fun StatColumn(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Text(value, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun PlatformStatsCard(stats: PlatformStats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stats.icon, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(stats.platform, fontWeight = FontWeight.Bold)
                Text(
                    "${stats.publishedCount}篇 · ${formatNumber(stats.totalViews)}浏览",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "¥${stats.totalEarnings}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "互动率 ${(stats.avgEngagementRate * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun PublishProgressDialog(progress: PublishProgress) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("发布进度") },
        text = {
            Column {
                Text("正在发布到: ${progress.currentPlatform}")
                Spacer(Modifier.height(16.dp))
                LinearProgressIndicator(progress = { progress.progress })
                Spacer(Modifier.height(8.dp))
                Text("${(progress.progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {}
    )
}

private fun getPlatformEmoji(platform: String): String {
    return when (platform) {
        "小红书" -> "📕"
        "抖音" -> "🎵"
        "B站" -> "📺"
        "知乎" -> "💬"
        "公众号" -> "💚"
        else -> "📱"
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("MM-dd HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}

private fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val hours = diff / (1000 * 60 * 60)
    return if (hours < 24) "${hours}小时前" else "${hours / 24}天前"
}

private fun formatNumber(number: Int): String {
    return when {
        number >= 1000000 -> "${number / 1000000}M"
        number >= 1000 -> "${number / 1000}K"
        else -> number.toString()
    }
}
