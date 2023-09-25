package com.example.plantlets.activities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.plantlets.activities.BaseActivity
import com.example.plantlets.R
import com.example.plantlets.databinding.ActivitySignupBinding
import com.example.plantlets.interfaces.CustomSuccessFailureListener
import com.example.plantlets.models.Store
import com.example.plantlets.models.User
import com.example.plantlets.utils.Constants
import com.example.plantlets.utils.Constants.LATITUDE
import com.example.plantlets.utils.Constants.LONGITUDE
import com.example.plantlets.utils.Constants.USER_TYPE
import com.example.plantlets.utils.Constants.VENDOR_TYPE
import com.example.plantlets.utils.Extensions.getImprovedBitmap
import com.example.plantlets.utils.Extensions.isValidEmail
import com.example.plantlets.utils.Extensions.showError
import com.example.plantlets.utils.Extensions.toUri
import com.example.plantlets.utils.Extensions.togglePasswordVisibility
import com.example.plantlets.utils.Helper.getAddressFromLocation
import com.example.plantlets.viewmodels.SignupViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import java.util.*


@AndroidEntryPoint
class SignupActivity : BaseActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var signupViewModel: SignupViewModel
    private lateinit var signupListener: CustomSuccessFailureListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        signupViewModel = ViewModelProvider(this).get(SignupViewModel::class.java)
        initializeSignupListener()
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
                        signupViewModel.type = USER_TYPE
                    }
                    R.id.rb_vendor -> {
                        rlStore.visibility = View.VISIBLE
                        rlAddress.visibility = View.VISIBLE
                        rlPinLocation.visibility = View.VISIBLE
                        etPinLocation.isFocusableInTouchMode = false
                        signupViewModel.type = VENDOR_TYPE
                    }
                }
            }

//            when (rgType.checkedRadioButtonId) {
//                R.id.rb_user -> {
//
//                }
//                R.id.rb_vendor -> {
//
//                }
//            }

            ivProfileImage.setOnClickListener {
                showDialog()
            }

            tvUploadImage.setOnClickListener {
                showDialog()
            }

            ivEye.setOnClickListener {
                etPassword.togglePasswordVisibility()
            }

            etPinLocation.setOnClickListener {
                navigateToMap()
            }

            btnSignup.setOnClickListener {
                signup()
            }
        }
    }

    private fun showDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_image_options, null)
        val openCameraButton = dialogView.findViewById<Button>(R.id.btn_open_camera)
        val chooseGalleryButton = dialogView.findViewById<Button>(R.id.btn_choose_gallery)

        val dialogBuilder = AlertDialog.Builder(this, R.style.CustomDialog)
            .setView(dialogView)

        val dialog = dialogBuilder.create()


        openCameraButton.setOnClickListener {
            lauchCamera()
            dialog.dismiss()
        }

        chooseGalleryButton.setOnClickListener {
            pickImageFromGallery()
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun navigateToMap() {
        val intent = Intent(this, PinLocationMapActivity::class.java)
        resultLauncher.launch(intent)
    }

    private fun signup() {
        if (checkFormValidation()) {
            with(signupViewModel) {
                if (type == VENDOR_TYPE) {

                    if (checkVendorValidation()) {
                        val storeDetails = Store(email, storeName, storeAddress, storePinLocation)
                        signUp(listener =  signupListener, storeDetails =  storeDetails)
                    }

                }
                else {
                    signUp(listener = signupListener)
                }
            }
        }

    }

    private fun checkVendorValidation(): Boolean {
        with(binding) {
            with(signupViewModel) {
                storeName = etStore.text.toString().trim()
                storeAddress = etAddress.text.toString().trim()
                storePinLocation = etPinLocation.text.toString().trim()
                when {
                    storeName.isBlank() -> {
                        etStore.showError(getString(R.string.field_required_error))
                    }
                    storeAddress.isBlank() -> {
                        etAddress.showError(getString(R.string.field_required_error))
                    }
                    storePinLocation.isBlank() -> {
                        etPinLocation.isFocusableInTouchMode = true
                        etPinLocation.showError(getString(R.string.address_required_error))
                        etPinLocation.isFocusableInTouchMode = false
                    }
                    else -> {
                        return true
                    }
                }
            }

        }
        return false
    }

    private fun checkFormValidation(): Boolean {
        with(binding) {
            with(signupViewModel) {
                email = etEmail.text.toString().trim()
                password = etPassword.text.toString().trim()
                name = etName.text.toString().trim()
                mobileNumber = etMobileNum.text.toString().trim()

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
        }
        return false
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                data?.apply {
                    if (hasExtra(LATITUDE) && hasExtra(LONGITUDE)) {

                        setAddressFromLocation(
                            getDoubleExtra(LATITUDE, 0.0),
                            getDoubleExtra(LONGITUDE, 0.0)
                        )
                    }
                }
            }
        }


    private fun setAddressFromLocation(latitude: Double, longitude: Double) {
        if (latitude != 0.0 && longitude != 0.0) {
            val geocoder = Geocoder(this, Locale.getDefault())
            binding.etPinLocation.setText(getAddressFromLocation(geocoder, latitude, longitude))
        }
    }

    private fun initializeSignupListener() {
        signupListener = object : CustomSuccessFailureListener {
            override fun onSuccess() {
                showAlert(
                    title = getString(R.string.information),
                    message = getString(R.string.account_created)
                )
            }

            override fun onFailure(errorMessage: String?) {
                binding.apply {
                    svSignup.alpha = 1f
                    btnSignup.isEnabled = true
                    progressBarSignup.visibility = View.GONE
                }
                showAlert(
                    title = getString(R.string.error),
                    message = errorMessage.toString()
                )


            }

        }
    }

    private fun lauchCamera() {
        takePhotoLauncher.launch(null)
    }

    private fun pickImageFromGallery() {
        pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


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
                signupViewModel.imageUri = bitmap.toUri(applicationContext)
            }
        }


}