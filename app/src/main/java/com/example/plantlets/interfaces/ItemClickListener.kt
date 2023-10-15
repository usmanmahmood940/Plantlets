package com.example.plantlets.interfaces

import com.example.plantlets.models.Category
import com.example.plantlets.models.SellerItem


interface ItemClickListener {

    fun onView(item: SellerItem)
    fun onEdit(item: SellerItem)

    fun onDelete(item: SellerItem)
}