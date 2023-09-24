package com.example.plantlets.repositories

import android.net.Uri
import com.example.plantlets.interfaces.CustomSuccessFailureListener
import com.example.plantlets.models.Store
import com.example.plantlets.models.User
import com.example.plantlets.utils.Constants.ROLE_USER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
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
        role:String,
        imageUri: Uri,
        listener: CustomSuccessFailureListener,
        storeDetails:Store?=null
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
                val newUser = User(uid, ROLE_USER, mobileNumber)
                saveUser(newUser)
                auth.signOut()
                runOnMain({ listener.onSuccess() })

            }
        } catch (e: Exception) {
            runOnMain {
                listener.onFailure(e.message)
            }
        }
    }

    private fun saveUser(user: User) {
        firabaseDbRef.child(USER_REFRENCE).child(user.id).setValue(user)
    }

    private suspend fun runOnMain(action: () -> Unit) {
        withContext(Dispatchers.Main) {
            action.invoke()
        }
    }
}