package com.mobile.alarm.applications.mobilealarms.lan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppListViewModel(private val repository: AppRepository) : ViewModel() {
    private val _appList = MutableLiveData<List<AppInfo>>()
    val appList: LiveData<List<AppInfo>> get() = _appList

    private val _filteredApps = MutableLiveData<List<AppInfo>>()
    val filteredApps: LiveData<List<AppInfo>> get() = _filteredApps

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> get() = _searchQuery

    init {
        loadApps()
    }

    private fun loadApps() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.getInstalledApps()
            }.let {
                _appList.postValue(it)
                _filteredApps.postValue(it)
            }
        }
    }

    fun filterApps(query: String? = null) {
        _searchQuery.value = query
        val filtered = if (query.isNullOrEmpty()) {
            _appList.value ?: emptyList()
        } else {
            _appList.value?.filter {
                it.name.contains(query, ignoreCase = true)
            } ?: emptyList()
        }
        _filteredApps.postValue(filtered)
    }

    fun onAppUninstalled() {
        loadApps()
    }
}