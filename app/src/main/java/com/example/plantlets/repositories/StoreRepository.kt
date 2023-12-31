package com.example.plantlets.repositories

import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.models.Store
import com.example.plantlets.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StoreRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreRef: FirebaseFirestore,
    private val localRepository: LocalRepository,
) {
    private var databaseReference: CollectionReference? = null
    private var valueEventListener: EventListener<QuerySnapshot>? = null
    private var storeListener: ListenerRegistration? = null

    private val _storesStateFlow =
        MutableStateFlow<CustomResponse<List<Store>>>(CustomResponse.Loading())
    val storesStateFlow: StateFlow<CustomResponse<List<Store>>>
        get() = _storesStateFlow

    init {
        _storesStateFlow.value = CustomResponse.Loading()
        auth.currentUser?.let {
            databaseReference = firestoreRef.collection(Constants.STORE_REFRENCE)
        }
    }

    fun getStores() {
        _storesStateFlow.value = CustomResponse.Loading()
        valueEventListener = object : EventListener<QuerySnapshot> {
            override fun onEvent(snapshotlist: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    _storesStateFlow.value =
                        CustomResponse.Error(error.message.toString())
                }
                if (snapshotlist != null) {
                    var storeList: MutableList<Store> = mutableListOf()
                    if (snapshotlist.isEmpty)
                        _storesStateFlow.value = CustomResponse.Success(storeList)
                    else{
                        storeList = snapshotlist.toObjects(Store::class.java)
                        _storesStateFlow.value = CustomResponse.Success(storeList)
                    }


//                        for (snapshot in snapshotlist) {
//                            val item = snapshot.toObject(Store::class.java)
//                            if (item != null) {
//                                storeList.add(item)
//                            }
//                            _storesStateFlow.value = CustomResponse.Success(storeList)
//                        }
                }
            }
        }
        storeListener = databaseReference?.addSnapshotListener(valueEventListener!!)

    }

    fun removeListener() {
        storeListener?.apply {
            remove()
        }
    }

    suspend fun getStoreFromId(storeId: String): CustomResponse<Store> {
        try {
            val storeDoc = firestoreRef.collection(Constants.STORE_REFRENCE).document(storeId).get().await()

            return if (storeDoc.exists()) {
                CustomResponse.Success(storeDoc.toObject(Store::class.java))
            } else {
                CustomResponse.Error("Store with ID $storeId not found")
            }
        } catch (e: Exception) {
            // Handle exceptions (e.g., FirestoreException, etc.) appropriately
            return CustomResponse.Error("Error fetching store: ${e.message}")
        }
    }


}