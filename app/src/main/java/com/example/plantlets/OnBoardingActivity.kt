package com.example.plantlets

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.example.plantlets.databinding.ActivityOnBoardingBinding

private lateinit var binding:ActivityOnBoardingBinding

class OnBoardingActivity : BaseActivity() {
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
            startActivity(Intent(this@OnBoardingActivity,LoginActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

    }

}