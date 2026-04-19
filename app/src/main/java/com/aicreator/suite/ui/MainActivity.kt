package com.aicreator.suite.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.aicreator.suite.ui.theme.AICreatorSuiteTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * 主Activity
 *
 * 使用Jetpack Compose作为UI框架
 * 支持Edge-to-Edge显示
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AICreatorSuiteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AICreatorApp()
                }
            }
        }
    }
}
