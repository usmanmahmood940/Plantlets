package com.example.plantlets.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val plantItem: SellerItem,
    var quantity:Int,
    var totalAmount:Double
): Parcelable {

    fun updateTotalAmount() {
        totalAmount = plantItem.price!! * quantity
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CartItem
        return plantItem.id == other.plantItem.id
    }

    constructor():this(SellerItem(),0,0.0)

}