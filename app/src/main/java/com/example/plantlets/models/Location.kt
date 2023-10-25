package com.example.plantlets.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(
    var latitude:Double?=null,
    var logitude:Double?=null
):Parcelable
