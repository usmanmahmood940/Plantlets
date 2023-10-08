package com.example.plantlets.interfaces

import com.example.plantlets.models.Category


interface ItemClickListener {
    fun onEdit(category: Category)

    fun onDelete(category: Category)
}