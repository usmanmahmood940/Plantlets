package com.example.plantlets.activities

import android.os.Bundle
import android.view.View
import com.example.plantlets.activities.BaseActivity
import com.example.plantlets.R
import com.example.plantlets.databinding.ActivitySignupBinding


private lateinit var binding: ActivitySignupBinding

class SignupActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.rgType.setOnCheckedChangeListener { group, checkedId ->
            // Handle the selected radio button change here
            when (checkedId) {
                R.id.rb_user -> {
                  binding.rlStore.visibility = View.GONE
                }
                R.id.rb_vendor -> {
                    binding.rlStore.visibility = View.VISIBLE
                }
            }
        }

        when(binding.rgType.checkedRadioButtonId){
            R.id.rb_user ->{

            }
            R.id.rb_vendor ->{

            }
        }

    }

}