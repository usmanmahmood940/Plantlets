package com.example.plantlets.models

import com.example.plantlets.utils.Constants.ORDER_IN_DELIVERY
import com.example.plantlets.utils.Constants.ORDER_PLACED
import com.example.plantlets.utils.Constants.ORDER_PROCEED

data class OrderTracking(
    var status:String = "",
    val deliveryInfo: DeliveryInfo? = null
) {

    fun placeOrder(){
        status = ORDER_PLACED
    }

    fun proceedOrder(){
        status = ORDER_PROCEED
    }

    fun sendForDelivery(){
        status = ORDER_IN_DELIVERY
    }
}