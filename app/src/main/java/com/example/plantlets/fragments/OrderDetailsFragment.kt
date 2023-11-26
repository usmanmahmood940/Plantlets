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
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.activities.BaseActivity
import com.example.plantlets.adapters.OrderItemAdapter
import com.example.plantlets.databinding.FragmentOrderDetailsBinding
import com.example.plantlets.fragments.user.UserOrdersFragmentDirections
import com.example.plantlets.models.Order
import com.example.plantlets.utils.Constants
import com.example.plantlets.utils.Constants.ORDER_IN_PROGRESS
import com.example.plantlets.utils.Constants.ORDER_PENDING
import com.example.plantlets.viewmodels.OrderDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailsFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailsBinding
    private lateinit var orderDetailsViewModel: OrderDetailsViewModel
    private var myOrder: Order? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args: OrderDetailsFragmentArgs by navArgs()
        myOrder = args.order

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
        handleRatingResult()

        return binding.root
    }

    private fun handleRatingResult() {
        val navBackStackEntry = findNavController().getBackStackEntry(R.id.orderDetailsFragment2)
        val ratingData = navBackStackEntry.savedStateHandle.getLiveData<Bundle>(Constants.RATING)
        ratingData.observe(viewLifecycleOwner) { data ->
            val rating = data.getInt(Constants.RATING)
            myOrder?.rating = rating
            binding.order = myOrder
            binding.invalidateAll()
            orderDetailsViewModel.updateRating(myOrder)
            observeOrder()

        }
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
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun init() {
        checkNav()
        orderDetailsViewModel.store.observe(viewLifecycleOwner, { response ->
            when (response) {
                is CustomResponse.Loading -> {
                    (requireActivity() as BaseActivity).showProgressBar().also {
                        binding.svOrderDetails.alpha = 0.5f
                    }

                }

                is CustomResponse.Success -> {
                    (requireActivity() as BaseActivity).hideProgressBar().also {
                        binding.svOrderDetails.alpha = 1f
                        binding.store = response.data
                    }
                }

                is CustomResponse.Error -> {
                    (requireActivity() as BaseActivity).apply {
                        hideProgressBar()
                        binding.svOrderDetails.alpha = 1f
                        showAlert(
                            title = getString(R.string.error), message = response.errorMessage
                        )
                    }

                }
            }
        })
        myOrder?.storeId?.let {
            orderDetailsViewModel.getStore(it)
        } ?: kotlin.run {
            orderDetailsViewModel.store.value = CustomResponse.Success(null)
        }
        binding.order = myOrder
        binding.orderDetail = true
        binding.deliveryDetail = true
        binding.itemDetails = true
        binding.storeDetail = true
        initItemList()
        initListners()


    }

    private fun checkNav() {
        val previousBackStackEntry = findNavController().previousBackStackEntry
        if (previousBackStackEntry != null) {
            when (previousBackStackEntry.destination.id) {
                R.id.ordersFragment -> {
                    with(binding) {
                        when (myOrder?.orderStatus) {
                            ORDER_PENDING -> {
                                btnCancel.visibility = View.VISIBLE
                                btnCancel.setOnClickListener {
                                    btnConfirm.isEnabled = false
                                    orderDetailsViewModel.cancelOrder(myOrder)
                                    observeOrder()
                                }
                                btnConfirm.visibility = View.VISIBLE
                                btnConfirm.setOnClickListener {
                                    btnCancel.isEnabled = false
                                    orderDetailsViewModel.confirmOrder(myOrder)
                                    observeOrder()
                                }

                            }

                            ORDER_IN_PROGRESS -> {
                                btnCancel.visibility = View.GONE
                                btnConfirm.visibility = View.GONE
                                btnDeliver.visibility = View.VISIBLE
                                btnDeliver.setOnClickListener {
                                    orderDetailsViewModel.deliverOrder(myOrder)
                                    observeOrder()
                                }

                            }

                            Constants.ORDER_IN_DELIVERY -> {
                                btnDeliver.visibility = View.GONE
                                btnCompleted.visibility = View.VISIBLE
                                btnCompleted.setOnClickListener {
                                    orderDetailsViewModel.completeOrder(myOrder)
                                    observeOrder()
                                }
                            }

                            Constants.ORDER_DELIVERED -> {
                                myOrder?.let { tempOrder ->
                                    ratingBar.visibility = View.VISIBLE
                                    ratingBar.setOnClickListener {
                                        ratingBar.rating = tempOrder.rating?.toFloat()!!
                                    }

                                }
                            }

                            else -> {}
                        }

                    }

                }

                R.id.userOrdersFragment -> {
                    with(binding) {
                        when (myOrder?.orderStatus) {
                            Constants.ORDER_DELIVERED -> {
                                myOrder?.rating?.let { rating ->
                                    ratingBar.visibility = View.VISIBLE
                                    ratingBar.setOnClickListener {
                                        ratingBar.rating = rating.toFloat()
                                    }

                                } ?: kotlin.run {
                                    btnRate.visibility = View.VISIBLE
                                    btnRate.setOnClickListener{
                                        if (findNavController().currentDestination?.id == R.id.orderDetailsFragment2) {
                                            val action = OrderDetailsFragmentDirections.actionOrderDetailsFragment2ToOrderRatingFragment()
                                            findNavController().navigate(action)
                                        }
                                    }

                                }
                            }
                        }
                    }
                }

            }
        }
    }

    private fun observeOrder() {
        orderDetailsViewModel.order.observe(viewLifecycleOwner, { response ->
            when (response) {
                is CustomResponse.Loading -> {
                    (requireActivity() as BaseActivity).showProgressBar().also {
                        binding.svOrderDetails.alpha = 0.5f
                    }

                }

                is CustomResponse.Success -> {
                    (requireActivity() as BaseActivity).hideProgressBar().also {
                        binding.svOrderDetails.alpha = 1f
                        myOrder = response.data
                        binding.order = myOrder
                    }
                    checkNav()
                }

                is CustomResponse.Error -> {
                    (requireActivity() as BaseActivity).apply {
                        hideProgressBar()
                        binding.svOrderDetails.alpha = 1f
                        showAlert(
                            title = getString(R.string.error), message = response.errorMessage
                        )
                    }
                    binding.btnConfirm.isEnabled = true
                    binding.btnCancel.isEnabled = true

                }
            }
        })
    }

    private fun initItemList() {
        myOrder?.cartItemList?.let {
            binding.rvOrderItemList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = OrderItemAdapter(it)
            }
        }
    }

    private fun initListners() {


        binding.arrowOrderDetails.setOnClickListener {
            binding.orderDetail = binding.orderDetail?.let { !it }
            binding.invalidateAll()
        }
        binding.arrowDeliveryDetails.setOnClickListener {
            binding.deliveryDetail = binding.deliveryDetail?.let { !it }
            binding.invalidateAll()
        }
        binding.arrowItemDetails.setOnClickListener {
            binding.itemDetails = binding.itemDetails?.let { !it }
            binding.invalidateAll()
        }
        binding.arrowStoreDetails.setOnClickListener {
            binding.storeDetail = binding.storeDetail?.let { !it }
            binding.invalidateAll()
        }

        binding.ivBack.setOnClickListener {
            back()
        }
    }


}