package com.example.plantlets.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.Response.ItemSortOptions
import com.example.plantlets.Response.SortDirection
import com.example.plantlets.models.Category
import com.example.plantlets.models.SellerItem
import com.example.plantlets.models.ItemFillter
import com.example.plantlets.repositories.CategoryRepository
import com.example.plantlets.repositories.ItemRepository
import com.example.plantlets.utils.Constants.ALL
import com.example.plantlets.utils.Helper.sortBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SellerItemViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {


    val itemList: StateFlow<CustomResponse<List<SellerItem>>>
        get() = itemRepository.itemsStateFlow

    val categoryList: StateFlow<CustomResponse<List<Category>>>
        get() = categoryRepository.categoriesStateFlow


    var query: String = ""
    var sellerItemFillter = ItemFillter()

    fun startObserving() {
        itemRepository.getItems()
    }

    fun stopObserving() {
        itemRepository.removeListener()
    }

    suspend fun upsertItem(item: SellerItem, imageUri: Uri? = null) {
        itemRepository.upsertItem(item, imageUri)
    }

    fun deleteItem(item: SellerItem) {
        viewModelScope.launch(Dispatchers.IO) {
            itemRepository.deleteItem(item)
        }
    }

    fun getItemByQuery(query: String): List<SellerItem> {
        val filteredItems = itemList.value?.data?.filter { item ->
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

    fun getCategoriesOnce() {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.getCategoriesOnce()
        }
    }

    fun filteredItems(): List<SellerItem> {
        var filteredItems = itemList.value.data?: emptyList()
        if(query.isNotBlank()) {
            filteredItems = itemList.value.data?.filter { item ->
                item.name.lowercase().contains(query.lowercase())
            } ?: emptyList()
        }
        with(sellerItemFillter) {
            if (!filteredItems.isEmpty()) {
                if (category != ALL) {
                    val catId = getCatId(category)
                    filteredItems = filteredItems.filter { item ->
                        item.categoryId == catId
                    }
                }
                filteredItems = sortBy(filteredItems, sortOption, sortDirection)

            }
        }
        return filteredItems

    }


    fun getCatId(categoryName: String): String? {
        return categoryList.value.data?.firstOrNull { it.categoryName == categoryName }?.categoryId

    }




}

