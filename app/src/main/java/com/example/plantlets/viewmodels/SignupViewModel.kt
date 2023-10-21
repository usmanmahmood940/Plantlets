package com.example.plantlets.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantlets.interfaces.CustomSuccessFailureListener
import com.example.plantlets.models.Location
import com.example.plantlets.models.Store
import com.example.plantlets.repositories.UserRepository
import com.example.plantlets.utils.Constants.USER_TYPE
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel(){

    var type : String = USER_TYPE

    lateinit var email:String
    lateinit var password :String
    lateinit var name :String
    lateinit var mobileNumber :String

    lateinit var storeName :String
    lateinit var storeAddress :String
    lateinit var storePinLocation :String
    lateinit var location: Location
    var imageUri: Uri? = null


    fun signUp(
        listener: CustomSuccessFailureListener,
        storeDetails:Store?=null
    ){
        viewModelScope.launch {
            userRepository.signUp(email, password, name, mobileNumber,type, imageUri!!, listener,storeDetails)
        }
    }



}