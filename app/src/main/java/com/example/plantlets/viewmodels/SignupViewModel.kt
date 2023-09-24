package com.example.plantlets.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantlets.interfaces.CustomSuccessFailureListener
import com.example.plantlets.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel(){

    fun signUp(
        email: String,
        password: String,
        name: String,
        mobileNumner: String,
        imageUri: Uri,
        listener: CustomSuccessFailureListener
    ){
        viewModelScope.launch {
            userRepository.signUp(email, password, name, mobileNumner, imageUri, listener)
        }
    }

}