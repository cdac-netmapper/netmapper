package com.phamsonhoang.netmapper.network.services

import com.phamsonhoang.netmapper.models.responses.ImageUploadResponse
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

private const val IMGUR_BASE_URL = "https://api.imgur.com/"
private const val IMGUR_CLIENT_ID = "c36a40d78cc19fc"

interface ImgurService {
    @Multipart
    @Headers("Authorization: Client-ID $IMGUR_CLIENT_ID")
    @POST("3/image")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part?,
        @Part("name") name: RequestBody? = null,
        @Part("title") title: RequestBody? = null
    ): Response<ImageUploadResponse>

    companion object {
        var imgurService: ImgurService? = null

        fun getInstance(): ImgurService {
            if (imgurService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(IMGUR_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                imgurService = retrofit.create(ImgurService::class.java)
            }
            return imgurService!!
        }
    }
}