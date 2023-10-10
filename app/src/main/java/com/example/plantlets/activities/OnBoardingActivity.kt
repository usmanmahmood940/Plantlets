package com.example.plantlets.activities

import android.content.Intent
import android.os.Bundle
import com.example.plantlets.databinding.ActivityOnBoardingBinding
import com.example.plantlets.repositories.LocalRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingActivity : BaseActivity() {
    private lateinit var binding:ActivityOnBoardingBinding


    @Inject
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init(){
        auth.currentUser?.let {
            localRepository.getCurrentUserData()?.let {
                startActivity(Intent(this@OnBoardingActivity,getNavigation()))
                finish()
            }
        }
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