package com.example.plantlets.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.plantlets.databinding.ActivityLoginBinding
import com.example.plantlets.models.Store
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupListeners()


    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this,SellerHomeActivity::class.java))
        }

        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }


    private fun makeAccount() {
        val db = Firebase.firestore
        val email = " usman@gmail.com"
        val store = Store(email)


//            db.collection("Stores").document(email).collection("Items").document(email).set(store)
        db.collection("Stores").addSnapshotListener { snapshotArray, e ->
            if (e != null) {
                Log.w("ERROR", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshotArray != null) {
                for (snapshot in snapshotArray) {
                    if (snapshot != null && snapshot.exists()) {
                        Log.d("DATA-TAG", "Current data: ${snapshot.data}")
                    } else {
                        Log.d("TAG", "Current data: null")
                    }
                }
            }
        }
    }
}