package com.aicreator.suite

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * AI Creator Suite Application
 *
 * 主应用类，负责初始化Hilt依赖注入框架
 */
@HiltAndroidApp
class AICreatorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // 应用初始化逻辑
    }
}
