package com.example.plantlets.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantlets.activities.BaseActivity
import com.example.plantlets.activities.SellerHomeActivity
import com.example.plantlets.interfaces.CustomSuccessFailureListener
import com.example.plantlets.models.User
import com.example.plantlets.repositories.UserRepository
import com.example.plantlets.utils.Constants.USER_REFRENCE
import com.example.plantlets.utils.Constants.USER_TYPE
import com.example.plantlets.utils.Constants.VENDOR_TYPE
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {


    fun login(
        email: String,
        password: String,
        listener: CustomSuccessFailureListener,
    ) {
        viewModelScope.launch {
            userRepository.loginWithEmailPass(email, password, listener)
        }
    }

    fun getNavigation(): Class<out BaseActivity> {

        val userString: String? = sharedPreferences.getString(USER_REFRENCE, null)
        val objUser: User = Gson().fromJson(userString, User::class.java)
        val destinationClass = when (objUser.type) {
            VENDOR_TYPE -> {
                SellerHomeActivity::class.java
            }
            USER_TYPE -> {
                SellerHomeActivity::class.java
            }
            else -> {
                SellerHomeActivity::class.java
            }
        }
        return destinationClass
    }


}