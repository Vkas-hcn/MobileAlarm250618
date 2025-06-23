package com.mobile.alarm.applications.mobilealarms

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import com.mobile.alarm.applications.mobilealarms.utils.AudioPlayerUtil
import com.mobile.alarm.applications.mobilealarms.utils.LaunTool
import com.mobile.alarm.applications.mobilealarms.utils.MovementDetector
import com.mobile.alarm.applications.mobilealarms.utils.SharedPrefsUtil

class App : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
         lateinit var movementDetector: MovementDetector
        @SuppressLint("StaticFieldLeak")
         lateinit var audioPlayerUtil: AudioPlayerUtil
    }
    override fun onCreate() {
        super.onCreate()
        SharedPrefsUtil.init(this)
        audioPlayerUtil = AudioPlayerUtil.getInstance(this)
        movementDetector = MovementDetector(this) { isMoving ->
            if (isMoving) {
                // 触发移动警报
                Log.e("TAG", "onCreate:触发移动警报 ")
                if (!audioPlayerUtil.isPlaying()) {
                    LaunTool.playAudio(audioPlayerUtil, LaunTool.all_audio[LaunTool.pos]) {
                    }
                }
            }
        }
    }
}