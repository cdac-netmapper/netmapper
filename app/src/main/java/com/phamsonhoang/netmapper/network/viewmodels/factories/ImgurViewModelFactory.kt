package com.phamsonhoang.netmapper.network.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phamsonhoang.netmapper.network.repositories.ImgurRepository
import com.phamsonhoang.netmapper.network.viewmodels.ImgurViewModel
import java.lang.IllegalArgumentException

class ImgurViewModelFactory constructor(private val repository: ImgurRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ImgurViewModel::class.java)) {
            ImgurViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ImgurViewModel not found")
        }
    }
}