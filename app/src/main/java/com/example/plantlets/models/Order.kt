package com.example.plantlets.models

import java.util.Date

data class Order(
    var orderId:String="",
    val customerInfo: User?=null,
    val customerDeliveryInfo: DeliveryInfo?=null,
    val cartItemList:List<CartItem>?=null,
    val paymentMethod: String?=null,
    val amounts: Amounts?=null,
    val orderStatus : String?=null,
    val storeId:String?=null,
    val date: Date?=null
){

}
