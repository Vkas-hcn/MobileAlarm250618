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
import com.mobile.alarm.applications.mobilealarms.databinding.ActivityMainBinding
import com.mobile.alarm.applications.mobilealarms.databinding.ActivityOneBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OneActivity : AppCompatActivity() {
    private val binding by lazy { ActivityOneBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        startOneToMainActivity()
        onBackPressedDispatcher.addCallback {
        }
    }


    fun startOneToMainActivity() {
        var num = 0
        lifecycleScope.launch {
            while (true){
                num++
                binding.linOne.progress =num
                if(num>=100){
                     break
                }
                delay(20)
            }
            Intent(this@OneActivity, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }
}