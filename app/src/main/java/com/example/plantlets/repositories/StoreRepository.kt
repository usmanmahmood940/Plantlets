package com.example.plantlets.repositories

import android.net.Uri
import android.util.Log
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.models.Category
import com.example.plantlets.models.SellerItem
import com.example.plantlets.models.Store
import com.example.plantlets.utils.Constants
import com.example.plantlets.utils.Constants.ACTIVE
import com.example.plantlets.utils.Helper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
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

    fun getStores(status: String?= null) {
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

                }
            }
        }
        status?.let {  storeListener = databaseReference?.whereEqualTo("status",it)?.addSnapshotListener(valueEventListener!!) }
            ?: run { storeListener = databaseReference?.addSnapshotListener(valueEventListener!!) }

    }
    fun upsertStore(email: String?,status:String?) {

        val updatedData = mapOf(
            "status" to status
        )
        firestoreRef.collection(Constants.STORE_REFRENCE).document(email!!).update(updatedData).addOnCompleteListener {
            if(it.isSuccessful){
                Log.d("ss","aaa")
            }
            else  Log.d("ss","bbb")

        }

    }

    suspend fun updateStoreRating(storeId:String,rating:Double){
        val updatedData = mapOf(
            "totalRating" to rating
        )
        firestoreRef.collection(Constants.STORE_REFRENCE).document(storeId).update(updatedData).await()
    }

    suspend fun incrementStoreOrderCount(){
        localRepository.getStoreFromPref()?.let {
            it.totalOrders = it.totalOrders+1
            val updatedData = mapOf(
                "totalOrders" to it.totalOrders
            )
            firestoreRef.collection(Constants.STORE_REFRENCE).document(it.email!!).update(updatedData).await()
        }
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


    suspend fun changeDisplay(imageUri: Uri?) {
         localRepository.getStoreFromPref()?.email?.let {email->
             imageUri?.apply {
                 val imageUrl = uploadImage(imageUri)
                 val updatedData = mapOf(
                     "image" to imageUrl
                 )
                 databaseReference?.apply {
                     document(email).update(updatedData).await()
                 }
                 localRepository.getStoreFromPref()?.apply {
                     image = imageUrl
                     localRepository.saveStoreDataToSharedPreferences(this)
                 }
             }
         }

    }
    private suspend fun uploadImage(uri: Uri): String {
        val email = localRepository.getStoreFromPref()?.email
        val storageRef = FirebaseStorage.getInstance().reference
        val fileName = "${email ?: "display"}/${System.currentTimeMillis()}.jpg"
        val fileRef = storageRef.child(fileName)

        return try {
            val uploadTask = fileRef.putFile(uri)
            val taskSnapshot = uploadTask.await() // Wait for the upload to complete

            val downloadUri = fileRef.downloadUrl.await() // Wait for the URL to be available
            downloadUri.toString() // Return the URL as a String
        } catch (e: Exception) {
            Log.e("USMAN-TAG", "Image upload failed: ${e.message}")
            // Handle thsafe error or return a default URL
            ""
        }
    }


}