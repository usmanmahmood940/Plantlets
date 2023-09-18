package com.example.plantlets

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.plantlets.databinding.ActivitySignupBinding

private lateinit var binding: ActivitySignupBinding

class SignupActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when(binding.rgType.checkedRadioButtonId){
            R.id.rb_user ->{

            }
            R.id.rb_vendor ->{

            }
        }

    }

}