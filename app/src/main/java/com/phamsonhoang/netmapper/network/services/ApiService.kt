package com.phamsonhoang.netmapper.network.services

import com.phamsonhoang.netmapper.models.Submission
import com.phamsonhoang.netmapper.models.responses.ExamplesListResponse
import com.phamsonhoang.netmapper.models.responses.SubmissionResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

private const val BASE_API_URL = "https://042efde226f3.ngrok.io/"

interface ApiService {
    @POST("submit")
    suspend fun postSubmission(@Body submission: Submission): Response<SubmissionResponse>

    @GET("examples")
    suspend fun getExamples(): Response<ExamplesListResponse>

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