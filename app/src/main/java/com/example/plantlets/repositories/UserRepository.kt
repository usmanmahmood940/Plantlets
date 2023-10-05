package com.example.plantlets.repositories

import android.content.SharedPreferences
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
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreRef: FirebaseFirestore,
    private val sharedPreferences: SharedPreferences,
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
                    saveUserDataToSharedPreferences(sharedPreferences, it)
                }
                runOnMain({ listener.onSuccess() })
                withContext(Dispatchers.IO) {
                    user?.apply {
                        if (type == VENDOR_TYPE) {
                            val store = getStoreData(email)
                            store?.let {
                                saveStoreDataToSharedPreferences(sharedPreferences, it)
                            }
                        }
                    }

                }


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


    private fun saveUserDataToSharedPreferences(sharedPreferences: SharedPreferences, user: User) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val userJson: String = gson.toJson(user)
        editor.putString(USER_REFRENCE, userJson)
       
        // Add other user-related data to SharedPreferences
        editor.apply()

//        val json: String? = sharedPreferences.getString(USER_REFRENCE, null)
//        val obj: User = gson.fromJson(json, User::class.java)
    }

    private fun saveStoreDataToSharedPreferences(sharedPreferences: SharedPreferences, store: Store) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val storeJson: String = gson.toJson(store)
        editor.putString(STORE_REFRENCE, storeJson)
        // Add other store-related data to SharedPreferences
        editor.apply()
    }

}