package com.phamsonhoang.netmapper.network.services

import com.phamsonhoang.netmapper.models.responses.ImageUploadResponse
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

private const val IMGUR_BASE_URL = "https://api.imgur.com/"
private const val IMGUR_CLIENT_ID = "c36a40d78cc19fc"

interface ImgurService {
    @Multipart
    @POST("/3/upload")
    fun uploadImage(
        @Part image: MultipartBody.Part?,
        @Part("name") name: RequestBody? = null
    ): Call<ImageUploadResponse>

    companion object {
        var imgurService: ImgurService? = null

        fun getInstance(): ImgurService {
            if (imgurService == null) {
                val interceptor = object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                        var request = chain.request()
                        val headers = request
                            .headers
                            .newBuilder()
                            .add("Client-ID", IMGUR_CLIENT_ID)
                            .build()
                        request = request.newBuilder().headers(headers).build()
                        return chain.proceed(request)
                    }
                }
                val client = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build()
                val retrofit = Retrofit.Builder()
                    .client(client)
                    .baseUrl(IMGUR_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                imgurService = retrofit.create(ImgurService::class.java)
            }
            return imgurService!!
        }
    }
}