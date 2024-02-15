package com.example.plantlets.fragments.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantlets.R
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.Response.ItemSortOptions
import com.example.plantlets.Response.SortDirection
import com.example.plantlets.activities.UserHomeActivity
import com.example.plantlets.adapters.UserItemAdapter
import com.example.plantlets.databinding.DialogFilterItemsBinding
import com.example.plantlets.databinding.FragmentSearchBinding
import com.example.plantlets.interfaces.UserItemClickListener
import com.example.plantlets.models.ItemFillter
import com.example.plantlets.models.SellerItem
import com.example.plantlets.utils.CenterItemZoomScrollListener
import com.example.plantlets.utils.Constants
import com.example.plantlets.viewmodels.user.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(), UserItemClickListener {


    lateinit var binding:FragmentSearchBinding
    lateinit var searchViewModel: SearchViewModel
    lateinit var itemAdapter: UserItemAdapter
    private var dialogCheck = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        (requireActivity() as UserHomeActivity).hideBottomNav()

        init()
        observeList()
        searchViewModel.getCategoriesOnce()
        updateDialogCheck()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        searchViewModel.startObserving()
    }

    override fun onStop() {
        super.onStop()
        searchViewModel.stopObserving()
    }

    private fun init() {
        with(binding) {
            itemAdapter = UserItemAdapter(this@SearchFragment,true)
            rvPlants.apply {
                layoutManager = GridLayoutManager(requireContext(), 2)
                adapter = itemAdapter
            }

            rlBack.setOnClickListener {
                findNavController().navigateUp()
            }

            binding.btnfilter.setOnClickListener {
                showFilterDialog()
            }
            setupSearch()
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                val query = editable.toString().lowercase()
                searchViewModel.query = query
                val list = searchViewModel.filteredItems()
                itemAdapter.submitList(list)
            }
        })
    }

    private fun showFilterDialog() {
        if (dialogCheck) {
            val dialogBinding =
                DialogFilterItemsBinding.inflate(LayoutInflater.from(requireContext()))
            val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
                .setView(dialogBinding.root)
            val dialog = dialogBuilder.create()
            with(dialogBinding) {
                var tempCategoryList = mutableListOf<String>()
                searchViewModel.categoryList.value.data?.let {
                    tempCategoryList = it.map { category -> category.categoryName }.toMutableList()
                    tempCategoryList.add(0, Constants.ALL)
                }
                spinnerCategory.adapter = getSpinnerAdapter(tempCategoryList)
                spinnerSortOption.adapter = getSpinnerAdapter(
                    enumValues<ItemSortOptions>().map { it.name }.toMutableList()
                )
                spinnerSortDirection.adapter = getSpinnerAdapter(
                    enumValues<SortDirection>().map { it.name }.toMutableList()
                )

                searchViewModel.sellerItemFillter.apply {
                    val name =
                        searchViewModel.categoryList.value.data?.firstOrNull { it.categoryName == category }?.categoryName
                    spinnerCategory.setSelection(getIndex(tempCategoryList, name))

                    spinnerSortOption.setSelection(getIndex(
                        enumValues<ItemSortOptions>().map { it.name }
                            .toMutableList(),
                        sortOption)
                    )
                    spinnerSortDirection.setSelection(getIndex(
                        enumValues<SortDirection>().map { it.name }
                            .toMutableList(),
                        sortDirection)
                    )
                }

                btnSetFilter.setOnClickListener {
                    searchViewModel.sellerItemFillter = ItemFillter(
                        category = spinnerCategory.selectedItem.toString(),
                        sortOption = spinnerSortOption.selectedItem.toString(),
                        sortDirection = spinnerSortDirection.selectedItem.toString()
                    )
                    itemAdapter.submitList(searchViewModel.filteredItems())
                    dialog.dismiss()
                }

                dialog.show()
            }
        }

    }

    fun observeList() {
        lifecycleScope.launch {
            searchViewModel.itemList.collect { response ->
                when (response) {
                    is CustomResponse.Success -> {
                        (requireActivity() as UserHomeActivity).hideProgressBar()
                        response.data?.let { itemList ->

                            if (itemList.isEmpty()) {


                            } else {
                                binding.rvPlants.visibility = View.VISIBLE
                                val list = searchViewModel.filteredItems()
                                itemAdapter.submitList(list)
                            }

                        }

                    }

                    is CustomResponse.Loading -> {
                        (requireActivity() as UserHomeActivity).showProgressBar()
                        binding.rvPlants.visibility = View.GONE

                    }

                    is CustomResponse.Error -> {
                        (requireActivity() as UserHomeActivity).apply {
                            hideProgressBar()
                            showAlert(
                                title = getString(R.string.error), message = response.errorMessage
                            )
                        }
                        binding.rvPlants.visibility = View.GONE

                    }
                }
            }
        }
    }

    override fun onClick(item: SellerItem) {
        if (findNavController().currentDestination?.id == R.id.searchFragment) {
            val action = SearchFragmentDirections.actionSearchFragmentToItemDetails(item,null)
            findNavController().navigate(action)
        }
    }

    private fun getIndex(spinnerCategories: MutableList<String>, categoryName: String?): Int {
        categoryName?.let {
            return spinnerCategories.indexOf(categoryName)
        }
        return 0
    }

    fun updateDialogCheck() {
        lifecycleScope.launch {
           searchViewModel.categoryList.collect { response ->
                when (response) {
                    is CustomResponse.Success -> {
                        dialogCheck = true

                    }
                    else -> {
                        dialogCheck = false
                    }
                }
            }
        }
    }

    private fun getSpinnerAdapter(
        list: MutableList<String>,
    ): ArrayAdapter<String> {
        return ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, list
        )
    }


}