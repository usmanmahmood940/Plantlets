package com.example.plantlets.viewmodels.user

import androidx.lifecycle.ViewModel
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.models.Order
import com.example.plantlets.models.Store
import com.example.plantlets.models.User
import com.example.plantlets.repositories.LocalRepository
import com.example.plantlets.repositories.OrderRepository
import com.example.plantlets.utils.Constants.STORE_REFRENCE_USER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class UserOrdersViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val orderRepository: OrderRepository, )
    : ViewModel() {

    val ordersList: StateFlow<CustomResponse<List<Order>>>
        get() = orderRepository.ordersStateFlow

    fun startObserving() {
        localRepository.getCurrentUserData()?.let {
            orderRepository.getUserOrders(it)
        }
    }

    fun stopObserving() {
        orderRepository.removeListener()
    }

//    fun setStore(store:Store?){
//        localRepository.saveStoreDataToSharedPreferences(store,STORE_REFRENCE_USER)
//    }
//    fun getStore():Store?{
//        return localRepository.getStoreFromPref(STORE_REFRENCE_USER)
//    }
//
//    fun getUserData(): User? {
//        return localRepository.getCurrentUserData()
//    }


}