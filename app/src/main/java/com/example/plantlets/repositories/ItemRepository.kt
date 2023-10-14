package com.example.plantlets.repositories

import android.net.Uri
import android.util.Log
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.models.SellerItem
import com.example.plantlets.utils.Constants.ITEM_REFRENCE
import com.example.plantlets.utils.Constants.STORE_REFRENCE
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
    val itemssStateFlow: StateFlow<CustomResponse<List<SellerItem>>>
        get() = _itemsStateFlow

    init {
        _itemsStateFlow.value = CustomResponse.Loading()
        auth.currentUser?.apply {
            localRepository.getStoreFromPref()?.apply {
                databaseReference = firestoreRef.collection(STORE_REFRENCE).document(email!!)
                    .collection(ITEM_REFRENCE)

            }
        }
    }

    fun getItems() {
        valueEventListener = object : EventListener<QuerySnapshot> {
            override fun onEvent(snapshotlist: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    _itemsStateFlow.value =
                        CustomResponse.Error(error.message.toString())
                }
                if (snapshotlist != null) {
                    val itemList: MutableList<SellerItem> = mutableListOf()
                    _itemsStateFlow.value = CustomResponse.Success(itemList)
                    for (snapshot in snapshotlist) {
                        val item = snapshot.toObject(SellerItem::class.java)
                        if (item != null) {
                            itemList.add(item)
                        }
                        _itemsStateFlow.value = CustomResponse.Success(itemList)
                    }
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


    suspend fun upsertItem(item: SellerItem,imageUri: Uri?) {
        imageUri?.apply {
            item.image = uploadImage(this)
        }
        databaseReference?.apply {
            val key = document().id
            if (item.id == null)
                item.id = key
            document(item.id!!).set(item).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("USMAN-TAG", "item updated")
                }
                if (it.exception != null) {
                    Log.d("USMAN-TAG", it.exception!!.message.toString())
                }
            }
        }

    }


    private suspend fun uploadImage(uri: Uri): String {
        val email = localRepository.getStoreFromPref()?.email
        val storageRef = FirebaseStorage.getInstance().reference
        val fileName = "${email?:"plants"}/${System.currentTimeMillis()}.jpg"
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

    fun deleteItem(item: SellerItem) {
        databaseReference?.apply {
            document(item.id!!).delete()
        }
    }


}

