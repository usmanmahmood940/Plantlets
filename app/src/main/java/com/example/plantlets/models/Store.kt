package com.example.plantlets.models

import com.example.plantlets.utils.Extensions.addDays
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import java.util.Date

data class Store(
    var email:String?=null,
    var storeName:String? = null,
    var storeAddress:String? = null,
    var storePinLocation:String?=null,
    val freeTrialDate: Timestamp = Timestamp.now(),
    var freeTrialExpireDate:Timestamp = Timestamp(Date().addDays(30)) ,
    var activeStatus:Boolean = false,
    var location: Location?=null,
    var displayImage: String?=null,
    var bannerImage:String?=null,
)
