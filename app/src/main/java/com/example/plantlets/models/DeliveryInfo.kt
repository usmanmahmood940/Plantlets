package com.example.plantlets.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeliveryInfo(
    var fullAddress:String?=null,
    var pinAddress:String?=null,
    var locationLatitude:Double?=null,
    var locationLongitude:Double?=null,
    var zipcode:String?=null
):Parcelable{


}
