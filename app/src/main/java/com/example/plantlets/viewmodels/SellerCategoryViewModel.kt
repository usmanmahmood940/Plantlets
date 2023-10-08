package com.example.plantlets.viewmodels

import androidx.lifecycle.ViewModel
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.models.Category
import com.example.plantlets.repositories.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SellerCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
): ViewModel() {

    val categoryList: StateFlow<CustomResponse<List<Category>>>
        get() = categoryRepository.categoriesStateFlow

    init{
        categoryRepository.getCategories()
    }

    fun upsertCategory(category: Category){
        categoryRepository.upsertCategory(category)
    }

    fun deleteCategory(category: Category){
        categoryRepository.deleteCategory(category)
    }

    fun categoryNameExist(categories:List<Category>,newCategory: Category):Boolean{
      return categories.any { category -> category.categoryName == newCategory.categoryName }
    }
}