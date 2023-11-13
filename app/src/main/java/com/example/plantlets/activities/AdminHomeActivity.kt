package com.example.plantlets.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantlets.R
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.adapters.AdminStoreItemAdapter
import com.example.plantlets.adapters.StoreItemAdapter
import com.example.plantlets.databinding.ActivityAdminHomeBinding
import com.example.plantlets.interfaces.StoreClickListener
import com.example.plantlets.models.Store
import com.example.plantlets.viewmodels.user.StoreListingViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
@AndroidEntryPoint
class AdminHomeActivity : BaseActivity(), StoreClickListener {

    private lateinit var binding : ActivityAdminHomeBinding
    private lateinit var adminStoreItemAdapter: AdminStoreItemAdapter
    lateinit var storeListingViewModel: StoreListingViewModel
    @Inject
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        storeListingViewModel = ViewModelProvider(this).get(StoreListingViewModel::class.java)

        setContentView(binding.root)
        init()
        observeStoreList()
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
        adminStoreItemAdapter = AdminStoreItemAdapter(this)
        with(binding) {
            rvStoresList.apply {
                layoutManager =
                    LinearLayoutManager(this@AdminHomeActivity)
                adapter = adminStoreItemAdapter
            }
        }
    }
    private fun observeStoreList() {
        lifecycleScope.launch {
            storeListingViewModel.storeList.collect { response ->
                when (response) {
                    is CustomResponse.Success -> {
                        hideProgressBar()
                        response.data?.let { storeList ->
                            adminStoreItemAdapter.submitList(storeList)
                        }

                    }

                    is CustomResponse.Loading -> {
                        showProgressBar()
                    }

                    is CustomResponse.Error -> {
                            hideProgressBar()
                            showAlert(
                                title = getString(R.string.error), message = response.errorMessage
                            )


                    }
                }
            }
        }
    }

    override fun onClick(store: Store) {

    }
}