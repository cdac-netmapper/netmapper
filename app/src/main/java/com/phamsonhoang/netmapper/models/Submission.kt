package com.phamsonhoang.netmapper.models

import com.google.gson.annotations.SerializedName

data class Submission(
    @SerializedName("type")
    val type: String,
    @SerializedName("desc")
    val desc: String,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("long")
    val long: Double,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("submittedAt")
    val submittedAt: String
)