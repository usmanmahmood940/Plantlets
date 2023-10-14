package com.example.plantlets.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.models.Category
import com.example.plantlets.models.SellerItem
import com.example.plantlets.repositories.CategoryRepository
import com.example.plantlets.repositories.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SellerItemViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    val itemList: StateFlow<CustomResponse<List<SellerItem>>>
        get() = itemRepository.itemssStateFlow

    val categoryList: StateFlow<CustomResponse<List<Category>>>
        get() = categoryRepository.categoriesStateFlow


    var query: String? = null

    fun startObserving() {
        itemRepository.getItems()
    }

    fun stopObserving() {
        itemRepository.removeListener()
    }

    fun upsertItem(item: SellerItem, imageUri: Uri? = null) {

        viewModelScope.launch {
            itemRepository.upsertItem(item, imageUri)
        }

    }

    fun deleteItem(item: SellerItem) {
        itemRepository.deleteItem(item)
    }

    fun getItemByQuery(query: String): List<SellerItem> {
        val filteredItems = itemList.value.data?.filter { item ->
            item.name.lowercase().contains(query)
        } ?: emptyList()
        return filteredItems

    }

    fun startObservingCateories() {
        categoryRepository.getCategories()

    }

    fun stopObservingCategories() {
        categoryRepository.removeListener()

    }


}
