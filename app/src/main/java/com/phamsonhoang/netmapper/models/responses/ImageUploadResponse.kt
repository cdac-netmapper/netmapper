package com.phamsonhoang.netmapper.models.responses

import com.google.gson.annotations.SerializedName

data class ImageUploadResponse(
    @SerializedName("data")
    val upload: Upload,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean
)

data class Upload(
//    val accountId: Int?,
//    val accountUrl: String?,
//    val adType: Int?,
//    val adUrl: String?,
//    val animated: Boolean,
//    val bandwidth: Int,
//    val datetime: Long,
//    val deletehash: String,
//    val description: String?,
//    val favorite: Boolean,
//    val hasSound: Boolean,
    @SerializedName("height")
    val height: Int,
//    val hls: String,
//    val id: String,
//    val inGallery: Boolean,
//    val inMostViral: Boolean,
//    val isAd: Boolean,
    @SerializedName("link")
    val link: String,
//    val mp4: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("size")
    val size: Int,
//    val tags: List<String>,
//    val title: String?,
    @SerializedName("type")
    val type: String,
//    val views: Int,
    @SerializedName("width")
    val width: Int
)