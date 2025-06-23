package com.mobile.alarm.applications.mobilealarms

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.mobile.alarm.applications.mobilealarms.App.Companion.audioPlayerUtil
import com.mobile.alarm.applications.mobilealarms.App.Companion.movementDetector
import com.mobile.alarm.applications.mobilealarms.databinding.ActivityMainBinding
import com.mobile.alarm.applications.mobilealarms.lan.AppListActivity
import com.mobile.alarm.applications.mobilealarms.utils.AudioPlayerUtil
import com.mobile.alarm.applications.mobilealarms.utils.LaunTool
import com.mobile.alarm.applications.mobilealarms.utils.SharedPrefsUtil


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    var soundAdapter: SounhAdapter? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        clickFun()
        initRecy()
        onBackPressedDispatcher.addCallback {
            if (binding.dialogFirst.conFirst.isVisible) {
                cheakIsHaveLauncher()
                return@addCallback
            } else {
                finish()
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun cheakIsHaveLauncher() {
        val bool = LaunTool.isDefaultLauncher(this)
        if (bool) {
            binding.dialogFirst.conFirst.isVisible = false
            binding.dialogSer.conDialogSer.isVisible = false
            if(!LaunTool.isClickApp){
                Intent(this, AppListActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(this)
                    overridePendingTransition(0, 0) // 立即重置动画资源
                }
            }
            LaunTool.isClickApp = false
        } else {
            isShowFirstIdalog()
        }

        binding.conJump.isVisible = bool
    }

    fun isShowFirstIdalog() {
        val isFirst = SharedPrefsUtil.getBoolean(LaunTool.isFirstKey, false)
        if (!isFirst) {
            binding.dialogFirst.conFirst.isVisible = true
            SharedPrefsUtil.saveBoolean(LaunTool.isFirstKey, true)
        } else {
            binding.dialogFirst.conFirst.isVisible = false
        }
        val isFirstMain = SharedPrefsUtil.getBoolean(LaunTool.isFirstMainKey, false)
        if (!isFirstMain && !binding.dialogFirst.conFirst.isVisible) {
            binding.dialogSer.conDialogSer.isVisible = true
            SharedPrefsUtil.saveBoolean(LaunTool.isFirstMainKey, true)
        } else {
            binding.dialogSer.conDialogSer.isVisible = false
        }
    }

    override fun onResume() {
        super.onResume()
        cheakIsHaveLauncher()
        soundAdapter?.updateSelectedPosition(LaunTool.pos)
    }

    fun initRecy() {
        soundAdapter = SounhAdapter(this, LaunTool.all_icon)
        binding.recyList.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 3)
            adapter = soundAdapter
        }
        soundAdapter?.setOnItemClickListener(object : SounhAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                LaunTool.pos = position
                // 跳转到 DetailActivity 并传递 position
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                startActivity(intent)
            }
        })
    }

    fun clickFun() {
        binding.appCompatImageView.setOnClickListener {
            if (!AudioPlayerUtil.PermissionHelper.checkCameraPermission(this)) {
                AudioPlayerUtil.PermissionHelper.requestCameraPermission(this)
            } else {
                gengUI()
            }
        }
        binding.imgSm.setOnClickListener {
            Intent(this, UserActivity::class.java).apply {
                startActivity(this)
            }
        }
        binding.imgSetting.setOnClickListener {
            Intent(this, SettingActivity::class.java).apply {
                startActivity(this)
            }
        }

        binding.dialogFirst.tvContinue.setOnClickListener {
            LaunTool.openHomeScreenSettings(this)
        }
        binding.dialogSer.tvSetDefault.setOnClickListener {
            LaunTool.openHomeScreenSettings(this)
        }

        binding.dialogSer.tvIgnore.setOnClickListener {
            binding.dialogSer.conDialogSer.isVisible = false
        }
        binding.conJump.setOnClickListener {
            Intent(this, AppListActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(this)
                overridePendingTransition(0, 0) // 立即重置动画资源
            }
        }
        binding.dialogFirst.conFirst.setOnClickListener {
        }
        binding.dialogSer.conDialogSer.setOnClickListener {
        }
        binding.dialogFirst.imgX.setOnClickListener {
            cheakIsHaveLauncher()
        }
    }


    fun gengUI() {
        if (!audioPlayerUtil.isPlaying()) {
            movementDetector.startDetection()
            binding.appCompatImageView.setImageResource(R.drawable.ic_start_2)
            binding.imgKg.setImageResource(R.drawable.ic_kaiguan_2)
        } else {
            movementDetector.stopDetection()
            audioPlayerUtil.stopAudio()
            binding.appCompatImageView.setImageResource(R.drawable.ic_start_1)
            binding.imgKg.setImageResource(R.drawable.ic_kaiguan)
        }
    }


    /**
     * 处理权限请求结果
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        AudioPlayerUtil.PermissionHelper.handlePermissionResult(
            requestCode, permissions, grantResults,
            onGranted = {
                gengUI()
            },
            onDenied = {
                Toast.makeText(
                    this,
                    "Camera permission is denied and the flash function cannot be used",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerUtil.release()
    }
}
