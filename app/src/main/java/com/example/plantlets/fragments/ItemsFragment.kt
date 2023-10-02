package com.example.plantlets.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantlets.R
import com.example.plantlets.adapters.SellerItemAdapter
import com.example.plantlets.databinding.FragmentItemsBinding
import com.example.plantlets.models.SellerItem


class ItemsFragment : Fragment() {

    private lateinit var binding:FragmentItemsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentItemsBinding.inflate(inflater, container, false)

        val sellerItemAdapter = SellerItemAdapter(createDummySellerItemList())
        binding.rvSellerItemList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = sellerItemAdapter
        }
        return binding.root
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


}