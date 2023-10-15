package com.example.plantlets.repositories

import android.util.Log
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.models.Category
import com.example.plantlets.utils.Constants.CATEGORIES_REFRENCE
import com.example.plantlets.utils.Constants.STORE_REFRENCE
import com.example.plantlets.utils.Helper
import com.example.plantlets.utils.Helper.generateRandomStringWithTime
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreRef: FirebaseFirestore,
    private val localRepository: LocalRepository
) {

    private var databaseReference: CollectionReference? = null
    private var valueEventListener: EventListener<QuerySnapshot>? = null
    private var categoryListener: ListenerRegistration? = null

    private val _categoriesStateFlow =
        MutableStateFlow<CustomResponse<List<Category>>>(CustomResponse.Loading())
    val categoriesStateFlow: StateFlow<CustomResponse<List<Category>>>
        get() = _categoriesStateFlow

    init {
        _categoriesStateFlow.value = CustomResponse.Loading()
        auth.currentUser?.apply {
            localRepository.getStoreFromPref()?.apply {
               databaseReference = firestoreRef.collection(STORE_REFRENCE).document(email!!)
                    .collection(CATEGORIES_REFRENCE)

            }
        }
    }

    fun getCategories() {
        _categoriesStateFlow.value = CustomResponse.Loading()
        valueEventListener = object : EventListener<QuerySnapshot> {
            override fun onEvent(snapshotlist: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    _categoriesStateFlow.value =
                        CustomResponse.Error(error.message.toString())
                }
                if (snapshotlist != null) {
                    val categoryList: MutableList<Category> = mutableListOf()
                    _categoriesStateFlow.value = CustomResponse.Success(categoryList)
                    for (snapshot in snapshotlist) {
                        val category = snapshot.toObject(Category::class.java)
                        if (category != null) {
                            categoryList.add(category)
                        }
                        _categoriesStateFlow.value = CustomResponse.Success(categoryList)
                    }
                }
            }
        }
        categoryListener = databaseReference?.addSnapshotListener(valueEventListener!!)

    }

    fun removeListener(){
        categoryListener?.apply {
            remove()
        }
    }



    fun upsertCategory(category: Category){

        databaseReference?.apply {
            val key = generateRandomStringWithTime()
            if(category.categoryId == null)
                category.categoryId = key
            document(category.categoryId!!).set(category).addOnCompleteListener {
                if (it.isSuccessful){
                    Log.d("USMAN-TAG","category added")
                }
                if(it.exception!=null){
                    Log.d("USMAN-TAG",it.exception!!.message.toString())
                }
            }
        }

    }


    fun deleteCategory(category: Category){
        databaseReference?.apply {
            document(category.categoryId!!).delete()
        }
    }

    suspend fun getCategoriesOnce(): CustomResponse<List<Category>> {
        try {
            val snapshotList: QuerySnapshot? = databaseReference?.get()?.await()

            if (snapshotList != null && !snapshotList.isEmpty) {
                val categories = snapshotList.toObjects<Category>()

                return CustomResponse.Success(categories)
            } else {
                // Handle the case where no data is available
                return CustomResponse.Error("Category List is empty")
            }
        } catch (e: Exception) {
            // Handle any exceptions (e.g., Firebase Firestore exceptions)
            e.printStackTrace()
            return CustomResponse.Error(e.message.toString())
        }
    }










}

