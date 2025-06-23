package com.mobile.alarm.applications.mobilealarms.lan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.mobile.alarm.applications.mobilealarms.R
import com.mobile.alarm.applications.mobilealarms.databinding.ActivityLaunchBinding
import com.mobile.alarm.applications.mobilealarms.utils.LaunTool

class AppListActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLaunchBinding.inflate(layoutInflater) }
    private lateinit var appAdapter: AppListAdapter
    private lateinit var navigator: AppNavigator
    private lateinit var viewModel: AppListViewModel

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_PACKAGE_REMOVED) {
                viewModel.onAppUninstalled()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupViewModel()
        setupRecyclerView()
        setupObservers()
        registerReceivers()
        setupClickListeners()
    }

    private fun setupViewModel() {
        val repository = AppRepository(this)
        val factory = AppListViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AppListViewModel::class.java]
        navigator = AppNavigator(this)
        LaunTool.isClickApp = false
    }

    private fun setupRecyclerView() {
        appAdapter = AppListAdapter(
            onClick = { appInfo -> navigator.launchApp(appInfo.packageName) },
            onLongClick = { appInfo -> showAppOptionsDialog(appInfo) }
        )

        binding.rvAppList.apply {
            layoutManager = GridLayoutManager(this@AppListActivity, 4)
            adapter = appAdapter
        }
    }



    private fun setupObservers() {
        viewModel.filteredApps.observe(this) {
            appAdapter.submitList(it)
        }
    }

    private fun registerReceivers() {
        registerReceiver(packageReceiver, IntentFilter(Intent.ACTION_PACKAGE_REMOVED).apply {
            addDataScheme("package")
        })
    }

    private fun setupClickListeners() {
        onBackPressedDispatcher.addCallback {
        }
        binding.iconIcon.setOnClickListener {
            LaunTool.isClickApp = true
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun showAppOptionsDialog(appInfo: AppInfo) {
        val options = arrayOf("Uninstall the app", "See the application details")
        AlertDialog.Builder(this)
            .setTitle(appInfo.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> navigator.uninstallApp(appInfo.packageName)
                    1 -> navigator.openAppDetails(appInfo.packageName)
                }
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(packageReceiver)
    }
}