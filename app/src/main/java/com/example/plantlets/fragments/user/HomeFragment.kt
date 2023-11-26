package com.example.plantlets.fragments.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantlets.R
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.activities.UserHomeActivity
import com.example.plantlets.adapters.UserItemAdapter
import com.example.plantlets.databinding.FragmentHomeBinding
import com.example.plantlets.fragments.seller.AddItemFragmentArgs
import com.example.plantlets.interfaces.ItemClickListener
import com.example.plantlets.interfaces.UserItemClickListener
import com.example.plantlets.models.SellerItem
import com.example.plantlets.models.Store
import com.example.plantlets.utils.CenterItemZoomScrollListener
import com.example.plantlets.viewmodels.user.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), UserItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    lateinit var itemAdapter: UserItemAdapter
    private lateinit var homeViewModel: HomeViewModel
    var store: Store? = null

    @Inject
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        (requireActivity() as UserHomeActivity).showBottomNav()
        (requireActivity() as UserHomeActivity).changeBottomNavColor(R.color.green_grey)

        val args: HomeFragmentArgs by navArgs()
        store = args.store

        init()
        observeList()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.startObserving()

    }

    override fun onPause() {
        super.onPause()
        homeViewModel.stopObserving()


    }

    private fun init() {
        setupStore()
        with(binding) {
            itemAdapter = UserItemAdapter(this@HomeFragment)
            rvPopular.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = itemAdapter
                addOnScrollListener(CenterItemZoomScrollListener(this))
            }

            rlBack.setOnClickListener {
                findNavController().navigateUp()
            }
            etSearch.setOnClickListener {
                if (findNavController().currentDestination?.id == R.id.homeFragment) {
                    val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
                    findNavController().navigate(action)
                }
            }

            tvViewAll.setOnClickListener {


            }
        }
    }

    private fun setupStore() {
//        auth.currentUser?.apply {
//            binding.tvStoreName.text = "$displayName"
//            homeViewModel.getUserData()?.image?.let{
//                Glide.with(requireContext()).load(it).into(binding.ivProfilePic)
//            }
//
//            Log.d("USMAN-TAG",photoUrl.toString())
//        }
        store?.apply {
            binding.tvStore.text = storeName
        }
    }

    fun observeList() {
        lifecycleScope.launch {
            homeViewModel.itemList.collect { response ->
                when (response) {
                    is CustomResponse.Success -> {
                        (requireActivity() as UserHomeActivity).hideProgressBar()
                        response.data?.let { itemList ->
                            binding.progressBarRv.visibility = View.GONE
                            if (itemList.isEmpty()) {
                                binding.tvNoItems.visibility = View.VISIBLE

                            } else {
                                binding.rvPopular.visibility = View.VISIBLE
                                val list = homeViewModel.sortByPopularity(itemList)
                                itemAdapter.submitList(list)
                            }

                        }

                    }

                    is CustomResponse.Loading -> {
                        (requireActivity() as UserHomeActivity).showProgressBar()
                        binding.progressBarRv.visibility = View.VISIBLE
                        binding.rvPopular.visibility = View.GONE
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
                        binding.rvPopular.visibility = View.GONE
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

    override fun onClick(item: SellerItem) {
        if (findNavController().currentDestination?.id == R.id.homeFragment) {
            val action = HomeFragmentDirections.actionHomeFragmentToItemDetails(item,null)
            findNavController().navigate(action)
        }
    }


}