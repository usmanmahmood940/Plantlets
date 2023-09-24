package com.example.plantlets.repositories

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
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
        imageUri: Uri,
        listener: CustomSuccessFailureListener,
    ) {
        try {
            val task = auth.createUserWithEmailAndPassword(email, password).await()
            val displayNameUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(imageUri)
                .build()

            task.user?.apply {
                sendEmailVerification()
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
}