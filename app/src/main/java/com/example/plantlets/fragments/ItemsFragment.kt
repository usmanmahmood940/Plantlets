package com.example.plantlets.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantlets.R
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.Response.ItemSortOptions
import com.example.plantlets.Response.SortDirection
import com.example.plantlets.activities.BaseActivity
import com.example.plantlets.activities.SellerHomeActivity
import com.example.plantlets.adapters.SellerItemAdapter
import com.example.plantlets.databinding.DialogFilterItemsBinding
import com.example.plantlets.databinding.FragmentItemsBinding
import com.example.plantlets.interfaces.ItemClickListener
import com.example.plantlets.models.SellerItem
import com.example.plantlets.models.SellerItemFillter
import com.example.plantlets.utils.Constants.ALL
import com.example.plantlets.utils.Constants.CATEGORY_REFRENCE
import com.example.plantlets.viewmodels.SellerItemViewModel
import kotlinx.coroutines.launch


class ItemsFragment : Fragment(), ItemClickListener {

    private lateinit var binding: FragmentItemsBinding
    lateinit var itemAdapter: SellerItemAdapter
    private lateinit var itemViewModel: SellerItemViewModel
    private var dialogCheck = false
    private var navController:NavController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentItemsBinding.inflate(inflater, container, false)
        itemViewModel = ViewModelProvider(requireActivity()).get(SellerItemViewModel::class.java)

        initListeners()
        observeItemList()
        initItemList()
        itemViewModel.getCategoriesOnce()
        updateDialogCheck()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        itemViewModel.startObserving()
    }

    override fun onPause() {
        super.onPause()
        itemViewModel.stopObserving()
    }

    private fun initItemList() {
        itemAdapter = SellerItemAdapter(this)
        binding.rvSellerItemList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemAdapter
        }
    }



    private fun initListeners() {
        binding.fabItem.blockingClickListener {

            val action = ItemsFragmentDirections.actionItemsFragmentToAddItemFragment(null)
            findNavController().navigate(action)

        }
        binding.btnfilter.setOnClickListener {
            showFilterDialog()
        }
        setupSearch()


    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                val query = editable.toString().lowercase()
                itemViewModel.query = query
                val list = itemViewModel.filteredItems()
                itemAdapter.submitList(list)
            }
        })
    }

    fun createDummySellerItemList(): List<SellerItem> {
        val dummyList = mutableListOf<SellerItem>()

        // Add dummy SellerItem objects to the list
        dummyList.add(SellerItem("Item 1", 10, 19.99, "This is the first item."))
        dummyList.add(SellerItem("Item 2", 5, 29.99, "This is the second item."))
        dummyList.add(SellerItem("Item 3", 20, 9.99, "This is the third item."))
        dummyList.add(SellerItem("Item 4", 15, 14.99, "This is the fourth item."))
        dummyList.add(SellerItem("Item 5", 8, 39.99, "This is the fifth item."))

        return dummyList
    }

    private fun observeItemList() {
        lifecycleScope.launch {
            itemViewModel.itemList.collect { response ->
                when (response) {
                    is CustomResponse.Success -> {
                        (requireActivity() as SellerHomeActivity).hideProgressBar().also {
                            binding.fabItem.isEnabled = true
                        }
                        response.data?.let { itemList ->
                            val list = itemViewModel.filteredItems()
                            itemAdapter.submitList(list)
                        }

                    }

                    is CustomResponse.Loading -> {
                        (requireActivity() as SellerHomeActivity).showProgressBar().also {
                            binding.fabItem.isEnabled = false
                        }
                    }

                    is CustomResponse.Error -> {
                        (requireActivity() as SellerHomeActivity).apply {
                            hideProgressBar()
                            showAlert(
                                title = getString(R.string.error), message = response.errorMessage
                            )
                        }

                    }
                }
            }
        }
    }

    override fun onView(item: SellerItem) {


    }

    override fun onEdit(item: SellerItem) {
        if (findNavController().currentDestination?.id == R.id.itemsFragment) {
            val action = ItemsFragmentDirections.actionItemsFragmentToAddItemFragment(item)
            findNavController().navigate(action)
        }
    }

    override fun onDelete(item: SellerItem) {
        (requireActivity() as BaseActivity).showAlert(title = "Confirmation",
            message = "Do you really want to delete it",
            positiveButtonText = "Yes",
            positiveButtonClickListener = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    itemViewModel.deleteItem(item)
                }

            },
            negativeButtonText = "No",
            negativeButtonClickListener = object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    p0?.dismiss()
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
                itemViewModel.categoryList.value.data?.let {
                    tempCategoryList = it.map { category -> category.categoryName }.toMutableList()
                    tempCategoryList.add(0, ALL)
                }
                spinnerCategory.adapter = getSpinnerAdapter(tempCategoryList)
                spinnerSortOption.adapter = getSpinnerAdapter(
                    enumValues<ItemSortOptions>().map { it.name }.toMutableList()
                )
                spinnerSortDirection.adapter = getSpinnerAdapter(
                    enumValues<SortDirection>().map { it.name }.toMutableList()
                )

                itemViewModel.sellerItemFillter.apply {
                    val name =
                        itemViewModel.categoryList.value.data?.firstOrNull { it.categoryName == category }?.categoryName
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
                    itemViewModel.sellerItemFillter = SellerItemFillter(
                        category = spinnerCategory.selectedItem.toString(),
                        sortOption = spinnerSortOption.selectedItem.toString(),
                        sortDirection = spinnerSortDirection.selectedItem.toString()
                    )
                    itemAdapter.submitList(itemViewModel.filteredItems())
                    dialog.dismiss()
                }

                dialog.show()
            }
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
            val job = itemViewModel.categoryList.collect { response ->
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

//    private fun filterDialog() {
//        binding.fabCategory.isEnabled = false
//        val dialogView =
//            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_category, null)
//        val tvDialogLabel = dialogView.findViewById<TextView>(R.id.tv_dialog_label)
//        val btnAction = dialogView.findViewById<TextView>(R.id.btnAction)
//        val etCategoryName = dialogView.findViewById<EditText>(R.id.etCategoryName)
//        var newCategory = Category()
//
//        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
//            .setView(dialogView)
//            .setOnDismissListener {
//                binding.fabCategory.isEnabled = true
//            }
//
//        val dialog = dialogBuilder.create()
//
//        category?.apply {
//            tvDialogLabel.text = "Update Category"
//            etCategoryName.setText(categoryName)
//            newCategory.categoryId = category.categoryId
//            btnAction.text = "Update"
//        }
//
//        btnAction.setOnClickListener {
//            if (etCategoryName.text.trim().isBlank()) {
//                etCategoryName.showError(getString(com.example.plantlets.R.string.field_required_error))
//                return@setOnClickListener
//            }
//            newCategory.categoryName = etCategoryName.text.toString()
//            if (!sellerCategoryViewModel.categoryNameExist(
//                    categoryAdapter.currentList,
//                    newCategory
//                )
//            ) {
//                sellerCategoryViewModel.upsertCategory(newCategory)
//                dialog.dismiss()
//            } else {
//                dialog.dismiss()
//                (requireActivity() as SellerHomeActivity).showAlert(
//                    title = getString(R.string.error),
//                    message = "Category of this name already exist"
//                )
//            }
//
//        }
//
//
//        dialog.show()
//    }

    private val clickTag = "__click__"
    fun View.blockingClickListener(debounceTime: Long = 1200L, action: () -> Unit) {
        this.setOnClickListener(object : View.OnClickListener {
            private var lastClickTime: Long = 0
            override fun onClick(v: View) {
                val timeNow = SystemClock.elapsedRealtime()
                val elapsedTimeSinceLastClick = timeNow - lastClickTime
                Log.d(clickTag, """
                        DebounceTime: $debounceTime
                        Time Elapsed: $elapsedTimeSinceLastClick
                        Is within debounce time: ${elapsedTimeSinceLastClick < debounceTime}
                    """.trimIndent())

                if (elapsedTimeSinceLastClick < debounceTime) {
                    Log.d(clickTag, "Double click shielded")
                    return
                }
                else {
                    Log.d(clickTag, "Click happened")
                    action()
                }
                lastClickTime = SystemClock.elapsedRealtime()
            }
        })
    }

    fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.run {
            navigate(direction)
        }
    }


}