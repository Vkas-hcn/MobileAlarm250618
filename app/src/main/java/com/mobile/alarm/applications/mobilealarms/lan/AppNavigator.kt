package com.mobile.alarm.applications.mobilealarms.lan

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.net.toUri

class AppNavigator(private val context: Context) {
    fun launchApp(packageName: String) {
        context.startActivity(
            context.packageManager.getLaunchIntentForPackage(packageName)
                ?: Intent().apply {
                    // 可以添加错误处理intent
                }
        )
    }

    fun openAppDetails(packageName: String) {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = "package:$packageName".toUri()
            context.startActivity(this)
        }
    }

    fun uninstallApp(packageName: String) {
        Intent(Intent.ACTION_DELETE).apply {
            data = "package:$packageName".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(this)
        }
    }
}