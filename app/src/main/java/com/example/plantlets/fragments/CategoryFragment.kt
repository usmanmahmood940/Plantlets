package com.example.plantlets.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantlets.R
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.activities.BaseActivity
import com.example.plantlets.activities.SellerHomeActivity
import com.example.plantlets.adapters.CategoryAdapter
import com.example.plantlets.databinding.FragmentCategoryBinding
import com.example.plantlets.interfaces.CategoryClickListener
import com.example.plantlets.interfaces.CategoryExistListener
import com.example.plantlets.models.Category
import com.example.plantlets.utils.Extensions.showError
import com.example.plantlets.viewmodels.SellerCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryFragment : Fragment(), CategoryClickListener {


    lateinit var binding: FragmentCategoryBinding
    lateinit var categoryAdapter: CategoryAdapter
    private lateinit var sellerCategoryViewModel: SellerCategoryViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        sellerCategoryViewModel =
            ViewModelProvider(requireActivity()).get(SellerCategoryViewModel::class.java)
        // Inflate the layout for this fragment
        setupCategoryList()
        setupListeners()
        observeCategoryList()
        setupSearch()
        return binding.root

    }

    override fun onResume() {
        super.onResume()
        sellerCategoryViewModel.startObserving()
    }

    override fun onPause() {
        super.onPause()
        sellerCategoryViewModel.stopObserving()
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                val query = editable.toString().lowercase()
                sellerCategoryViewModel.query = query
                val list = sellerCategoryViewModel.getCategoryByQuery(query)
                categoryAdapter.setList(list)
            }
        })
    }


    private fun setupCategoryList() {
        categoryAdapter = CategoryAdapter(categoryItemList = emptyList(), listener = this)
        binding.rvCategoryList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
    }


    private fun setupListeners() {
        with(binding) {
            fabCategory.setOnClickListener {
                showDialog()
            }
        }
    }

    private fun observeCategoryList() {
        lifecycleScope.launch {
            sellerCategoryViewModel.categoryList.collect { response ->
                when (response) {
                    is CustomResponse.Success -> {
                        (requireActivity() as SellerHomeActivity).hideProgressBar()
                        response.data?.let { categoryList ->
                            val list = sellerCategoryViewModel.getCategoryByQuery(
                                sellerCategoryViewModel.query ?: ""
                            )
                            categoryAdapter.setList(list)
                        }

                    }

                    is CustomResponse.Loading -> {
                        (requireActivity() as SellerHomeActivity).showProgressBar()
                    }

                    is CustomResponse.Error -> {
                        (requireActivity() as SellerHomeActivity).apply {
                            hideProgressBar()
                            showAlert(
                                title = getString(R.string.error),
                                message = response.errorMessage
                            )
                        }

                    }
                }
            }
        }
    }

    private fun showDialog(category: Category? = null) {
        binding.fabCategory.isEnabled = false
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_category, null)
        val tvDialogLabel = dialogView.findViewById<TextView>(R.id.tv_dialog_label)
        val btnAction = dialogView.findViewById<TextView>(R.id.btnAction)
        val etCategoryName = dialogView.findViewById<EditText>(R.id.etCategoryName)
        var newCategory = Category()

        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
            .setView(dialogView)
            .setOnDismissListener {
                binding.fabCategory.isEnabled = true
            }

        val dialog = dialogBuilder.create()

        category?.apply {
            tvDialogLabel.text = "Update Category"
            etCategoryName.setText(categoryName)
            newCategory.categoryId = category.categoryId
            btnAction.text = "Update"
        }

        btnAction.setOnClickListener {
            if (etCategoryName.text.trim().isBlank()) {
                etCategoryName.showError(getString(com.example.plantlets.R.string.field_required_error))
                return@setOnClickListener
            }
            newCategory.categoryName = etCategoryName.text.toString()
            if (!sellerCategoryViewModel.categoryNameExist(
                    categoryAdapter.getList(),
                    newCategory
                )
            ) {
                sellerCategoryViewModel.upsertCategory(newCategory)
                dialog.dismiss()
            } else {
                dialog.dismiss()
                (requireActivity() as SellerHomeActivity).showAlert(
                    title = getString(R.string.error),
                    message = "Category of this name already exist"
                )
            }

        }


        dialog.show()
    }

    override fun onEdit(category: Category) {
        showDialog(category)
    }

    override fun onDelete(category: Category) {
        sellerCategoryViewModel.getCategoryExistInItem(category.categoryId!!,object :CategoryExistListener{
            override fun onExist() {
                (requireActivity() as BaseActivity).showAlert(
                    title = "Category Is in Use",
                    message = "This category is currently in use by items and cannot be deleted"
                )
            }

            override fun onNotExist() {
                sellerCategoryViewModel.deleteCategory(category)
            }

            override fun onFailure(errorMessage: String?) {
                (requireActivity() as BaseActivity).showAlert(
                    title = getString(R.string.error),
                    message = errorMessage
                )
            }

        })

    }

}