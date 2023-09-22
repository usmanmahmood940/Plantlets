package com.example.plantlets.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.plantlets.activities.BaseActivity
import com.example.plantlets.R
import com.example.plantlets.databinding.ActivitySignupBinding
import com.example.plantlets.interfaces.CustomSuccessFailureListener
import com.example.plantlets.utils.Extensions.getImprovedBitmap
import com.example.plantlets.utils.Extensions.isValidEmail
import com.example.plantlets.utils.Extensions.showError
import com.example.plantlets.utils.Extensions.toUri


class SignupActivity : BaseActivity() {
    private lateinit var binding: ActivitySignupBinding

    //    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var signupListener: CustomSuccessFailureListener
    private var imageUri: Uri? = null

    private val pickImage: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.tvUploadImage.visibility = View.GONE
                binding.ivProfileImage.setImageURI(uri)

            }
        }

    private val takePhotoLauncher: ActivityResultLauncher<Void?> =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                binding.tvUploadImage.visibility = View.GONE
                binding.ivProfileImage.setImageBitmap(bitmap.getImprovedBitmap())
                imageUri = bitmap.toUri(applicationContext)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()

    }

    private fun setListeners() {
        with(binding) {
            rgType.setOnCheckedChangeListener { group, checkedId ->
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

            when (rgType.checkedRadioButtonId) {
                R.id.rb_user -> {

                }
                R.id.rb_vendor -> {

                }
            }

            btnSignup.setOnClickListener {

            }
        }
    }

    private fun signup() {
         if(checkFormValidation()){

         }

    }

    private fun checkFormValidation(): Boolean {
        with(binding) {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val name = etName.text.toString().trim()
            val mobileNumber = etMobileNum.text.toString().trim()
            val storeName = etStore.text.toString().trim()

            when {
                email.isBlank() -> {
                    etEmail.showError(getString(R.string.email_required_error))
                }
                !email.isValidEmail() -> {
                    etEmail.showError(getString(R.string.email_valid_error))
                }
                password.isBlank() -> {
                    etPassword.showError(getString(R.string.password_required_error))
                }
                name.isBlank() -> {
                    etName.showError(getString(R.string.field_required_error))
                }
                mobileNumber.isBlank() -> {
                    etMobileNum.showError(getString(R.string.field_required_error))
                }
                storeName.isBlank() ->{
                    etStore.showError(getString(R.string.field_required_error))
                }
                (imageUri == null) -> {
                    showAlert(
                        getString(R.string.error),
                        getString(R.string.upload_image)
                    )
                }
                else -> {
                    return true
                }
            }
        }
        return false
    }

}