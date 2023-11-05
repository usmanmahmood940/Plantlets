package com.example.plantlets.viewmodels.user

import androidx.lifecycle.ViewModel
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.models.Store
import com.example.plantlets.models.User
import com.example.plantlets.repositories.LocalRepository
import com.example.plantlets.repositories.StoreRepository
import com.example.plantlets.utils.Constants.STORE_REFRENCE_USER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class StoreListingViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val storeRepository: StoreRepository, )
    : ViewModel() {

    val storeList: StateFlow<CustomResponse<List<Store>>>
        get() = storeRepository.storesStateFlow

    fun startObserving() {
        storeRepository.getStores()
    }

    fun stopObserving() {
        storeRepository.removeListener()
    }

    fun setStore(store:Store?){
        localRepository.saveStoreDataToSharedPreferences(store,STORE_REFRENCE_USER)
    }
    fun getStore():Store?{
        return localRepository.getStoreFromPref(STORE_REFRENCE_USER)
    }

    fun getUserData(): User? {
        return localRepository.getCurrentUserData()
    }


}