package com.example.plantlets.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SellerItem(
    var name:String="",
    var stockQuantity:Int? =null,
    var price:Double? = null,
    var details:String? = null,
    var categoryId:String?=null,
    var soldCount:Int?=null,
    var image:String?=null,
    var id:String? =null

): Parcelable
