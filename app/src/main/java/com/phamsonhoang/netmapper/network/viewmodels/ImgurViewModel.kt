package com.phamsonhoang.netmapper.network.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phamsonhoang.netmapper.models.responses.ImageUploadResponse
import com.phamsonhoang.netmapper.network.repositories.ImgurRepository
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ImgurViewModel constructor(private val repository: ImgurRepository) : ViewModel() {
    val imageUploadResponse = MutableLiveData<ImageUploadResponse>()
    val errorMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    fun uploadImage(image: MultipartBody.Part) {
        loading.postValue(true)
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = repository.uploadImage(image)
            withContext(Dispatchers.Main + exceptionHandler) {
                if (response.isSuccessful) {
                    imageUploadResponse.postValue(response.body())
                    loading.postValue(false)
                } else {
                    onError("Error: ${response.message()}")
                }
            }
        }
    }

    private fun onError(message: String) {
        errorMessage.postValue(message)
        loading.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}