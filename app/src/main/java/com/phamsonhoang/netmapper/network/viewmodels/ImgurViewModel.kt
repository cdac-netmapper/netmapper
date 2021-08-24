package com.phamsonhoang.netmapper.network.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phamsonhoang.netmapper.models.responses.ImageUploadResponse
import com.phamsonhoang.netmapper.network.repositories.ImgurRepository
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import java.lang.Exception

class ImgurViewModel constructor(private val repository: ImgurRepository) : ViewModel() {
    val imageUploadResponse = MutableLiveData<ImageUploadResponse>()
    val imagesUploadResponse = MutableLiveData<List<ImageUploadResponse>>()
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

    fun upload2Images(image1: MultipartBody.Part, image2: MultipartBody.Part) {
        loading.postValue(true)
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val task1 = async { repository.uploadImage(image1) }
                val task2 = async { repository.uploadImage(image2) }
                val data = listOf(task1.await().body()!!, task2.await().body()!!)
                imagesUploadResponse.postValue(data)
            } catch (e: Exception) {
                onError("Error: ${e.message}")
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