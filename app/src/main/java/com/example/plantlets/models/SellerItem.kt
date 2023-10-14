package com.example.plantlets.models

data class SellerItem(
    var name:String="",
    var stockQuantity:Int? =null,
    var price:Double? = null,
    var details:String? = null,
    var categoryId:String?=null,
    var soldCount:Int?=null,
    var image:String?=null,
    var id:String? =null

)
