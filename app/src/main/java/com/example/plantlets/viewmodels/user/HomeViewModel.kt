package com.example.plantlets.viewmodels.user

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.models.Category
import com.example.plantlets.models.ItemFillter
import com.example.plantlets.models.SellerItem
import com.example.plantlets.models.User
import com.example.plantlets.repositories.ItemRepository
import com.example.plantlets.repositories.LocalRepository
import com.example.plantlets.utils.Constants
import com.example.plantlets.utils.Helper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor (
    private val localRepository: LocalRepository,
    private val itemRepository: ItemRepository,

):ViewModel(){

    fun getUserData(): User? {
        return localRepository.getCurrentUserData()
    }

    val itemList: StateFlow<CustomResponse<List<SellerItem>>>
        get() = itemRepository.itemsStateFlow



    fun startObserving() {
        itemRepository.getItems()
    }

    fun stopObserving() {
        itemRepository.removeListener()
    }

    fun sortByPopularity(itemList:List<SellerItem>):List<SellerItem>{
        return itemList.sortedBy { it.soldCount }.reversed()
    }



}