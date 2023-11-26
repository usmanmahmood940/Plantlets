package com.example.plantlets.fragments.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.plantlets.R
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.activities.UserHomeActivity
import com.example.plantlets.adapters.StoreItemAdapter
import com.example.plantlets.databinding.FragmentStoreListingBinding
import com.example.plantlets.interfaces.StoreClickListener
import com.example.plantlets.models.Store
import com.example.plantlets.viewmodels.user.StoreListingViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StoreListingFragment : Fragment(),StoreClickListener {

    lateinit var binding:FragmentStoreListingBinding
    lateinit var storeItemAdapter: StoreItemAdapter
    lateinit var storeListingViewModel: StoreListingViewModel

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
        binding = FragmentStoreListingBinding.inflate(inflater, container, false)
        storeListingViewModel = ViewModelProvider(this).get(StoreListingViewModel::class.java)
        (requireActivity() as UserHomeActivity).apply {
            changeBottomNavColor(R.color.white)
            showBottomNav()
        }

        init()
        observeStoreList()
        setupSearchBar()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        storeListingViewModel.setStore(null)
        storeListingViewModel.startObserving()
    }

    override fun onPause() {
        super.onPause()
        storeListingViewModel.stopObserving()
    }

    private fun init() {
        setupUserProfile()
        with(binding) {
            storeItemAdapter = StoreItemAdapter(this@StoreListingFragment)
            rvStores.apply {
                layoutManager =
                    LinearLayoutManager(requireContext())
                adapter = storeItemAdapter
            }
        }
//        val list = mutableListOf<Store>(
//            Store(email = "usman@gmail.com", storeName = "Aroma Gardens", storeAddress ="Cantt,Lahore"),
//            Store(email = "humza@gmail.com", storeName = "Aroma Gardens", storeAddress ="Cantt,Lahore"),
//            Store(email = "hassan@gmail.com", storeName = "Aroma Gardens", storeAddress ="Cantt,Lahore"),
//            Store(email = "ahmad@gmail.com", storeName = "Aroma Gardens", storeAddress ="Cantt,Lahore")
//        )
//        storeItemAdapter.submitList(list)
    }

    private fun observeStoreList() {
        lifecycleScope.launch {
            storeListingViewModel.storeList.collect { response ->
                when (response) {
                    is CustomResponse.Success -> {
                        (requireActivity() as UserHomeActivity).hideProgressBar()
                        response.data?.let { storeList ->
                            val list =
                                storeListingViewModel.getStoresBySearch(storeListingViewModel.query ?: "")
                            storeItemAdapter.submitList(list)
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


    private fun setupUserProfile() {
        auth.currentUser?.apply {
            binding.tvUserName.text = "Hey $displayName"
            storeListingViewModel.getUserData()?.let {
                it.image?.let {image ->
                    Glide.with(binding.ivProfilePic.context).load(image).into(binding.ivProfilePic)
                }
            }

            Log.d("USMAN-TAG",photoUrl.toString())
        }
    }
    override fun onClick(store: Store) {
        if (findNavController().currentDestination?.id == R.id.storeListingFragment) {
            storeListingViewModel.setStore(store)
            val action = StoreListingFragmentDirections.actionStoreListingFragmentToHomeFragment(store)
            findNavController().navigate(action)
        }
    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                val query = editable.toString()
                storeListingViewModel.query = query
                val list = storeListingViewModel.getStoresBySearch(query)
                storeItemAdapter.submitList(list)
            }
        })
    }


}