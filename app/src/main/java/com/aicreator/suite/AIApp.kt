package com.aicreator.suite

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * AI Creator Suite 主应用类
 * 使用 Hilt 进行依赖注入
 */
@HiltAndroidApp
class AIApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // 应用初始化逻辑
    }
}
