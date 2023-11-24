package com.example.plantlets.fragments.user

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
import com.example.plantlets.activities.UserHomeActivity
import com.example.plantlets.adapters.OrderAdapter
import com.example.plantlets.databinding.FragmentUserOrdersBinding
import com.example.plantlets.models.Order
import com.example.plantlets.viewmodels.user.UserOrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class UserOrdersFragment : Fragment() {

    private lateinit var binding:FragmentUserOrdersBinding
    private lateinit var userOrdersViewModel: UserOrdersViewModel
    private lateinit var orderAdapter: OrderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentUserOrdersBinding.inflate(inflater, container, false)
        userOrdersViewModel = ViewModelProvider(this).get(UserOrdersViewModel::class.java)
        init()
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        userOrdersViewModel.startObserving()
    }


    override fun onStop() {
        super.onStop()
        userOrdersViewModel.stopObserving()
    }

    private fun init() {
        orderAdapter = OrderAdapter(listener = object: OrderAdapter.OrderClickListener{
            override fun onClick(order: Order) {
                openOrderDetail(order)
            }
        })
        binding.rvUserOrderList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }

        observeOrderList()
    }

    private fun openOrderDetail(order: Order) {
        if (findNavController().currentDestination?.id == R.id.userOrdersFragment) {
            val action = UserOrdersFragmentDirections.actionUserOrdersFragmentToOrderDetailsFragment2(order)
            findNavController().navigate(action)
        }
    }

    private fun observeOrderList() {
        lifecycleScope.launch {
            userOrdersViewModel.ordersList.collect { response ->
                when (response) {
                    is CustomResponse.Success -> {
                        (requireActivity() as UserHomeActivity).hideProgressBar()
                        response.data?.let { storeList ->
                            orderAdapter.submitList(storeList)
                        }

                    }
                    is CustomResponse.Loading -> {
                        (requireActivity() as UserHomeActivity).showProgressBar()
                    }

                    is CustomResponse.Error -> {
                        (requireActivity() as UserHomeActivity).apply {
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


}