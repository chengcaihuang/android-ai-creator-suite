package com.aicreator.suite.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 创作页面
 *
 * AI文案生成 + AI图像生成入口
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("文案生成", "图像生成", "视频脚本")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 顶部Tab
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        // 内容区域
        when (selectedTab) {
            0 -> TextGeneratorContent()
            1 -> ImageGeneratorContent()
            2 -> ScriptGeneratorContent()
        }
    }
}

@Composable
fun TextGeneratorContent() {
    var inputText by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("小红书") }
    val styles = listOf("小红书", "公众号", "知乎", "抖音", "微博")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 风格选择
        Text(
            text = "选择风格",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            styles.forEach { style ->
                FilterChip(
                    selected = selectedStyle == style,
                    onClick = { selectedStyle = style },
                    label = { Text(style) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 输入区域
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("输入主题或关键词") },
            placeholder = { Text("例如：如何提高工作效率") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            maxLines = 6
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 功能按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { /* TODO: 生成标题 */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("生成标题")
            }
            Button(
                onClick = { /* TODO: 生成正文 */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("生成正文")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 生成结果
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "生成结果",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "AI生成的文案将显示在这里...\n\n" +
                            "示例：\n" +
                            "📚 5个提高工作效率的秘诀\n\n" +
                            "你是不是经常觉得时间不够用？\n" +
                            "工作总是做不完？\n\n" +
                            "今天分享5个实用的方法...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ImageGeneratorContent() {
    var prompt by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "AI图像生成",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("描述你想要的图片") },
            placeholder = { Text("例如：一只可爱的猫咪在阳光下打盹") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: 生成图片 */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("生成图片")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 风格选择
        Text(
            text = "选择风格",
            style = MaterialTheme.typography.titleMedium
        )

        // TODO: 风格网格
    }
}

@Composable
fun ScriptGeneratorContent() {
    var topic by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "视频脚本生成",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = topic,
            onValueChange = { topic = it },
            label = { Text("视频主题") },
            placeholder = { Text("例如：3分钟学会做蛋炒饭") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: 生成脚本 */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("生成脚本")
        }
    }
}
