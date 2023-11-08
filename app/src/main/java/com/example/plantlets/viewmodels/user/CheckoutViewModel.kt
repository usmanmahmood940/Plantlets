package com.example.plantlets.viewmodels.user

import androidx.lifecycle.ViewModel
import com.example.plantlets.models.Order
import com.example.plantlets.models.User
import com.example.plantlets.repositories.ItemRepository
import com.example.plantlets.repositories.LocalRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val itemRepository: ItemRepository,
    private val firebaseAuth: FirebaseAuth,

    ) : ViewModel() {

    init {
        itemRepository.getOrders()
    }
    fun getUser(): User? {
        val user = localRepository.getCurrentUserData()
        user?.name = firebaseAuth.currentUser?.displayName ?: ""
        return user

    }

    fun placeOrder(order: Order){
        itemRepository.placeOrder(order)
    }

}