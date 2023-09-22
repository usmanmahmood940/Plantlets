package com.example.plantlets.activities

import android.content.Intent
import android.os.Bundle
import com.example.plantlets.databinding.ActivityLoginBinding

private lateinit var binding: ActivityLoginBinding

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

    }
}