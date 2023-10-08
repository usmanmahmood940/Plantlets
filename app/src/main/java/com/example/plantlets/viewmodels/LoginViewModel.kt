package com.example.plantlets.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantlets.interfaces.CustomSuccessFailureListener
import com.example.plantlets.repositories.UserRepository
import com.example.plantlets.repositories.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val localRepository: LocalRepository
) : ViewModel() {

    lateinit var email: String
    lateinit var password: String

    fun login(
        email: String,
        password: String,
        listener: CustomSuccessFailureListener,
    ) {
        viewModelScope.launch {
            userRepository.loginWithEmailPass(email, password, listener)
        }
    }




}