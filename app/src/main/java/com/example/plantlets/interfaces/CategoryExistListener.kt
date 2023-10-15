package com.example.plantlets.interfaces

interface CategoryExistListener {

    fun onExist()

    fun onNotExist()

    fun onFailure(errorMessage:String?)
}