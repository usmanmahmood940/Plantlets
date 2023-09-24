package com.example.plantlets.activities

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.plantlets.activities.BaseActivity
import com.example.plantlets.R
import com.example.plantlets.databinding.ActivitySignupBinding
import com.example.plantlets.interfaces.CustomSuccessFailureListener
import com.example.plantlets.utils.Constants.LATITUDE
import com.example.plantlets.utils.Constants.LONGITUDE
import com.example.plantlets.utils.Extensions.getImprovedBitmap
import com.example.plantlets.utils.Extensions.isValidEmail
import com.example.plantlets.utils.Extensions.showError
import com.example.plantlets.utils.Extensions.toUri
import com.example.plantlets.utils.Helper.getAddressFromLocation
import java.util.*


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
                        rlStore.visibility = View.GONE
                        rlAddress.visibility = View.GONE
                        rlPinLocation.visibility = View.GONE
                    }
                    R.id.rb_vendor -> {
                        rlStore.visibility = View.VISIBLE
                        rlAddress.visibility = View.VISIBLE
                        rlPinLocation.visibility = View.VISIBLE
                        etPinLocation.isFocusableInTouchMode = false

                    }
                }
            }

            when (rgType.checkedRadioButtonId) {
                R.id.rb_user -> {

                }
                R.id.rb_vendor -> {

                }
            }

            etPinLocation.setOnClickListener {
                navigateToMap()
            }


            btnSignup.setOnClickListener {
                signup()
            }
        }
    }

    private fun navigateToMap() {
        val intent = Intent(this, PinLocationMapActivity::class.java)
        resultLauncher.launch(intent)
    }

    private fun signup() {
        if (checkFormValidation()) {
            if (checkVendorValidation()) {
                Toast.makeText(this, "Form is Valid", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Vendor Form is InValid", Toast.LENGTH_SHORT).show()

            }
        }
        else{
            Toast.makeText(this, "Form is InValid", Toast.LENGTH_SHORT).show()

        }

    }

    private fun checkVendorValidation(): Boolean {
        with(binding) {
            if (rgType.checkedRadioButtonId == R.id.rb_vendor) {
                val storeName = etStore.text.toString().trim()
                val address = etAddress.text.toString().trim()
                val pinLocation = etPinLocation.text.toString().trim()
                when {
                    storeName.isBlank() -> {
                        etStore.showError(getString(R.string.field_required_error))
                    }
                    address.isBlank() -> {
                        etAddress.showError(getString(R.string.field_required_error))
                    }
                    pinLocation.isBlank() -> {
                        etPinLocation.isFocusableInTouchMode = true
                        etPinLocation.showError(getString(R.string.address_required_error))
                        etPinLocation.isFocusableInTouchMode = false

                    }
                    else -> {
                        return true
                    }
                }
            } else {
                return true
            }
        }
        return false
    }

    private fun checkFormValidation(): Boolean {
        with(binding) {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val name = etName.text.toString().trim()
            val mobileNumber = etMobileNum.text.toString().trim()

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

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                data?.apply {
                    if (hasExtra(LATITUDE) && hasExtra(LONGITUDE)){

                        setAddressFromLocation(getDoubleExtra(LATITUDE,0.0),getDoubleExtra(LONGITUDE,0.0))
                    }
                }
            }
        }


    private fun setAddressFromLocation(latitude: Double, longitude: Double) {
        if(latitude != 0.0 && longitude != 0.0) {
            val geocoder = Geocoder(this, Locale.getDefault())
            binding.etPinLocation.setText(getAddressFromLocation(geocoder, latitude, longitude))
        }
    }

}