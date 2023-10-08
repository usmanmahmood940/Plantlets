package com.example.plantlets.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantlets.R
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.activities.BaseActivity
import com.example.plantlets.activities.SellerHomeActivity
import com.example.plantlets.adapters.CategoryAdapter
import com.example.plantlets.databinding.FragmentCategoryBinding
import com.example.plantlets.interfaces.ItemClickListener
import com.example.plantlets.models.Category
import com.example.plantlets.utils.Extensions.showError
import com.example.plantlets.viewmodels.LoginViewModel
import com.example.plantlets.viewmodels.SellerCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryFragment : Fragment(),ItemClickListener {


    lateinit var binding:FragmentCategoryBinding
    lateinit var categoryAdapter: CategoryAdapter
    private lateinit var sellerCategoryViewModel: SellerCategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(inflater,container,false)
        sellerCategoryViewModel = ViewModelProvider(requireActivity()).get(SellerCategoryViewModel::class.java)
        // Inflate the layout for this fragment
        setupCategoryList()
        setupListeners()
        observeCategoryList()
        return binding.root

    }


    private fun setupCategoryList() {
        categoryAdapter  = CategoryAdapter(categoryItemList = emptyList(),listener = this)
        binding.rvCategoryList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
    }


    private fun setupListeners() {
        with(binding){
            fabCategory.setOnClickListener {
                showDialog()
            }
        }
    }

    private fun observeCategoryList() {
        lifecycleScope.launch {
            sellerCategoryViewModel.categoryList.collect{response ->
                when(response){
                    is CustomResponse.Success -> {
                        (requireActivity() as SellerHomeActivity).hideProgressBar()
                        response.data?.let {categoryList ->
                            categoryAdapter.setList(categoryList)
                        }

                    }

                    is CustomResponse.Loading -> {
                        (requireActivity() as SellerHomeActivity).showProgressBar()
                    }

                    is CustomResponse.Error -> {
                        (requireActivity() as SellerHomeActivity).apply{
                            hideProgressBar()
                            showAlert(title = getString(R.string.error), message = response.errorMessage)
                        }

                    }
                }
            }
        }
    }

    private fun showDialog(category: Category?=null) {
        binding.fabCategory.isEnabled = false
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_category, null)
        val btnAction = dialogView.findViewById<TextView>(R.id.btnAction)
        val etCategoryName = dialogView.findViewById<EditText>(R.id.etCategoryName)
        val newCategory = Category()

        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
            .setView(dialogView)
            .setOnDismissListener {
                binding.fabCategory.isEnabled = true
            }

        val dialog = dialogBuilder.create()

        category?.apply {
            etCategoryName.setText(categoryName)
            newCategory.categoryId = categoryId
            btnAction.text = "Update"
        }

        btnAction.setOnClickListener {
            if(etCategoryName.text.trim().isBlank()) {
                etCategoryName.showError(getString(com.example.plantlets.R.string.field_required_error))
                return@setOnClickListener
            }
            newCategory.categoryName = etCategoryName.text.toString()
            sellerCategoryViewModel.upsertCategory(newCategory)
            dialog.dismiss()
        }


        dialog.show()
    }

    override fun onEdit(category: Category) {
        showDialog(category)
    }

    override fun onDelete(category: Category) {
        sellerCategoryViewModel.deleteCategory(category)
    }

}