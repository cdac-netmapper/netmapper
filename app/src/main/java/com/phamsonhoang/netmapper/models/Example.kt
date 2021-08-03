package com.phamsonhoang.netmapper.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Example (val type : String, val image : String, val desc : String) : Parcelable {
    override fun toString(): String = type
}