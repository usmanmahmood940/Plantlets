package com.example.plantlets.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.plantlets.R
import com.example.plantlets.databinding.ActivityLoginBinding
import com.example.plantlets.interfaces.CustomSuccessFailureListener
import com.example.plantlets.models.Store
import com.example.plantlets.utils.Extensions.isValidEmail
import com.example.plantlets.utils.Extensions.showError
import com.example.plantlets.utils.Extensions.togglePasswordVisibility
import com.example.plantlets.viewmodels.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loginListener: CustomSuccessFailureListener

    @Inject
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setChildView(binding.root)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        initializeLoginListener()
        setupListeners()


    }

    private fun initializeLoginListener() {
        loginListener = object : CustomSuccessFailureListener {
            override fun onSuccess() {
                hideProgressBar().also {
                    binding.svLogin.alpha = 1f
                }
                auth.currentUser?.let {
                    startActivity(Intent(this@LoginActivity, getNavigation()))
                }

            }

            override fun onFailure(errorMessage: String?) {
                hideProgressBar().also {
                    binding.btnLogin.isEnabled = true
                }
                showAlert(
                    title = getString(R.string.error),
                    message = errorMessage.toString()
                )



            }

        }
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            login()
//            startActivity(Intent(this, SellerHomeActivity::class.java))
        }

        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.ivEye.setOnClickListener{
            binding.etPassword.togglePasswordVisibility()
        }

    }

    fun login() {
        if (checkFormValidation()) {
            showProgressBar().also {
                binding.svLogin.alpha = 0.5f
                binding.btnLogin.isEnabled = false
            }
            loginViewModel.login(loginViewModel.email, loginViewModel.password,loginListener)
        }
    }

    private fun checkFormValidation(): Boolean {
        with(binding) {
            with(loginViewModel) {
                email = etEmail.text.toString().trim()
                password = etPassword.text.toString().toString()
                when {
                    email.isBlank() -> {
                        etEmail.showError(getString(com.example.plantlets.R.string.email_required_error))
                    }
                    !email.isValidEmail() -> {
                        etEmail.showError(getString(com.example.plantlets.R.string.email_valid_error))
                    }
                    password.isBlank() -> {
                        etPassword.showError(getString(com.example.plantlets.R.string.password_required_error))
                    }
                    else -> {
                        return true
                    }
                }
            }
        }
        return false
    }

//    private fun checkFormValidation(): Boolean {
//        with(binding) {
//            with(login) {
//                email = etEmail.text.toString().trim()
//                password = etPassword.text.toString().trim()
//                name = etName.text.toString().trim()
//                mobileNumber = etMobileNum.text.toString().trim()
//
//                when {
//                    email.isBlank() -> {
//                        etEmail.showError(getString(com.example.plantlets.R.string.email_required_error))
//                    }
//                    !email.isValidEmail() -> {
//                        etEmail.showError(getString(com.example.plantlets.R.string.email_valid_error))
//                    }
//                    password.isBlank() -> {
//                        etPassword.showError(getString(com.example.plantlets.R.string.password_required_error))
//                    }
//                    name.isBlank() -> {
//                        etName.showError(getString(com.example.plantlets.R.string.field_required_error))
//                    }
//                    mobileNumber.isBlank() -> {
//                        etMobileNum.showError(getString(com.example.plantlets.R.string.field_required_error))
//                    }
//                    (imageUri == null) -> {
//                        showAlert(
//                            getString(com.example.plantlets.R.string.error),
//                            getString(com.example.plantlets.R.string.upload_image)
//                        )
//                    }
//                    else -> {
//                        return true
//                    }
//                }
//            }
//        }
//        return false
//    }


    private fun makeAccount() {
        val db = Firebase.firestore
        val email = " usman@gmail.com"
        val store = Store(email)


        db.collection("Stores").document(email).collection("Items").document(email).set(store)
//        db.collection("Stores").addSnapshotListener { snapshotArray, e ->
//            if (e != null) {
//                Log.w("ERROR", "Listen failed.", e)
//                return@addSnapshotListener
//            }
//
//            if (snapshotArray != null) {
//                for (snapshot in snapshotArray) {
//                    if (snapshot != null && snapshot.exists()) {
//                        Log.d("DATA-TAG", "Current data: ${snapshot.data}")
//                    } else {
//                        Log.d("TAG", "Current data: null")
//                    }
//                }
//            }
//        }
    }
}