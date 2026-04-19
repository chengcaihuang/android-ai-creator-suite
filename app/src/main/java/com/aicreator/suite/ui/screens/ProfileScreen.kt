package com.aicreator.suite.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 个人中心页面
 */
@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "我的",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 用户信息
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "创作者: ChengcaiHuang",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "创作天数: 30天",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "累计收益: ¥1,234.56",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 功能列表
        SettingsItem("账号设置")
        SettingsItem("收益管理")
        SettingsItem("帮助与反馈")
        SettingsItem("关于我们")

        Spacer(modifier = Modifier.weight(1f))

        // 升级Pro按钮
        Button(
            onClick = { /* TODO: 升级Pro */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("升级 Pro 版本")
        }
    }
}

@Composable
fun SettingsItem(title: String) {
    TextButton(
        onClick = { /* TODO */ },
        modifier = Modifier.fillMaxWidth()
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            androidx.compose.material3.Text(title)
            androidx.compose.material3.Text(">")
        }
    }
}
