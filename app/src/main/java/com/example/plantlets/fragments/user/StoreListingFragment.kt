package com.example.plantlets.fragments.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.plantlets.R
import com.example.plantlets.activities.UserHomeActivity
import com.example.plantlets.adapters.StoreItemAdapter
import com.example.plantlets.adapters.UserItemAdapter
import com.example.plantlets.databinding.FragmentHomeBinding
import com.example.plantlets.databinding.FragmentStoreListingBinding
import com.example.plantlets.fragments.seller.ItemsFragmentDirections
import com.example.plantlets.interfaces.StoreClickListener
import com.example.plantlets.models.Store
import com.example.plantlets.utils.CenterItemZoomScrollListener
import com.example.plantlets.viewmodels.user.StoreListingViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
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
        (requireActivity() as UserHomeActivity).changeBottomNavColor(R.color.white)

        init()
        return binding.root
    }

    private fun init() {
//        setupUserProfile()
        with(binding) {
            storeItemAdapter = StoreItemAdapter(this@StoreListingFragment)
            rvStores.apply {
                layoutManager =
                    LinearLayoutManager(requireContext())
                adapter = storeItemAdapter
            }
        }
        val list = mutableListOf<Store>(
            Store(email = "usman@gmail.com", storeName = "Aroma Gardens", storeAddress ="Cantt,Lahore"),
            Store(email = "humza@gmail.com", storeName = "Aroma Gardens", storeAddress ="Cantt,Lahore"),
            Store(email = "hassan@gmail.com", storeName = "Aroma Gardens", storeAddress ="Cantt,Lahore"),
            Store(email = "ahmad@gmail.com", storeName = "Aroma Gardens", storeAddress ="Cantt,Lahore")
        )
        storeItemAdapter.submitList(list)
    }

    private fun setupUserProfile() {
        auth.currentUser?.apply {
            binding.tvUserName.text = "$displayName"

//            storeListingViewModel.getUserData()?.image?.let{
//                Glide.with(requireContext()).load(it).into(binding.ivProfilePic)
//            }

            Log.d("USMAN-TAG",photoUrl.toString())
        }
    }
    override fun onClick(store: Store) {
        if (findNavController().currentDestination?.id == R.id.storeListingFragment) {
            val action = StoreListingFragmentDirections.actionStoreListingFragmentToHomeFragment(store)
            findNavController().navigate(action)
        }
    }


}