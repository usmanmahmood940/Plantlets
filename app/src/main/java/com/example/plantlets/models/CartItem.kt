package com.example.plantlets.models

data class CartItem(
    val plantItem: SellerItem,
    var quantity:Int,
    var totalAmount:Double
) {

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