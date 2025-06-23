package com.mobile.alarm.applications.mobilealarms

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.alarm.applications.mobilealarms.MainActivity
import com.mobile.alarm.applications.mobilealarms.databinding.ActivityDetailBinding
import com.mobile.alarm.applications.mobilealarms.databinding.ActivityMainBinding
import com.mobile.alarm.applications.mobilealarms.utils.AudioPlayerUtil
import com.mobile.alarm.applications.mobilealarms.utils.LaunTool
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }
    var soundAdapter: SounhAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initRecy()
        showUI()
        clickFun()
    }

    private fun clickFun() {
        binding.back.setOnClickListener {
            finish()
        }
        binding.tvApply.setOnClickListener {
            startActivity(Intent(this, SuccessActivity::class.java))
            finish()
        }
        binding.atvPlay.setOnClickListener {
            if (!AudioPlayerUtil.PermissionHelper.checkCameraPermission(this)) {
                AudioPlayerUtil.PermissionHelper.requestCameraPermission(this)
            } else {
                gengUI()
            }
        }
        binding.kgFlash.setOnClickListenerFun {
            LaunTool.isFlash = !LaunTool.isFlash
            showUI()
        }
        binding.kgVibration.setOnClickListenerFun {
            LaunTool.isVibrate = !LaunTool.isVibrate
            showUI()
        }
        binding.kgVolume.setOnClickListener {
            LaunTool.isVolume = !LaunTool.isVolume
            showUI()
        }
//        binding.kgDuration.setOnClickListener {
//            LaunTool.isDuration = !LaunTool.isDuration
//            showUI()
//        }
        binding.tv15s.setOnClickListenerFun {

            LaunTool.isLoopTime = 15 * 1000
            showUI()
            App.audioPlayerUtil.scheduleStop(LaunTool.isLoopTime){}

        }
        binding.tv30s.setOnClickListenerFun {
            LaunTool.isLoopTime = 30 * 1000
            showUI()
            App.audioPlayerUtil.scheduleStop(LaunTool.isLoopTime){}
        }
        binding.tv1m.setOnClickListenerFun {
            LaunTool.isLoopTime = 1 * 60 * 1000
            showUI()
            App.audioPlayerUtil.scheduleStop(LaunTool.isLoopTime){}
        }
        binding.tv2m.setOnClickListenerFun {
            LaunTool.isLoopTime = 2 * 60 * 1000
            showUI()
            App.audioPlayerUtil.scheduleStop(LaunTool.isLoopTime){}
        }
        binding.seekbarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.e("TAG", "onProgressChanged: $progress")
                LaunTool.volume = progress / 100f
                App.audioPlayerUtil.setVolume(LaunTool.volume)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    fun View.setOnClickListenerFun(block: () -> Unit) {
        setOnClickListener {
            if(App.audioPlayerUtil.isPlaying()){
                Toast.makeText(this@DetailActivity,"Please stop playing first",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            block()
        }
    }

    fun initRecy() {
        soundAdapter = SounhAdapter(this, LaunTool.all_icon)
        binding.recyListDetail.apply {
            layoutManager =
                LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)
            //item间距
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: android.graphics.Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.right = 26
                }
            })
            adapter = soundAdapter
            smoothScrollToPosition(LaunTool.pos)
            soundAdapter?.selectedPosition = LaunTool.pos
            soundAdapter?.notifyDataSetChanged()
        }
        soundAdapter?.setOnItemClickListener(object : SounhAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                if(App.audioPlayerUtil.isPlaying()){
                    Toast.makeText(this@DetailActivity,"Please stop playing first",Toast.LENGTH_SHORT).show()
                    return
                }
                Log.e("TAG", "onItemClick: ${position}")
                LaunTool.pos = position
                showUI()
            }
        })
    }

    fun showUI() {
        binding.aivIcon.setImageResource(LaunTool.all_icon[LaunTool.pos])
        if (LaunTool.isFlash) {
            binding.kgFlash.setImageResource(R.drawable.ic_flash_on)
        } else {
            binding.kgFlash.setImageResource(R.drawable.ic_flash_off)
        }
        if (LaunTool.isVibrate) {
            binding.kgVibration.setImageResource(R.drawable.ic_flash_on)
        } else {
            binding.kgVibration.setImageResource(R.drawable.ic_flash_off)
        }
        if (LaunTool.isVolume) {
            binding.kgVolume.setImageResource(R.drawable.ic_flash_on)
            binding.seekbarVolume.visibility = View.VISIBLE
            App.audioPlayerUtil.setVolume(LaunTool.volume)
        } else {
            binding.kgVolume.setImageResource(R.drawable.ic_flash_off)
            binding.seekbarVolume.visibility = View.GONE
            App.audioPlayerUtil.setVolume(0f)
        }
        if (LaunTool.isDuration) {
            binding.kgDuration.setImageResource(R.drawable.ic_flash_on)
        } else {
            binding.kgDuration.setImageResource(R.drawable.ic_flash_off)
        }
        when (LaunTool.isLoopTime) {
            15 * 1000L -> {
                binding.tv15s.setBackgroundResource(R.drawable.bg_time_2)
                binding.tv30s.setBackgroundResource(R.drawable.bg_time)
                binding.tv1m.setBackgroundResource(R.drawable.bg_time)
                binding.tv2m.setBackgroundResource(R.drawable.bg_time)
            }

            30 * 1000L -> {
                binding.tv15s.setBackgroundResource(R.drawable.bg_time)
                binding.tv30s.setBackgroundResource(R.drawable.bg_time_2)
                binding.tv1m.setBackgroundResource(R.drawable.bg_time)
                binding.tv2m.setBackgroundResource(R.drawable.bg_time)
            }

            1 * 60 * 1000L -> {
                binding.tv15s.setBackgroundResource(R.drawable.bg_time)
                binding.tv30s.setBackgroundResource(R.drawable.bg_time)
                binding.tv1m.setBackgroundResource(R.drawable.bg_time_2)
                binding.tv2m.setBackgroundResource(R.drawable.bg_time)
            }

            2 * 60 * 1000L -> {
                binding.tv15s.setBackgroundResource(R.drawable.bg_time)
                binding.tv30s.setBackgroundResource(R.drawable.bg_time)
                binding.tv1m.setBackgroundResource(R.drawable.bg_time)
                binding.tv2m.setBackgroundResource(R.drawable.bg_time_2)
            }
        }
    }

    fun gengUI() {
        if (!App.audioPlayerUtil.isPlaying()) {
            LaunTool.playAudio(App.audioPlayerUtil, LaunTool.all_audio[LaunTool.pos]){
                palyUi()
            }
        } else {
            App.audioPlayerUtil.stopAudio()
        }
        palyUi()

    }

    fun palyUi(){
        val iconRes =
            if (App.audioPlayerUtil.isPlaying()) R.drawable.ic_weibiaoti else R.drawable.ic_bofang
        binding.atvPlay.setCompoundDrawablesRelativeWithIntrinsicBounds(
            iconRes, 0, 0, 0
        )
        // 可选：同步更新文本
        binding.atvPlay.text = if (App.audioPlayerUtil.isPlaying()) "Pause" else "Play"
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
        App.audioPlayerUtil.release()
    }
}