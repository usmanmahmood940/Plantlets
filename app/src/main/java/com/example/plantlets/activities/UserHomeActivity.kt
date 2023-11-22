package com.example.plantlets.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.plantlets.R
import com.example.plantlets.databinding.ActivityUserHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserHomeActivity : BaseActivity() {
    private lateinit var binding:ActivityUserHomeBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavController()
        setupCartButton()
    }

    private fun setupNavController() {
        navController = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)!!
            .findNavController()
        binding.bottomNavigationView.setupWithNavController(navController)



    }

    fun hideBottomNav() {
        binding.coordinatorLayout.visibility = View.GONE

    }
    fun showBottomNav(){
        binding.coordinatorLayout.visibility = View.VISIBLE
    }

    fun openHome(){
        binding.bottomNavigationView.selectedItemId = R.id.homeFragment
    }

    private fun setupCartButton() {
        binding.btnCart.setOnClickListener {
            binding.bottomNavigationView.apply {
                binding.btnCart.setImageDrawable(getResources().getDrawable(R.drawable.ic_cart_filled))
                menu.findItem(R.id.nav_cart)?.isEnabled = true
                selectedItemId = R.id.nav_cart
                menu.findItem(R.id.nav_cart)?.isEnabled = false
            }
        }
//        binding.btnCart.count = cartManager.getCartCount()
    }
    fun changeIconCart(){
        binding.btnCart.setImageDrawable(getResources().getDrawable(R.drawable.ic_cart_outlined))

    }

    fun changeBottomNavColor(color:Int= R.color.green_grey){
        binding.coordinatorLayout.setBackgroundResource(color)
    }
}