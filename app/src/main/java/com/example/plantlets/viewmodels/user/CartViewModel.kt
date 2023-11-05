package com.example.plantlets.viewmodels.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.plantlets.Manager.CartManager
import com.example.plantlets.models.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartViewModel  @Inject constructor(
    private val cartManager: CartManager
) : ViewModel() {

    val cartItemList: LiveData<MutableList<CartItem>>
        get() = cartManager.cartItemList

    fun updateCart(){
        cartManager.updateSharedPref()
    }

    fun removeItem(position:Int){
        cartManager.removeFromCart(position)
    }

    
}