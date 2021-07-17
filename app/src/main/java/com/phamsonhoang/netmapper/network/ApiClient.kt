package com.phamsonhoang.netmapper.network

import com.phamsonhoang.netmapper.models.Submission
import io.reactivex.Completable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiClient {
    @POST("submit") fun addSubmission(@Body submission: Submission): Completable

    companion object {
        fun create(): ApiClient {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://10.0.2.2:8080/")
                .build()
            return retrofit.create(ApiClient::class.java)
        }
    }
}