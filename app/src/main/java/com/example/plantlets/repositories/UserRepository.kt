package com.example.plantlets.repositories

import android.net.Uri
import com.example.plantlets.interfaces.CustomSuccessFailureListener
import com.example.plantlets.models.Store
import com.example.plantlets.models.User
import com.example.plantlets.utils.Constants.STORE_REFRENCE
import com.example.plantlets.utils.Constants.USER_REFRENCE
import com.example.plantlets.utils.Constants.VENDOR_TYPE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreRef: FirebaseFirestore,
    private val localRepository: LocalRepository
) {

    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        mobileNumber: String,
        type: String,
        imageUri: Uri,
        listener: CustomSuccessFailureListener,
        storeDetails: Store? = null,
    ) {
        try {
            val task = auth.createUserWithEmailAndPassword(email, password).await()
            val displayNameUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(imageUri)
                .build()


            task.user?.apply {
//                sendEmailVerification()
                updateProfile(displayNameUpdate)
                runOnMain({ listener.onSuccess() })
                withContext(Dispatchers.IO) {
                    saveUser(
                        user = User(uid, email, type, mobileNumber)
                    )
                    storeDetails?.let {
                        addStore(storeDetails)
                    }
                }
                auth.signOut()


            }
        } catch (e: Exception) {
            runOnMain {
                listener.onFailure(e.message)
            }
        }
    }

    suspend fun addStore(storeDetails: Store) {
//        firestoreRef.collection(STORE_REFRENCE).document(storeDetails.email!!).get()
//            .addOnSuccessListener {
//                it.exists()
//
//            }
        firestoreRef.collection(STORE_REFRENCE).document(storeDetails.email!!).set(storeDetails)
            .await()
//        firestoreRef.collection(STORE_REFRENCE).document(email).collection(ITEM_REFRENCE).

    }

    private suspend fun saveUser(user: User) {
        firestoreRef.collection(USER_REFRENCE).document(user.id).set(user).await()
    }

    private suspend fun runOnMain(action: () -> Unit) {
        withContext(Dispatchers.Main) {
            action.invoke()
        }
    }


    suspend fun loginWithEmailPass(
        email: String,
        password: String,
        listener: CustomSuccessFailureListener,
    ) {
        try {
            val signInAuth = auth.signInWithEmailAndPassword(email, password).await()
            signInAuth.user?.apply {
                //on running this :listener.onSuccess() the activity will navigate to next

                val user = getLoginUser(uid)
                user?.let {
                    localRepository.saveUserDataToSharedPreferences(it)
                }


                user?.apply {
                    if (type == VENDOR_TYPE) {
                        val store = getStoreData(email)
                        store?.let {
                            localRepository.saveStoreDataToSharedPreferences(it)
                        }
                    }
                }
                runOnMain({ listener.onSuccess() })


            }
        } catch (exception: Exception) {
            runOnMain {
                listener.onFailure(exception.message)
            }
        }
    }

    private suspend fun getLoginUser(userId: String): User? {
        val userDoc = firestoreRef.collection(USER_REFRENCE).document(userId).get().await()
        return userDoc.toObject(User::class.java)
    }

    private suspend fun getStoreData(storeId: String): Store? {
        val storeDoc = firestoreRef.collection(STORE_REFRENCE).document(storeId).get().await()
        return storeDoc.toObject(Store::class.java)
    }


}