package com.example.plantlets.repositories

import android.net.Uri
import com.example.plantlets.interfaces.CustomSuccessFailureListener
import com.example.plantlets.models.Store
import com.example.plantlets.models.User
import com.example.plantlets.utils.Constants.ITEM_REFRENCE
import com.example.plantlets.utils.Constants.STORE_REFRENCE
import com.example.plantlets.utils.Constants.USER_REFRENCE
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

                saveUser(
                   user = User(uid, email, type, mobileNumber)
                )
                storeDetails?.let {
                    addStore(storeDetails)
                }
                auth.signOut()
                runOnMain({ listener.onSuccess() })

            }
        } catch (e: Exception) {
            runOnMain {
                listener.onFailure(e.message)
            }
        }
    }

    fun addStore(storeDetails: Store) {
        firestoreRef.collection(STORE_REFRENCE).document(storeDetails.email!!).set(storeDetails)
//        firestoreRef.collection(STORE_REFRENCE).document(email).collection(ITEM_REFRENCE).

    }

    private fun saveUser(user: User) {
        firestoreRef.collection(USER_REFRENCE).document(user.id).set(user)
    }

    private suspend fun runOnMain(action: () -> Unit) {
        withContext(Dispatchers.Main) {
            action.invoke()
        }
    }
}