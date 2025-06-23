package com.mobile.alarm.applications.mobilealarms

import android.content.Intent
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
import com.mobile.alarm.applications.mobilealarms.databinding.ActivitySuccessBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SuccessActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySuccessBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.success)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.back.setOnClickListener {
            finish()
        }
        binding.atvNext.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}