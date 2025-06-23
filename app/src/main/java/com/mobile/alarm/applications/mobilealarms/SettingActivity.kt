package com.mobile.alarm.applications.mobilealarms

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.mobile.alarm.applications.mobilealarms.MainActivity
import com.mobile.alarm.applications.mobilealarms.databinding.ActivityHowToUseBinding
import com.mobile.alarm.applications.mobilealarms.databinding.ActivityMainBinding
import com.mobile.alarm.applications.mobilealarms.databinding.ActivityOneBinding
import com.mobile.alarm.applications.mobilealarms.databinding.ActivitySettingBinding
import com.mobile.alarm.applications.mobilealarms.databinding.ActivitySuccessBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SettingActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySettingBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.setting)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.back.setOnClickListener {
            finish()
        }
        binding.conPp.setOnClickListener {
         //跳转网页
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.baidu.com"))
            startActivity(intent)
        }
    }
}