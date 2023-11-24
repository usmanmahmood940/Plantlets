package com.example.plantlets.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantlets.R
import com.example.plantlets.adapters.OrderItemAdapter
import com.example.plantlets.databinding.FragmentOrderDetailsBinding
import com.example.plantlets.models.Order
import com.example.plantlets.models.Store
import com.example.plantlets.viewmodels.OrderDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailsFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailsBinding
    private lateinit var orderDetailsViewModel: OrderDetailsViewModel
    private var order:Order?=null
    private var store: Store?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args: OrderDetailsFragmentArgs by navArgs()
        order = args.order
        orderDetailsViewModel = ViewModelProvider(this).get(OrderDetailsViewModel::class.java)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        init()
        setBackPress()

        return binding.root
    }

    private fun setBackPress() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    back()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun back() {
        val previousBackStackEntry = findNavController().previousBackStackEntry
        if (previousBackStackEntry != null) {
            when (previousBackStackEntry.destination.id) {
                R.id.userOrdersFragment -> {
                    findNavController().navigateUp()
                }
                R.id.checkoutFragment -> {
                    findNavController().popBackStack(R.id.cartFragment, false)
                }
                else -> {
                    // Handle other cases if necessary
                }
            }
        }
    }

    private fun init() {
        binding.order = order
        binding.orderDetail = true
        binding.deliveryDetail = true
        binding.itemDetails = true
        initItemList()
        initListners()
    }

    private fun initItemList() {
        order?.cartItemList?.let {
            binding.rvOrderItemList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter =OrderItemAdapter(it)
            }
        }
    }

    private fun initListners() {


        binding.arrowOrderDetails.setOnClickListener {
            binding.orderDetail =  binding.orderDetail?.let { !it }
            binding.invalidateAll()
        }
        binding.arrowDeliveryDetails.setOnClickListener {
            binding.deliveryDetail = binding.deliveryDetail?.let { !it }
            binding.invalidateAll()
        }
        binding.arrowItemDetails.setOnClickListener {
            binding.itemDetails =  binding.itemDetails?.let { !it }
            binding.invalidateAll()
        }
        binding.arrowStoreDetails.setOnClickListener {
            binding.storeDetail =  binding.storeDetail?.let { !it }
            binding.invalidateAll()
        }

        binding.ivBack.setOnClickListener {
            back()
        }
    }




}