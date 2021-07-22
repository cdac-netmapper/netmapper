package com.phamsonhoang.netmapper.network.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phamsonhoang.netmapper.models.Submission
import com.phamsonhoang.netmapper.models.responses.SubmissionResponse
import com.phamsonhoang.netmapper.network.repositories.MainRepository
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel constructor(private val repository: MainRepository) : ViewModel() {
    val submissionResponse = MutableLiveData<SubmissionResponse>()
    val errorMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    fun postSubmission(submission: Submission) {
        loading.postValue(true)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val resp = repository.postSubmission(submission)
            withContext(Dispatchers.Main) {
                if (resp.isSuccessful) {
                    submissionResponse.postValue(resp.body())
                    loading.postValue(false)
                } else {
                    onError("Error: ${resp.message()}")
                }
            }
        }
    }

    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}