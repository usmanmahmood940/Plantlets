package com.example.plantlets.fragments

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantlets.R
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.activities.BaseActivity
import com.example.plantlets.activities.SellerHomeActivity
import com.example.plantlets.adapters.SellerItemAdapter
import com.example.plantlets.databinding.FragmentItemsBinding
import com.example.plantlets.interfaces.CategoryClickListener
import com.example.plantlets.interfaces.ItemClickListener
import com.example.plantlets.models.Category
import com.example.plantlets.models.SellerItem
import com.example.plantlets.viewmodels.SellerItemViewModel
import kotlinx.coroutines.launch


class ItemsFragment : Fragment(), ItemClickListener {

    private lateinit var binding: FragmentItemsBinding
    lateinit var itemAdapter: SellerItemAdapter
    private lateinit var itemViewModel: SellerItemViewModel
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
        itemAdapter = SellerItemAdapter(emptyList(), this)
        binding.rvSellerItemList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemAdapter
        }
    }


    private fun initListeners() {
        binding.fabItem.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.itemsFragment) {
                val action = ItemsFragmentDirections.actionItemsFragmentToAddItemFragment(null)
                findNavController().navigate(action)
            }
        }


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
                        response.data?.let { categoryList ->
                            val list = itemViewModel.getItemByQuery(
                                itemViewModel.query ?: ""
                            )
                            itemAdapter.setList(list)
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
                                title = getString(R.string.error),
                                message = response.errorMessage
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
        (requireActivity() as BaseActivity)
            .showAlert(
                title = "Confirmation",
                message = "Do you really want to delete it",
                positiveButtonText = "Yes",
                positiveButtonClickListener = object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        itemViewModel.deleteItem(item)
                    }

                },
                negativeButtonText = "No",
                negativeButtonClickListener =  object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                       p0?.dismiss()
                    }

                }
            )

    }


}