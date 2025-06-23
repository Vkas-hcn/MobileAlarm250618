package com.mobile.alarm.applications.mobilealarms.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build

class MovementDetector(
    private val context: Context,
    private val movementCallback: (Boolean) -> Unit
) : SensorEventListener {

    private val sensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    // 使用线性加速度传感器（排除重力影响）
    private val accelerometer by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    }

    // 兼容性回退方案
    private val legacyAccelerometer by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    // 阈值设置（单位 m/s²）
    private val MOVEMENT_THRESHOLD = 1.5f
    private val STABLE_THRESHOLD = 0.5f
    private val CHECK_INTERVAL = 1000L // 检测间隔（毫秒）

    private var lastMovementTime = 0L
    private var isMoving = false

    fun startDetection() {
        val sensor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && accelerometer != null) {
            accelerometer  // 优先使用线性加速度传感器
        } else {
            legacyAccelerometer  // 旧设备回退方案
        }

        sensor?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun stopDetection() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        // 限制检测频率
        if (System.currentTimeMillis() - lastMovementTime < CHECK_INTERVAL) return

        lastMovementTime = System.currentTimeMillis()

        val (x, y, z) = event.values
        val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble())

        // 检测运动状态变化
        val movementDetected = when {
            acceleration > MOVEMENT_THRESHOLD -> true
            acceleration < STABLE_THRESHOLD -> false
            else -> isMoving // 保持当前状态
        }

        // 状态变化时触发回调
        if (movementDetected != isMoving) {
            isMoving = movementDetected
            movementCallback(isMoving)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // 可处理精度变化
    }
}
