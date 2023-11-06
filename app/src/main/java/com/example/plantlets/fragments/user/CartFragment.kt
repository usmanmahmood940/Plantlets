package com.example.plantlets.fragments.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantlets.Manager.CartManager
import com.example.plantlets.R
import com.example.plantlets.activities.UserHomeActivity
import com.example.plantlets.adapters.CartAdapter
import com.example.plantlets.databinding.FragmentCartBinding
import com.example.plantlets.interfaces.CartItemTouchHelperCallback
import com.example.plantlets.models.Amounts
import com.example.plantlets.models.CartItem
import com.example.plantlets.models.SellerItem
import com.example.plantlets.viewmodels.user.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint

class CartFragment : Fragment(),CartAdapter.OnCartListener {

    private lateinit var binding:FragmentCartBinding
    private lateinit var cartViewModel:CartViewModel

    @Inject
    lateinit var cartManager: CartManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false)
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        (requireActivity() as UserHomeActivity).apply {
            changeBottomNavColor()
        }
        init()
        return binding.root
    }
    private fun init() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            amounts = Amounts()
            val cartAdapter = CartAdapter(listener = this@CartFragment)
            rvCart.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = cartAdapter
            }
            setupItemTouchHelper(cartAdapter)

            btnCheckout.setOnClickListener {
                if (findNavController().currentDestination?.id == R.id.cartFragment) {
                    val action = CartFragmentDirections.actionCartFragmentToCheckoutFragment(amounts)
                    findNavController().navigate(action)
                }
            }

        }
        observeCartItems()

    }

    private fun setupItemTouchHelper(cartAdapter: CartAdapter) {
        val itemTouchHelperCallback = CartItemTouchHelperCallback(cartAdapter)
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvCart)
    }

    private fun observeCartItems() {
        cartViewModel.cartItemList.observe(viewLifecycleOwner) { cartItems ->
            if (cartItems.isNotEmpty()) {
                showCartItems(cartItems)
            } else {
                showEmptyCart()
            }
        }
    }

    private fun showCartItems(cartItems: List<CartItem>) {
        binding.apply {
            tvCartEmpty.visibility = View.GONE
            svCart.visibility = View.VISIBLE

            val amounts = amounts ?: return
            val totalAmount = cartItems.sumOf { it.totalAmount }
            amounts.updateTotalItemAmount(totalAmount)

            val cartAdapter = binding.rvCart.adapter as? CartAdapter
            cartAdapter?.setList(cartItems)

            invalidateAll()
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            svCart.visibility = View.GONE
            tvCartEmpty.visibility = View.VISIBLE
            btnCheckout.visibility = View.GONE
        }
    }



    override fun onStop() {
        super.onStop()
        (requireActivity() as UserHomeActivity).apply {
            changeIconCart()
        }
    }

    override fun onCartUpdate() {
        cartViewModel.updateCart()
    }

    override fun onItemRemove(position: Int) {
        cartViewModel.removeItem(position)
    }

    override fun onItemClick(plantItem: SellerItem) {
        if (findNavController().currentDestination?.id == R.id.cartFragment) {
            val action = CartFragmentDirections.actionCartFragmentToItemDetails(plantItem,cartManager.store)
            findNavController().navigate(action)
        }
    }

//    private fun navigateToCheckout() {
//        val amounts = binding.amounts ?: return
//        val action = CartFragmentDirections.actionCartFragmentToCheckoutFragment(amounts = amounts)
//        findNavController().navigate(action)
//    }

}