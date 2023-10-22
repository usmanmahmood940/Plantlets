package com.example.plantlets.fragments.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.plantlets.R
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.Response.ItemSortOptions
import com.example.plantlets.activities.SellerHomeActivity
import com.example.plantlets.activities.UserHomeActivity
import com.example.plantlets.adapters.SellerItemAdapter
import com.example.plantlets.adapters.UserItemAdapter
import com.example.plantlets.databinding.FragmentHomeBinding
import com.example.plantlets.databinding.FragmentItemsBinding
import com.example.plantlets.models.ItemFillter
import com.example.plantlets.utils.CenterItemZoomScrollListener
import com.example.plantlets.viewmodels.SellerItemViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    lateinit var itemAdapter: UserItemAdapter
    private lateinit var itemViewModel: SellerItemViewModel

    @Inject
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        itemViewModel = ViewModelProvider(this).get(SellerItemViewModel::class.java)
        (requireActivity() as UserHomeActivity).showBottomNav()

        init()
        observeList()

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

    private fun init() {
        setupUserProfile()
        with(binding) {
            itemAdapter = UserItemAdapter()
            rvPopular.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = itemAdapter
//                addOnScrollListener(CenterItemZoomScrollListener(this))
            }

            tvViewAll.setOnClickListener {


            }
        }
    }

    private fun setupUserProfile() {
        auth.currentUser?.apply {
            binding.tvStoreName.text = "$displayName"
            Glide.with(this@HomeFragment).load(photoUrl).into(binding.ivProfilePic)
//            println(photoUrl)
        }
    }

    fun observeList() {
        lifecycleScope.launch {
            itemViewModel.itemList.collect { response ->
                when (response) {
                    is CustomResponse.Success -> {
                        (requireActivity() as UserHomeActivity).hideProgressBar()
                        response.data?.let { itemList ->
                            binding.progressBarRv.visibility = View.GONE
                            if(itemList.isEmpty()){
                                binding.tvNoItems.visibility = View.VISIBLE

                            }else {
                                binding.rvPopular.visibility= View.VISIBLE
                                val sortOptions =
                                    ItemFillter(sortOption = ItemSortOptions.Popularity.toString())
                                itemViewModel.sellerItemFillter = sortOptions
                                val list = itemViewModel.filteredItems().toMutableList()
                                itemAdapter.submitList(list)
                            }

                        }

                    }

                    is CustomResponse.Loading -> {
                        (requireActivity() as UserHomeActivity).showProgressBar()
                        binding.progressBarRv.visibility = View.VISIBLE
                        binding.rvPopular.visibility= View.GONE
                        binding.tvNoItems.visibility = View.GONE
                    }

                    is CustomResponse.Error -> {
                        (requireActivity() as UserHomeActivity).apply {
                            hideProgressBar()
                            showAlert(
                                title = getString(R.string.error), message = response.errorMessage
                            )
                        }
                        binding.progressBarRv.visibility = View.GONE
                        binding.rvPopular.visibility= View.GONE
                        binding.tvNoItems.visibility = View.VISIBLE

                    }
                }
            }
        }
    }

    fun getScrollPosition(): Int {
        val secondItemPosition = 1
        val recyclerViewWidth = binding.rvPopular.width
        val itemWidth = binding.rvPopular.getChildAt(0)?.width ?: 0
        return secondItemPosition * itemWidth + (itemWidth / 2) - (recyclerViewWidth / 2)
    }


}