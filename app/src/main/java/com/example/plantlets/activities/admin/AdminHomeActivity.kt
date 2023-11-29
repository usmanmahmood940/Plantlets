package com.example.plantlets.activities.admin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantlets.R
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.activities.BaseActivity
import com.example.plantlets.adapters.AdminStoreItemAdapter
import com.example.plantlets.databinding.ActivityAdminHomeBinding
import com.example.plantlets.interfaces.StoreClickListener
import com.example.plantlets.models.Store
import com.example.plantlets.viewmodels.user.AdminViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
@AndroidEntryPoint
class AdminHomeActivity : BaseActivity(), StoreClickListener {

    private lateinit var binding : ActivityAdminHomeBinding
    private lateinit var adminStoreItemAdapter: AdminStoreItemAdapter
    lateinit var adminViewModel: AdminViewModel
    @Inject
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        adminViewModel = ViewModelProvider(this).get(AdminViewModel::class.java)

        setContentView(binding.root)
        init()
        observeStoreList()
        setupSearchBar()
    }
    override fun onResume() {
        super.onResume()
        adminViewModel.setStore(null)
        adminViewModel.startObserving()
    }

    override fun onPause() {
        super.onPause()
        adminViewModel.stopObserving()
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
            adminViewModel.storeList.collect { response ->
                when (response) {
                    is CustomResponse.Success -> {
                        hideProgressBar()
                        response.data?.let { storeList ->
                            val list = adminViewModel.getStoresBySearch(adminViewModel.query ?: "")
                            adminStoreItemAdapter.submitList(list)
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
        val intent = Intent(this, EditStoreActivity::class.java)
        intent.putExtra("storeName", store.storeName)
        intent.putExtra("storeAddress", store.storeAddress)
        intent.putExtra("storeStatus", store.status)
        intent.putExtra("storeEmail", store.email)
        this.startActivity(intent)

    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                val query = editable.toString()
                adminViewModel.query = query
                val list = adminViewModel.getStoresBySearch(query)
                adminStoreItemAdapter.submitList(list)
            }
        })
    }


}