package com.example.plantlets.models

import com.example.plantlets.utils.Constants.ORDER_DELIVERED
import com.example.plantlets.utils.Constants.ORDER_IN_DELIVERY
import com.example.plantlets.utils.Constants.ORDER_IN_PROGRESS
import com.example.plantlets.utils.Constants.ORDER_PENDING
import com.example.plantlets.utils.Constants.ORDER_PLACED
import com.example.plantlets.utils.Constants.ORDER_PROCEED

data class OrderTracking(
    var status:String = "",
    val deliveryInfo: DeliveryInfo? = null
) {

    fun placeOrder(){
        status = ORDER_PENDING
    }

    fun proceedOrder(){
        status = ORDER_IN_PROGRESS
    }

    fun delivered(){
        status = ORDER_DELIVERED
    }
}