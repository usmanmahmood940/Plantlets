package com.example.plantlets.fragments.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.plantlets.Manager.CartManager
import com.example.plantlets.R
import com.example.plantlets.activities.BaseActivity
import com.example.plantlets.activities.SellerHomeActivity
import com.example.plantlets.activities.UserHomeActivity
import com.example.plantlets.databinding.FragmentItemDetailsBinding
import com.example.plantlets.fragments.seller.AddItemFragmentArgs
import com.example.plantlets.models.CartItem
import com.example.plantlets.models.SellerItem
import com.example.plantlets.models.Store
import com.example.plantlets.repositories.LocalRepository
import com.example.plantlets.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ItemDetailsFragment : Fragment() {


    private lateinit var binding: FragmentItemDetailsBinding
    private var item: SellerItem? = null
    var quantity: Int = 1

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var localRepository: LocalRepository

    private var store: Store? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentItemDetailsBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    private fun init() {
        (requireActivity() as UserHomeActivity).hideBottomNav()
        showItem()
        initListeners()
    }

    private fun initListeners() {
        with(binding) {
            rlBack.setOnClickListener {
                findNavController().navigateUp()
            }
            tvQuantity.text = quantity.toString()
            plus.setOnClickListener {
                tvQuantity.text = (++quantity).toString()
            }
            minus.setOnClickListener {
                if (quantity > 1) {
                    tvQuantity.text = (--quantity).toString()
                }
            }
            btnAdd.setOnClickListener {
                store?.let {
                    addToCart(it)
                }?: kotlin.run {
                    localRepository.getStoreFromPref(Constants.STORE_REFRENCE_USER)?.let { store ->
                        addToCart(store)
                    }
                }
            }
            tvAddToCart.setOnClickListener {
                store?.let {
                    addToCart(it)
                }?: kotlin.run {
                    localRepository.getStoreFromPref(Constants.STORE_REFRENCE_USER)?.let { store ->
                        addToCart(store)
                    }
                }
            }
        }
    }

    private fun addToCart(store: Store) {

        item?.let { item ->

            val cartItem = CartItem(
                plantItem = item,
                quantity = quantity,
                totalAmount = quantity * (item.price ?: 0.0)
            )
            cartManager.addToCart(cartItem, store).let {
                if (it) {
                    (requireActivity() as BaseActivity).showAlert(
                        title = getString(R.string.information),
                        message = "Item Added in Cart"
                    )
                } else {
                    (requireActivity() as BaseActivity).showAlert(
                        title = getString(R.string.error),
                        message = "Please Clear Pending Cart"
                    )
                }
                findNavController().navigateUp()
            }
        }


    }

    private fun showItem() {
        val args: ItemDetailsFragmentArgs by navArgs()
        item = args.item
        store = args.store
        item?.let {
            with(binding) {
                Glide.with(ivItemImage.context).load(it.image).into(ivItemImage)
                tvItemName.setText(it.name)
                tvDetails.setText(it.details)
                tvItemPrice.setText(it.price.toString())
            }
        }
    }

}