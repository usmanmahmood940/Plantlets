package com.example.plantlets.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SellerItem(
    var name:String="",
    var stockQuantity:Int? =null,
    var price:Double? = null,
    var details:String? = "",
    var categoryId:String?=null,
    var soldCount:Int?=0,
    var image:String?=null,
    var id:String? =null

): Parcelable
