package com.aicreator.suite.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 分发页面
 *
 * 一键发布到多个平台
 */
@Composable
fun PublishScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "内容分发",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "选择发布平台",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 平台列表
        PlatformSelectionItem("小红书", true)
        PlatformSelectionItem("抖音", false)
        PlatformSelectionItem("B站", false)
        PlatformSelectionItem("公众号", true)
        PlatformSelectionItem("知乎", false)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* TODO: 发布 */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("一键发布到已选平台")
        }
    }
}

@Composable
fun PlatformSelectionItem(platform: String, isConnected: Boolean) {
    var isSelected by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { if (isConnected) isSelected = !isSelected }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(platform)
            Row {
                if (isConnected) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { isSelected = it }
                    )
                } else {
                    TextButton(onClick = { /* TODO: 连接平台 */ }) {
                        Text("连接账号")
                    }
                }
            }
        }
    }
}
