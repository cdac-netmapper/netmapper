package com.phamsonhoang.netmapper.network.repositories

import com.phamsonhoang.netmapper.models.Submission
import com.phamsonhoang.netmapper.network.services.ApiService

class MainRepository constructor(private val apiService: ApiService) {
    fun postSubmission(submission: Submission) = apiService.postSubmission(submission)
}