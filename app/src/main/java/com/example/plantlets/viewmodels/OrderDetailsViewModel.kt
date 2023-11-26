package com.example.plantlets.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.interfaces.CustomSuccessFailureListener
import com.example.plantlets.models.Order
import com.example.plantlets.models.Store
import com.example.plantlets.models.User
import com.example.plantlets.repositories.LocalRepository
import com.example.plantlets.repositories.OrderRepository
import com.example.plantlets.repositories.StoreRepository
import com.example.plantlets.utils.Constants.ORDER_CANCELLED
import com.example.plantlets.utils.Constants.ORDER_DELIVERED
import com.example.plantlets.utils.Constants.ORDER_IN_DELIVERY
import com.example.plantlets.utils.Constants.ORDER_IN_PROGRESS
import com.example.plantlets.utils.Constants.STORE_REFRENCE_USER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.security.auth.callback.Callback


@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val orderRepository: OrderRepository,
    private val storeRepository: StoreRepository)
    : ViewModel() {

    val ordersList: StateFlow<CustomResponse<List<Order>>>
        get() = orderRepository.ordersStateFlow

    var order: MutableLiveData<CustomResponse<Order>> = MutableLiveData(CustomResponse.Loading())

    var store: MutableLiveData<CustomResponse<Store>> = MutableLiveData(CustomResponse.Loading())


    fun startObserving() {
        localRepository.getCurrentUserData()?.let {
            orderRepository.getUserOrders(it)
        }
    }

    fun stopObserving() {
        orderRepository.removeListener()
    }

    fun getStore(storeId:String){
        viewModelScope.launch {
            store.postValue(storeRepository.getStoreFromId(storeId))

        }
    }

    fun confirmOrder(tempOrder: Order?) {
        viewModelScope.launch {
          order.postValue( orderRepository.updateOrder(tempOrder,ORDER_IN_PROGRESS))
        }
    }

    fun cancelOrder(tempOrder: Order?) {
        viewModelScope.launch {
            order.postValue( orderRepository.updateOrder(tempOrder,ORDER_CANCELLED))
        }
    }

    fun deliverOrder(myOrder: Order?) {
        viewModelScope.launch {
            order.postValue( orderRepository.updateOrder(myOrder,ORDER_IN_DELIVERY))
        }
    }

    fun completeOrder(myOrder: Order?) {
        viewModelScope.launch {
            order.postValue( orderRepository.updateOrder(myOrder,ORDER_DELIVERED))
        }
    }

    fun updateRating(myOrder: Order?) {
        viewModelScope.launch {
            order.postValue( orderRepository.updateRating(myOrder))
        }
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