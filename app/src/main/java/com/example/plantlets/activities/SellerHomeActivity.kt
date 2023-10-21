package com.example.plantlets.activities

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.plantlets.R
import com.example.plantlets.databinding.ActivitySellerHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellerHomeActivity : BaseActivity() {
    lateinit var binding: ActivitySellerHomeBinding
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerHomeBinding.inflate(layoutInflater)
        setChildView(binding.root)
        setupNavController()


    }

    private fun setupNavController() {
        navController = supportFragmentManager.findFragmentById(R.id.sellerFragmentContainer)!!
            .findNavController()
        binding.bottomNavSeller.setupWithNavController(navController)

        
    }

    fun hideBottomNav() {
        binding.bottomNavSeller.visibility = View.GONE

    }
    fun showBottomNav(){
        binding.bottomNavSeller.visibility = View.VISIBLE
    }


}