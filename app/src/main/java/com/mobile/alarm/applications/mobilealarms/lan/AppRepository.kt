package com.mobile.alarm.applications.mobilealarms.lan

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class AppRepository(private val context: Context) {
    fun getInstalledApps(): List<AppInfo> {
        val packageManager = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val launcherPackages = packageManager.queryIntentActivities(mainIntent, PackageManager.MATCH_ALL)
            .map { it.activityInfo.packageName }
            .toSet()

        return ContextCompat.getMainExecutor(context).let {
            packageManager.getInstalledApplications(PackageManager.MATCH_ALL)
                .filter {
                    it.packageName in launcherPackages &&
                            it.packageName != context.packageName
                }
                .map {
                    AppInfo(
                        packageManager.getApplicationLabel(it).toString(),
                        packageManager.getApplicationIcon(it),
                        it.packageName
                    )
                }
                .sortedBy { it.name }
        }
    }
}