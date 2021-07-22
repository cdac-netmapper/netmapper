package com.phamsonhoang.netmapper.network.services

import com.phamsonhoang.netmapper.models.Submission
import com.phamsonhoang.netmapper.models.responses.SubmissionResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

private const val BASE_API_URL = "http://10.0.2.2:8080/"

interface ApiService {
    @POST("submit")
    suspend fun postSubmission(@Body submission: Submission): Response<SubmissionResponse>

    companion object {
        var apiService: ApiService? = null

        fun getInstance(): ApiService {
            if (apiService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                apiService = retrofit.create(ApiService::class.java)
            }
            return apiService!!
        }
    }
}