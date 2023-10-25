package com.example.plantlets.repositories

import android.content.SharedPreferences
import com.example.plantlets.models.Store
import com.example.plantlets.models.User
import com.example.plantlets.utils.Constants
import com.example.plantlets.utils.Constants.STORE_REFRENCE
import com.google.gson.Gson
import javax.inject.Inject

class LocalRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

     fun saveUserDataToSharedPreferences(user: User) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val userJson: String = gson.toJson(user)
        editor.putString(Constants.USER_REFRENCE, userJson)

        editor.apply()
    }

     fun saveStoreDataToSharedPreferences(store: Store,storeRef:String =STORE_REFRENCE ) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val storeJson: String = gson.toJson(store)
        editor.putString(storeRef, storeJson)
        // Add other store-related data to SharedPreferences
        editor.apply()
    }
    fun getCurrentUserData(): User? {
        val userString: String? = sharedPreferences.getString(Constants.USER_REFRENCE, null)
        userString?.let {
            val objUser: User = Gson().fromJson(userString, User::class.java)
            return objUser
        }
        return null

    }

    fun getStoreFromPref(storeRef:String =STORE_REFRENCE ): Store? {
        val storeString: String? = sharedPreferences.getString(storeRef, null)
        storeString?.let {
            val objStore: Store = Gson().fromJson(storeString, Store::class.java)
            return objStore
        }
        return null

    }
}