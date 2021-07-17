package com.phamsonhoang.netmapper.network

import com.phamsonhoang.netmapper.models.UploadResponse
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

private const val BASE_URL = "https://api.imgur.com/"
private const val IMGUR_CLIENT_ID = "c36a40d78cc19fc"

interface ImgurApiClient {
    @Multipart
    @POST("/3/upload")
    fun uploadFile(
        @Part image: MultipartBody.Part?,
        @Part("name") name: RequestBody? = null
    ): Observable<UploadResponse>

    companion object {
        val clientBuilder = OkHttpClient.Builder()
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

        fun create(): ImgurApiClient {
            clientBuilder.addInterceptor(interceptor)
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ImgurApiClient::class.java)
        }
    }
}