package com.example.plantlets.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.plantlets.R
import com.example.plantlets.databinding.ActivitySellerHomeBinding

class SellerHomeActivity : AppCompatActivity() {
    lateinit var binding:ActivitySellerHomeBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_home)


    }

    private fun setupNavController() {
        navController = supportFragmentManager.findFragmentById(R.id.sellerFragmentContainer)!!
            .findNavController()
        binding.bottomNavSeller.setupWithNavController(navController)
    }
}