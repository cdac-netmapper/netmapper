package com.phamsonhoang.netmapper.network.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phamsonhoang.netmapper.network.repositories.MainRepository
import com.phamsonhoang.netmapper.network.viewmodels.MainViewModel
import java.lang.IllegalArgumentException

class MainViewModelFactory constructor(private val repository: MainRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("MainViewModel not found")
        }
    }
}