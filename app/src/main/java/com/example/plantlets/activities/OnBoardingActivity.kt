package com.example.plantlets.activities

import android.content.Intent
import android.os.Bundle
import com.example.plantlets.databinding.ActivityOnBoardingBinding



class OnBoardingActivity : BaseActivity() {
    private lateinit var binding:ActivityOnBoardingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init(){
        setupListener()
    }

    private fun setupListener() {
        binding.btnContinue.setOnClickListener {
            startActivity(Intent(this@OnBoardingActivity, LoginActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

    }

}