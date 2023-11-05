package com.example.plantlets.Manager

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.plantlets.models.CartItem
import com.example.plantlets.models.Store
import com.example.plantlets.models.User
import com.example.plantlets.repositories.LocalRepository
import com.example.plantlets.utils.Constants
import com.example.plantlets.utils.Constants.CART
import com.example.plantlets.utils.Constants.CART_COUNT
import com.example.plantlets.utils.Constants.CART_STORE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartManager(
    private val sharedPreferences: SharedPreferences,
    private val localRepository: LocalRepository
) {

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    private var cartChangeListener: CartChangeListener? = null
    private val _cartItemList = MutableLiveData<MutableList<CartItem>>(mutableListOf())

    val cartItemList : LiveData<MutableList<CartItem>>
        get()  = _cartItemList

    var store: Store? = null


    init {
        val storeString =  sharedPreferences.getString(CART_STORE, null)
        if(!storeString.isNullOrEmpty()){
             store = Gson().fromJson(storeString, Store::class.java)
        }
        val cartJson = sharedPreferences.getString(CART, null)
        if (!cartJson.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<CartItem>?>() {}.type
            _cartItemList.value = Gson().fromJson<MutableList<CartItem>>(cartJson, type)
        }
    }

    interface CartChangeListener {
        fun onCartChanged(cartCount: Int)
    }

    fun getCartCount(): Int {
        return sharedPreferences.getInt(CART_COUNT, 0)
    }

    fun addToCart(cartItem: CartItem,cartStore: Store):Boolean {
        store?.let {
            if(it.email == cartStore.email){
                if (_cartItemList.value?.contains(cartItem) == true) {
                    val existingCartItem = _cartItemList.value?.find { it == cartItem }
                    existingCartItem?.let {
                        it.quantity += cartItem.quantity
                        it.updateTotalAmount()
                    }
                } else {
                    _cartItemList.value?.add(cartItem)
                }
                updateSharedPref()
                return true
            }
            else{
                return false
            }
        }?: kotlin.run {
            store = cartStore
            val storeJson = Gson().toJson(store)
            editor.putString(CART_STORE, storeJson)
            editor.apply()
            return addToCart(cartItem,cartStore)
        }
        return false
    }

    fun removeFromCart(cartItem: CartItem) {
        _cartItemList.value?.remove(cartItem)
        updateSharedPref()
    }
    fun removeFromCart(position: Int) {
        _cartItemList.value?.removeAt(position)
        _cartItemList.value = _cartItemList.value
        updateSharedPref()
    }
    fun decreaseQuantity(cartItem: CartItem){
        if (_cartItemList.value?.contains(cartItem) == true) {
            val existingCartItem = _cartItemList.value?.find { it == cartItem }
            existingCartItem?.let {
                it.quantity -= 1
            }
        }
    }

    fun updateSharedPref() {
        if(_cartItemList.value?.isEmpty()!!){
            store = null
            editor.putString(CART_STORE, null)
            editor.apply()
        }
        _cartItemList.value = _cartItemList.value
        var quantityCount = _cartItemList.value?.sumOf { it.quantity }
        val cartJson = Gson().toJson(_cartItemList.value)
        editor.putString(CART, cartJson)
        editor.putInt(CART_COUNT, quantityCount ?: 0)
        editor.apply()
        cartChangeListener?.onCartChanged(getCartCount())
    }

    fun clearCart(){
        _cartItemList.value?.clear()
        store = null
        updateSharedPref()
    }


    fun setCartChangeListener(listener: CartChangeListener) {
        cartChangeListener = listener
    }


}
