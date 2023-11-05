package com.example.plantlets.models

import com.example.plantlets.utils.Constants
import com.example.plantlets.utils.Constants.USER_TYPE

data class User(
    val id:String="",
    val email:String? =null,
    val type:String= USER_TYPE,
    val mobileNumber:String?=null,
    val image:String?=null
)
