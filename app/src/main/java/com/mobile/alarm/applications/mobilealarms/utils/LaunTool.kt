package com.mobile.alarm.applications.mobilealarms.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.mobile.alarm.applications.mobilealarms.MainActivity
import com.mobile.alarm.applications.mobilealarms.R
import com.mobile.alarm.applications.mobilealarms.lan.DialogActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object LaunTool {
    fun isDefaultLauncher(context: Context): Boolean {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val packageManager = context.packageManager
        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (resolveInfo != null) {
            val defaultLauncherPackage = resolveInfo.activityInfo.packageName
            val currentPackage = context.packageName
            return defaultLauncherPackage == currentPackage
        }
        return false
    }
     fun openHomeScreenSettings(activity: MainActivity) {
        val intent = Intent()
        intent.setAction("android.settings.HOME_SETTINGS")
        if (activity.packageManager.queryIntentActivities(intent, 0).isNotEmpty()) {
            activity.startActivity(intent)
            openSystemSettings(activity)
        } else {
            Toast.makeText(activity, "The settings page cannot be opened", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openSystemSettings(activity: MainActivity) {
        activity.lifecycleScope.launch(Dispatchers.Main) {
            delay(500)
            val intent = Intent(activity, DialogActivity::class.java)
            activity.startActivity(intent)
        }
    }

    var isClickApp = false
    var isFirstKey = "nhmkbg"
    var isFirstMainKey = "nhnjmjuyj"

    var pos = 0
    var isFlash = false
    var isVibrate = false
    var isVolume = true
    var volume = 0.5f
    var isDuration = false
    var isLoopTime: Long = 15*1000L
    val all_icon = listOf(
        R.drawable.ic_alarm,
        R.drawable.ic_ambulance,
        R.drawable.ic_alarmclock,

        R.drawable.ic_doorbell,
        R.drawable.ic_gunshot,
        R.drawable.ic_explosion,

        R.drawable.ic_dog,
        R.drawable.ic_cat,
        R.drawable.ic_scream,

        R.drawable.ic_laughter,
        R.drawable.ic_car,
        R.drawable.ic_piano,

    )

    val all_txt = listOf(
        "Alarm",
        "Ambulance",
        "Alarmclock",
        "Doorbell",
        "Gunshot",
        "Explosion",
        "Dog",
        "Cat",
        "Scream",
        "Laughter",
        "Car",
        "Piano",
    )

    val all_audio = listOf(
        R.raw.sou_alarm,
        R.raw.sou_ambulance,
        R.raw.sou_alarmclock,
        R.raw.sou_doorbell,
        R.raw.sou_gunshot,
        R.raw.sou_explosion,
        R.raw.sou_dog,
        R.raw.sou_cat,
        R.raw.sou_scream,
        R.raw.sou_laughter,
        R.raw.sou_car,
        R.raw.sou_piano
    )


    fun playAudio(audioPlayerUtil: AudioPlayerUtil, resId: Int, stopFun:()->Unit) {
        val volumeValue = if (isVolume) volume else 0.0f
        audioPlayerUtil.playAudioFromResource(
            resId = resId,
            volume = volumeValue,
            durationMs = isLoopTime,
            enableVibrate = isVibrate,
            enableFlash = isFlash,
            isLoop = true
        ){
            stopFun()
        }
    }
}