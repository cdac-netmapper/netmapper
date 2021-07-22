package com.phamsonhoang.netmapper.network.repositories

import com.phamsonhoang.netmapper.network.services.ImgurService
import okhttp3.MultipartBody

class ImgurRepository constructor(private val imgurService: ImgurService) {
    fun uploadImage(image: MultipartBody.Part?) = imgurService.uploadImage(image)
}