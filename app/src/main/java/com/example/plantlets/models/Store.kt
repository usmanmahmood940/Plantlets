package com.example.plantlets.models

import com.google.firebase.firestore.CollectionReference

data class Store(
    var email:String?=null,
    var storeName:String? = null,
    var storeAddress:String? = null,
    var storePinLocation:String?=null
)
