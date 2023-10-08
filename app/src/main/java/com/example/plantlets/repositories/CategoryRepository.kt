package com.example.plantlets.repositories

import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.models.Category
import com.example.plantlets.models.Store
import com.example.plantlets.utils.Constants.CATEGORIES_REFRENCE
import com.example.plantlets.utils.Constants.STORE_REFRENCE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreRef: FirebaseFirestore,
    private val localRepository: localRepository
) {

    private var databaseReference: CollectionReference? = null
    private var valueEventListener: EventListener<QuerySnapshot>? = null

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
        valueEventListener = object : EventListener<QuerySnapshot> {
            override fun onEvent(snapshotlist: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    _categoriesStateFlow.value =
                        CustomResponse.Error(error.message.toString())
                }
                if (snapshotlist != null) {
                    val categoryList: MutableList<Category> = mutableListOf()

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
        databaseReference?.addSnapshotListener(valueEventListener!!)

    }

    fun upsertCategory(category: Category){

        databaseReference?.apply {
           val key = document().id
            category.categoryId = key
            document(key).set(category)
        }

    }

    fun deleteCategory(category: Category){
        databaseReference?.apply {
            document(category.categoryId!!).delete()
        }
    }





}

