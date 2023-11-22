package com.example.plantlets.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantlets.R
import com.example.plantlets.adapters.OrderItemAdapter
import com.example.plantlets.databinding.FragmentOrderDetailsBinding
import com.example.plantlets.models.Order


class OrderDetailsFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailsBinding
    private var orderDetailVisibility = true
    private var deliveryDetailVisibility = true
    private var order:Order?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                    findNavController().popBackStack(R.id.cartFragment,true)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun init() {
        val args: OrderDetailsFragmentArgs by navArgs()
        order = args.order

        binding.order = order
        binding.orderDetail = orderDetailVisibility
        binding.deliveryDetail = deliveryDetailVisibility
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
            hideShowOrderDetails()
        }
        binding.arrowDeliveryDetails.setOnClickListener {
            hideShowDeliveryDetails()
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack(R.id.cartFragment,true)
        }
    }

    private fun hideShowDeliveryDetails() {
        deliveryDetailVisibility = !deliveryDetailVisibility
        binding.deliveryDetail = deliveryDetailVisibility
        binding.invalidateAll()
        if (deliveryDetailVisibility)
            binding.layoutDeliveryDetails.visibility = View.VISIBLE
        else
            binding.layoutDeliveryDetails.visibility = View.GONE
    }

    fun hideShowOrderDetails() {
        orderDetailVisibility = !orderDetailVisibility
        binding.orderDetail = orderDetailVisibility
        binding.invalidateAll()
        if (orderDetailVisibility)
            binding.layoutOrderDetails.visibility = View.VISIBLE
        else
            binding.layoutOrderDetails.visibility = View.GONE

    }



}