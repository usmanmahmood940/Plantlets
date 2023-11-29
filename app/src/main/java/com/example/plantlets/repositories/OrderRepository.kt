package com.example.plantlets.repositories

import android.util.Log
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.interfaces.CustomSuccessFailureListener
import com.example.plantlets.models.Order
import com.example.plantlets.models.SellerItem
import com.example.plantlets.models.User
import com.example.plantlets.utils.Constants
import com.example.plantlets.utils.Constants.ORDER_DELIVERED
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import javax.inject.Inject

class OrderRepository  @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreRef: FirebaseFirestore,
    private val localRepository: LocalRepository,
    private val storeRepository: StoreRepository
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

    fun getStore(){
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

    suspend fun getStoreOrdersFromId(storeId: String?,orderStatus: String =ORDER_DELIVERED):List<Order>{
        val orders = firestoreRef.collection(Constants.ORDER_REFRENCE).whereEqualTo("storeId",storeId).whereEqualTo("orderStatus",orderStatus).get().await()
       return  orders.toObjects(Order::class.java).filter { it.rating != null}
    }


    fun getUserOrders(user: User) {
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
                    else {
                        ordersList = snapshotlist.toObjects(Order::class.java)
                        _ordersStateFlow.value = CustomResponse.Success(ordersList.reversed())
                    }
                }
            }
        }
        itemListener = firestoreRef.collection(Constants.ORDER_REFRENCE).whereEqualTo("customerInfo.email",user.email)
            .addSnapshotListener(valueEventListener!!)
    }

        fun removeListener() {
        itemListener?.apply {
            remove()
        }
    }

    fun getStoreOrders(storeId: String?) {
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
                    else {
                        ordersList = snapshotlist.toObjects(Order::class.java)
                        _ordersStateFlow.value = CustomResponse.Success(ordersList.reversed())
                    }
                }
            }
        }
        itemListener = firestoreRef.collection(Constants.ORDER_REFRENCE).whereEqualTo("storeId",storeId)
            .addSnapshotListener(valueEventListener!!)
    }

    suspend fun updateOrder(myOrder: Order?,orderStatus:String): CustomResponse<Order>? {
        try {
            withContext(Dispatchers.IO){
                if(orderStatus == ORDER_DELIVERED){
                    storeRepository.incrementStoreOrderCount()
                }
            }
            // Update the status of the order
            firestoreRef.collection(Constants.ORDER_REFRENCE).document(myOrder!!.orderId)
                .update("orderStatus", orderStatus)
                .await()

            // Retrieve the updated order document
            val updatedOrderDoc = firestoreRef.collection(Constants.ORDER_REFRENCE).document(myOrder.orderId).get().await()

            // Check if the document exists and convert it to an Order object
            return if (updatedOrderDoc.exists()) {
                val updatedOrder = updatedOrderDoc.toObject(Order::class.java)
                if(orderStatus == Constants.ORDER_IN_PROGRESS) {
                    withContext(Dispatchers.IO) {
                        var itemRef = firestoreRef.collection(Constants.STORE_REFRENCE)
                            .document(myOrder.storeId ?: "").collection(Constants.ITEM_REFRENCE)
                        for (cartItem in myOrder.cartItemList) {
                            val item = itemRef.document(cartItem.plantItem.id!!).get().await()
                                .toObject(SellerItem::class.java)
                            item?.stockQuantity = item?.stockQuantity?.minus(cartItem.quantity) ?: 0
                            itemRef.document(item?.id!!).set(item).await()
                        }
                    }
                }
                CustomResponse.Success(updatedOrder)
            } else {
                CustomResponse.Error("Order with ID ${myOrder.orderId} not found")
            }
        } catch (e: Exception) {
            // Handle exceptions (e.g., FirestoreException, etc.) appropriately
            return CustomResponse.Error("Error confirming order: ${e.message}")
        }
    }

    suspend fun updateRating(myOrder: Order?): CustomResponse<Order>? {
        try {
            // Update the status of the order
            withContext(Dispatchers.IO){
                myOrder?.storeId?.let { storeId ->
                    val ordersList = getStoreOrdersFromId(storeId)
                    val rating = ordersList.sumOf { it.rating?.toDouble()?:0.0 }.plus(myOrder.rating?.toDouble()?:0.0)/(ordersList.size+1)
                    storeRepository.updateStoreRating(storeId,rating)
                }
            }
            firestoreRef.collection(Constants.ORDER_REFRENCE).document(myOrder!!.orderId)
                .update("rating", myOrder.rating)
                .await()

            // Retrieve the updated order document
            val updatedOrderDoc = firestoreRef.collection(Constants.ORDER_REFRENCE).document(myOrder.orderId).get().await()

            // Check if the document exists and convert it to an Order object
            return if (updatedOrderDoc.exists()) {
                val updatedOrder = updatedOrderDoc.toObject(Order::class.java)
                CustomResponse.Success(updatedOrder)
            } else {
                CustomResponse.Error("Order with ID ${myOrder.orderId} not found")
            }
        } catch (e: Exception) {
            // Handle exceptions (e.g., FirestoreException, etc.) appropriately
            return CustomResponse.Error("Error confirming order: ${e.message}")
        }
    }
}