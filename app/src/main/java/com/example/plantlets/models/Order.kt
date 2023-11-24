package com.example.plantlets.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
@Parcelize
data class Order(
    var orderId:String="",
    val customerInfo: User?=null,
    val customerDeliveryInfo: DeliveryInfo?=null,
    val cartItemList:List<CartItem>?=null,
    val paymentMethod: String?=null,
    val amounts: Amounts?=null,
    val orderStatus : String?=null,
    val storeId:String?=null,
    val date: String?=null
): Parcelable {

    fun getSubTotal():Double{
        return cartItemList?.sumOf { it.totalAmount } ?: 0.0
    }

    fun getTotal():Double{
        val itemTotal =  cartItemList?.sumOf { it.totalAmount } ?: 0.0
        return (itemTotal + amounts?.taxAmount!!)
    }

}

