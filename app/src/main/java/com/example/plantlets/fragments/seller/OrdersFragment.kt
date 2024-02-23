package com.example.plantlets.fragments.seller

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.plantlets.activities.UserHomeActivity
import com.example.plantlets.adapters.OrderAdapter
import com.example.plantlets.databinding.FragmentOrdersBinding
import com.example.plantlets.fragments.user.UserOrdersFragmentDirections
import com.example.plantlets.models.Order
import com.example.plantlets.viewmodels.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint

class OrdersFragment : Fragment() {

    lateinit var binding:FragmentOrdersBinding
    lateinit var ordersViewModel:OrdersViewModel
    lateinit var orderAdapter: OrderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentOrdersBinding.inflate(inflater, container, false)
        ordersViewModel = ViewModelProvider(this).get(OrdersViewModel::class.java)
        init()
        setupSearchBar()
        return binding.root
    }

    private fun init() {
        orderAdapter = OrderAdapter(listener = object: OrderAdapter.OrderClickListener{
            override fun onClick(order: Order) {
                openOrderDetail(order)
            }
        },true)
        binding.rvUserOrderList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }
        binding.cvSearchBar.visibility = View.VISIBLE

        observeOrderList()
    }

    private fun openOrderDetail(order: Order) {
        if (findNavController().currentDestination?.id == R.id.ordersFragment) {
            val action = OrdersFragmentDirections.actionOrdersFragmentToOrderDetailsFragment2(order)
            findNavController().navigate(action)
        }
    }

    private fun observeOrderList() {
        lifecycleScope.launch {
            ordersViewModel.ordersList.collect { response ->
                when (response) {
                    is CustomResponse.Success -> {
                        (requireActivity() as BaseActivity).hideProgressBar()
                        response.data?.let { orderList ->
                            val list = ordersViewModel.getOrdersBySearch(ordersViewModel.query?:"")
                            orderAdapter.submitList(list)
                        }

                    }
                    is CustomResponse.Loading -> {
                        (requireActivity() as BaseActivity).showProgressBar()
                    }

                    is CustomResponse.Error -> {
                        (requireActivity() as BaseActivity).apply {
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

    override fun onResume() {
        super.onResume()
        ordersViewModel.startObserving()
    }

    override fun onStop() {
        super.onStop()
        ordersViewModel.stopObserving()
    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                val query = editable.toString()
                ordersViewModel.query = query
                val list = ordersViewModel.getOrdersBySearch(query)
                orderAdapter.submitList(list)
            }
        })
    }


}