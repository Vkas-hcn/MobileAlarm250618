package com.mobile.alarm.applications.mobilealarms.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException



/**
 * 音频播放工具类
 * 支持mp3、wav格式音频播放
 * 支持音量控制、定时播放、震动、闪光灯功能
 */
class AudioPlayerUtil private constructor(private val context: Context) {

    companion object {
        private const val TAG = "AudioPlayerUtil"

        @Volatile
        private var INSTANCE: AudioPlayerUtil? = null

        fun getInstance(context: Context): AudioPlayerUtil {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AudioPlayerUtil(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null
    private var audioManager: AudioManager? = null

    private val handler = Handler(Looper.getMainLooper())
    private var stopRunnable: Runnable? = null
    private var flashRunnable: Runnable? = null

    // 播放配置
    private var isVibrateEnabled = false
    private var isFlashEnabled = false
    private var vibrateDuration = 500L
    private var flashInterval = 500L

    init {
        initComponents()
    }

    /**
     * 初始化组件
     */
    private fun initComponents() {
        try {
            // 初始化音频管理器
            audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            // 初始化震动器
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            // 初始化相机管理器（用于闪光灯）
            cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            initFlashlight()

        } catch (e: Exception) {
            Log.e(TAG, "初始化组件失败: ${e.message}")
        }
    }

    /**
     * 初始化闪光灯
     */
    private fun initFlashlight() {
        try {
            cameraManager?.let { manager ->
                val cameraIds = manager.cameraIdList
                for (id in cameraIds) {
                    val characteristics = manager.getCameraCharacteristics(id)
                    val hasFlash = characteristics.get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE)
                    if (hasFlash == true) {
                        cameraId = id
                        break
                    }
                }
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, "初始化闪光灯失败: ${e.message}")
        }
    }


    /**
     * 播放资源文件中的音频
     * @param resId 资源ID
     * @param volume 音量大小 (0.0f - 1.0f)
     * @param durationMs 播放时长(毫秒)，0表示播放完整音频，>0表示循环播放指定时长后停止
     * @param enableVibrate 是否启用震动
     * @param enableFlash 是否启用闪光灯
     * @param isLoop 是否循环播放，当durationMs>0时，会在指定时长内循环播放
     */
    fun playAudioFromResource(
        resId: Int,
        volume: Float = 1.0f,
        durationMs: Long = 0,
        enableVibrate: Boolean = false,
        enableFlash: Boolean = false,
        isLoop: Boolean = false,
        stopFun:()->Unit
    ) {
        try {
            stopAudio()

            isVibrateEnabled = enableVibrate
            isFlashEnabled = enableFlash

            mediaPlayer = MediaPlayer.create(context, resId).apply {
                setVolume(volume.coerceIn(0.0f, 1.0f), volume.coerceIn(0.0f, 1.0f))

                // 设置循环播放
                isLooping = isLoop && durationMs > 0

                setOnCompletionListener {
                    stopEffects()
                }

                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer错误: what=$what, extra=$extra")
                    stopEffects()
                    true
                }

                start()
                startEffects()

                if (durationMs > 0) {
                    scheduleStop(durationMs){
                        stopFun()
                    }
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "播放资源音频失败: ${e.message}")
        }
    }

    /**
     * 停止音频播放
     */
    fun stopAudio() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            }
            mediaPlayer = null

            stopEffects()
            cancelScheduledStop()

        } catch (e: Exception) {
            Log.e(TAG, "停止音频失败: ${e.message}")
        }
    }

    /**
     * 暂停播放
     */
    fun pauseAudio() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                    stopEffects()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "暂停音频失败: ${e.message}")
        }
    }

    /**
     * 恢复播放
     */
    fun resumeAudio() {
        try {
            mediaPlayer?.let { player ->
                if (!player.isPlaying) {
                    player.start()
                    startEffects()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "恢复音频失败: ${e.message}")
        }
    }
    //是否正在播放
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    /**
     * 设置音量
     * @param volume 音量大小 (0.0f - 1.0f)
     */
    fun setVolume(volume: Float) {
        try {
            val vol = volume.coerceIn(0.0f, 1.0f)
            mediaPlayer?.setVolume(vol, vol)
        } catch (e: Exception) {
            Log.e(TAG, "设置音量失败: ${e.message}")
        }
    }

    /**
     * 设置震动时长
     * @param duration 震动时长(毫秒)
     */
    fun setVibrateDuration(duration: Long) {
        this.vibrateDuration = duration
    }

    /**
     * 设置闪光灯闪烁间隔
     * @param interval 闪烁间隔(毫秒)
     */
    fun setFlashInterval(interval: Long) {
        this.flashInterval = interval
    }

    /**
     * 设置循环播放
     * @param isLoop 是否循环播放
     */
    fun setLooping(isLoop: Boolean) {
        try {
            mediaPlayer?.isLooping = isLoop
        } catch (e: Exception) {
            Log.e(TAG, "设置循环播放失败: ${e.message}")
        }
    }

    /**
     * 是否循环播放
     */
    fun isLooping(): Boolean {
        return try {
            mediaPlayer?.isLooping ?: false
        } catch (e: Exception) {
            false
        }
    }
    fun isPlayingFun(): Boolean {
        return try {
            mediaPlayer?.isPlaying ?: false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 获取播放进度
     */
    fun getCurrentPosition(): Int {
        return try {
            mediaPlayer?.currentPosition ?: 0
        } catch (e: Exception) {
            0
        }
    }

    /**
     * 获取音频总时长
     */
    fun getDuration(): Int {
        return try {
            mediaPlayer?.duration ?: 0
        } catch (e: Exception) {
            0
        }
    }

    /**
     * 开始特效（震动和闪光灯）
     */
    private fun startEffects() {
        if (isVibrateEnabled) {
            startVibrate()
        }
        if (isFlashEnabled) {
            startFlash()
        }
    }

    /**
     * 停止特效
     */
    private fun stopEffects() {
        stopVibrate()
        stopFlash()
    }

    /**
     * 开始震动
     */
     fun startVibrate() {
        try {
            vibrator?.let { vib ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createWaveform(
                        longArrayOf(0, vibrateDuration, vibrateDuration),
                        0
                    )
                    vib.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vib.vibrate(longArrayOf(0, vibrateDuration, vibrateDuration), 0)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "启动震动失败: ${e.message}")
        }
    }

    /**
     * 停止震动
     */
     fun stopVibrate() {
        try {
            vibrator?.cancel()
        } catch (e: Exception) {
            Log.e(TAG, "停止震动失败: ${e.message}")
        }
    }

    /**
     * 开始闪光灯
     */
     fun startFlash() {
        if (!hasFlashPermission()) {
            Log.w(TAG, "没有相机权限，无法使用闪光灯")
            return
        }

        cameraId?.let { id ->
            flashRunnable = object : Runnable {
                private var isOn = false

                override fun run() {
                    try {
                        cameraManager?.setTorchMode(id, isOn)
                        isOn = !isOn
                        handler.postDelayed(this, flashInterval)
                    } catch (e: CameraAccessException) {
                        Log.e(TAG, "控制闪光灯失败: ${e.message}")
                    }
                }
            }
            handler.post(flashRunnable!!)
        }
    }

    /**
     * 停止闪光灯
     */
     fun stopFlash() {
        flashRunnable?.let { runnable ->
            handler.removeCallbacks(runnable)
            flashRunnable = null
        }

        try {
            cameraId?.let { id ->
                cameraManager?.setTorchMode(id, false)
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, "关闭闪光灯失败: ${e.message}")
        }
    }

    /**
     * 计划停止播放
     */
     fun scheduleStop(delayMs: Long,stopFun:()->Unit) {
        cancelScheduledStop()
        stopRunnable = Runnable {
            stopAudio()
            stopFun()
        }
        handler.postDelayed(stopRunnable!!, delayMs)
    }

    /**
     * 取消计划停止
     */
    private fun cancelScheduledStop() {
        stopRunnable?.let { runnable ->
            handler.removeCallbacks(runnable)
            stopRunnable = null
        }
    }

    /**
     * 检查是否有相机权限
     */
    private fun hasFlashPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 释放资源
     */
    fun release() {
        stopAudio()
        INSTANCE = null
    }

    /**
     * 权限请求相关方法
     */
    object PermissionHelper {
        const val CAMERA_PERMISSION_REQUEST_CODE = 1001

        /**
         * 检查相机权限
         */
        fun checkCameraPermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        }

        /**
         * 请求相机权限
         * 在Activity中调用
         */
        fun requestCameraPermission(activity: android.app.Activity) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }

        /**
         * 处理权限请求结果
         * 在Activity的onRequestPermissionsResult中调用
         */
        fun handlePermissionResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray,
            onGranted: () -> Unit,
            onDenied: () -> Unit
        ) {
            when (requestCode) {
                CAMERA_PERMISSION_REQUEST_CODE -> {
                    if (grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        onGranted()
                    } else {
                        onDenied()
                    }
                }
            }
        }
    }
}