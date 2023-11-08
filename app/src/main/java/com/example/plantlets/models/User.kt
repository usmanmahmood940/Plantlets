package com.example.plantlets.models

import com.example.plantlets.utils.Constants
import com.example.plantlets.utils.Constants.USER_TYPE

data class User(
    val id:String="",
    var email:String? =null,
    val type:String= USER_TYPE,
    var mobileNumber:String?=null,
    val image:String?=null,
    var name:String=""
)
