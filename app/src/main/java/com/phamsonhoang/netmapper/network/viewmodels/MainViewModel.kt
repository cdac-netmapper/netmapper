package com.phamsonhoang.netmapper.network.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phamsonhoang.netmapper.models.Submission
import com.phamsonhoang.netmapper.models.responses.SubmissionResponse
import com.phamsonhoang.netmapper.network.repositories.MainRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel constructor(private val repository: MainRepository) : ViewModel() {
    val submissionResponse = MutableLiveData<SubmissionResponse>()
    val errorMessage = MutableLiveData<String>()

    fun postSubmission(submission: Submission) {
        val resp = repository.postSubmission(submission)
        resp.enqueue(object : Callback<SubmissionResponse> {
            override fun onResponse(
                call: Call<SubmissionResponse>,
                response: Response<SubmissionResponse>
            ) {
               submissionResponse.postValue(response.body())
            }

            override fun onFailure(call: Call<SubmissionResponse>, t: Throwable) {
                errorMessage.postValue(t.message)
            }
        })
    }
}