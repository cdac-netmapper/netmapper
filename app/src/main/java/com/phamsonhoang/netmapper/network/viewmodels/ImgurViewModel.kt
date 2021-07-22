package com.phamsonhoang.netmapper.network.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phamsonhoang.netmapper.models.responses.ImageUploadResponse
import com.phamsonhoang.netmapper.network.repositories.ImgurRepository
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ImgurViewModel constructor(private val repository: ImgurRepository) : ViewModel() {
    val imageUploadResponse = MutableLiveData<ImageUploadResponse>()
    val errorMessage = MutableLiveData<String>()

    fun uploadImage(image: MultipartBody.Part) {
        val resp = repository.uploadImage(image)
        resp.enqueue(object : Callback<ImageUploadResponse> {
            override fun onResponse(
                call: Call<ImageUploadResponse>,
                response: Response<ImageUploadResponse>
            ) {
                imageUploadResponse.postValue(response.body())
            }

            override fun onFailure(call: Call<ImageUploadResponse>, t: Throwable) {
                errorMessage.postValue(t.message)
            }
        })
    }
}