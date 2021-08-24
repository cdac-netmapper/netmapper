package com.phamsonhoang.netmapper.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Submission(
    @SerializedName("device")
    val device: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("desc")
    val desc: String,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("originalImage")
    val originalImage: String,
    @SerializedName("long")
    val long: Double,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("submittedAt")
    val submittedAt: String
) : Parcelable