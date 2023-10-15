package com.example.plantlets.viewmodels

import androidx.lifecycle.ViewModel
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.interfaces.CategoryExistListener
import com.example.plantlets.models.Category
import com.example.plantlets.repositories.CategoryRepository
import com.example.plantlets.repositories.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SellerCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val itemRepository: ItemRepository
) : ViewModel() {

    val categoryList: StateFlow<CustomResponse<List<Category>>>
        get() = categoryRepository.categoriesStateFlow


    var query: String? = null

    fun startObserving() {
        categoryRepository.getCategories()
    }

    fun stopObserving() {
        categoryRepository.removeListener()
    }

    fun upsertCategory(category: Category) {
        categoryRepository.upsertCategory(category)
    }

    fun deleteCategory(category: Category) {
        categoryRepository.deleteCategory(category)
    }

    fun categoryNameExist(categories: List<Category>, newCategory: Category): Boolean {
        return categories.any { category -> category.categoryName == newCategory.categoryName }
    }


    //    fun getFoodItemsByQuery(query: String): List<FoodItem> {
//        val filteredCategory = mutableListOf<Category>()
//        filteredCategory.addAll(categoryList.value?.data?.filter { it.name.contains(query) }
//            ?: emptyList())
//        val filteredItems = foodItemList.value?.data?.filter { foodItem ->
//            filteredCategory.any { category ->
//                category.id == foodItem.categoryId
//            } || foodItem.title.contains(query)
//        } ?: emptyList()
//        return filteredItems
//
//    }
    fun getCategoryByQuery(query: String): List<Category> {
        val filteredItems = categoryList.value.data?.filter { category ->
            category.categoryName.lowercase().contains(query)
        } ?: emptyList()
        return filteredItems

    }

    fun getCategoryExistInItem(categoryId:String,listener: CategoryExistListener){
        itemRepository.getCategoryExistInItem(categoryId,listener)
    }
}