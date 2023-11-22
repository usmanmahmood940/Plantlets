package com.example.plantlets.repositories

import android.util.Log
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.models.Order
import com.example.plantlets.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRepository  @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreRef: FirebaseFirestore,
    private val localRepository: LocalRepository,
) {

    private var valueEventListener: EventListener<QuerySnapshot>? = null
    private var itemListener: ListenerRegistration? = null

    private val _ordersStateFlow =
        MutableStateFlow<CustomResponse<List<Order>>>(CustomResponse.Loading())
    val ordersStateFlow: StateFlow<CustomResponse<List<Order>>>
        get() = _ordersStateFlow
    suspend fun getOrderId():String{
        val orders =  firestoreRef.collection(Constants.ORDER_REFRENCE).get().await()
        return orders.size().toString()
    }

    fun placeOrder(order: Order){
        firestoreRef.collection(Constants.ORDER_REFRENCE).document(order.orderId).set(order).addOnCompleteListener {
            if(it.isSuccessful){
                Log.d("USMAN-TAG","order placed")
            }
            else{
                Log.d("USMAN-O","Exception : ${it.exception?.message}")
            }
        }
    }

    fun getOrders(){
        _ordersStateFlow.value = CustomResponse.Loading()
        valueEventListener = object : EventListener<QuerySnapshot> {
            override fun onEvent(snapshotlist: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    _ordersStateFlow.value =
                        CustomResponse.Error(error.message.toString())
                }
                if (snapshotlist != null) {
                    var ordersList: MutableList<Order> = mutableListOf()
                    if (snapshotlist.isEmpty)
                        _ordersStateFlow.value = CustomResponse.Success(ordersList)
                    else{
                        ordersList = snapshotlist.toObjects(Order::class.java)
                        _ordersStateFlow.value = CustomResponse.Success(ordersList)
                    }
                }
            }
        }
        itemListener = firestoreRef.collection(Constants.ORDER_REFRENCE).addSnapshotListener(valueEventListener!!)



//        firestoreRef.collection(Constants.ORDER_REFRENCE).whereEqualTo("customerInfo.email", "user@gmail.com").get().addOnSuccessListener {
//            for(snap in it){
//                val order  = snap.toObject(Order::class.java)
//                Log.d("USMAN-TAG","Order : ${order.customerInfo?.email}")
//            }
//        }
    }

    fun removeListener() {
        itemListener?.apply {
            remove()
        }
    }
}