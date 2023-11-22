package com.example.plantlets.repositories

import android.net.Uri
import android.util.Log
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.interfaces.CategoryExistListener
import com.example.plantlets.models.Order
import com.example.plantlets.models.SellerItem
import com.example.plantlets.utils.Constants
import com.example.plantlets.utils.Constants.ITEM_REFRENCE
import com.example.plantlets.utils.Constants.ORDER_REFRENCE
import com.example.plantlets.utils.Constants.STORE_REFRENCE
import com.example.plantlets.utils.Constants.STORE_REFRENCE_USER
import com.example.plantlets.utils.Constants.VENDOR_TYPE
import com.example.plantlets.utils.Helper.generateRandomStringWithTime
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ItemRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreRef: FirebaseFirestore,
    private val localRepository: LocalRepository,
) {

    private var databaseReference: CollectionReference? = null
    private var valueEventListener: EventListener<QuerySnapshot>? = null
    private var itemListener: ListenerRegistration? = null

    private val _itemsStateFlow =
        MutableStateFlow<CustomResponse<List<SellerItem>>>(CustomResponse.Loading())
    val itemsStateFlow: StateFlow<CustomResponse<List<SellerItem>>>
        get() = _itemsStateFlow

    init {
        _itemsStateFlow.value = CustomResponse.Loading()
        localRepository.getCurrentUserData()?.let {user ->
            if(user.type == Constants.USER_TYPE){
                localRepository.getStoreFromPref(STORE_REFRENCE_USER)?.apply {
                    databaseReference = firestoreRef.collection(STORE_REFRENCE).document(email!!)
                        .collection(ITEM_REFRENCE)
                    return@let
                }
            }
            else if(user.type == VENDOR_TYPE){
                localRepository.getStoreFromPref()?.apply {
                    databaseReference = firestoreRef.collection(STORE_REFRENCE).document(email!!)
                        .collection(ITEM_REFRENCE)
                    return@let
                }
            }

        }
    }

    fun getItems() {
        _itemsStateFlow.value = CustomResponse.Loading()
        valueEventListener = object : EventListener<QuerySnapshot> {
            override fun onEvent(snapshotlist: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    _itemsStateFlow.value =
                        CustomResponse.Error(error.message.toString())
                }
                if (snapshotlist != null) {
                    var itemList: MutableList<SellerItem> = mutableListOf()
                    if (snapshotlist.isEmpty)
                        _itemsStateFlow.value = CustomResponse.Success(itemList)
                    else{
                        itemList = snapshotlist.toObjects(SellerItem::class.java)
                        _itemsStateFlow.value = CustomResponse.Success(itemList)
                    }
//                        for (snapshot in snapshotlist) {
//                            val item = snapshot.toObject(SellerItem::class.java)
//                            if (item != null) {
//                                itemList.add(item)
//                            }
//                            _itemsStateFlow.value = CustomResponse.Success(itemList)
//                        }
                }
            }
        }
        itemListener = databaseReference?.addSnapshotListener(valueEventListener!!)

    }

    fun removeListener() {
        itemListener?.apply {
            remove()
        }
    }


    suspend fun upsertItem(item: SellerItem, imageUri: Uri?) {
        imageUri?.apply {
            item.image = uploadImage(this)
        }
        databaseReference?.apply {
            val key = generateRandomStringWithTime()
            if (item.id == null)
                item.id = key
            document(item.id!!).set(item).await()
        }

    }


    private suspend fun uploadImage(uri: Uri): String {
        val email = localRepository.getStoreFromPref()?.email
        val storageRef = FirebaseStorage.getInstance().reference
        val fileName = "${email ?: "plants"}/${System.currentTimeMillis()}.jpg"
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

    suspend fun deleteItem(item: SellerItem) {
        try {
            databaseReference?.apply {
                document(item.id!!).delete().await()
            }
            item.image?.let {
                val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(it)
                storageRef.delete().await()
            }
        } catch (e: Exception) {
            Log.e("USMAN-TAG", "Error deleting item: ${e.message}")
        }
    }

    fun getCategoryExistInItem(categoryId: String, listener: CategoryExistListener) {
        databaseReference?.apply {
            whereEqualTo("categoryId", categoryId)
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.size() > 0) {
                        listener.onExist()
                    } else {
                        listener.onNotExist()
                    }
                }
                .addOnFailureListener { exception ->
                    listener.onFailure(exception.message.toString())
                }
        }
    }



}

