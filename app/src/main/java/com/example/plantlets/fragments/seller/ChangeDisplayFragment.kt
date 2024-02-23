package com.example.plantlets.fragments.seller

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.plantlets.R
import com.example.plantlets.activities.BaseActivity
import com.example.plantlets.databinding.FragmentChangeDisplayBinding
import com.example.plantlets.repositories.LocalRepository
import com.example.plantlets.repositories.StoreRepository
import com.example.plantlets.utils.Extensions.getImprovedBitmap
import com.example.plantlets.utils.Extensions.toUri
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ChangeDisplayFragment : Fragment() {

    lateinit var binding:FragmentChangeDisplayBinding
    var imageUri: Uri? = null

    @Inject
    lateinit var storeRepository: StoreRepository
    @Inject
    lateinit var localRepository: LocalRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChangeDisplayBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        binding.apply {
            localRepository.getStoreFromPref()?.image?.let {
                Glide.with(ivDisplayImage.context).load(it).into(ivDisplayImage)
            }
            btnChangeImage.setOnClickListener {
                showDialog()
            }
            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
            btnAction.setOnClickListener {
                imageUri?.let {
                    btnAction.isEnabled= false
                    (requireActivity() as BaseActivity).showProgressBar()
                    CoroutineScope(Dispatchers.IO).launch {
                        storeRepository.changeDisplay(it)
                        withContext(Dispatchers.Main) {
                            (requireActivity() as BaseActivity).hideProgressBar()
                            findNavController().navigateUp()
                        }
                    }

                }?:kotlin.run {
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun showDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_image_options, null)
        val openCameraButton = dialogView.findViewById<Button>(R.id.btn_open_camera)
        val chooseGalleryButton = dialogView.findViewById<Button>(R.id.btn_choose_gallery)

        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
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

    private fun lauchCamera() {
        takePhotoLauncher.launch(null)
    }

    private fun pickImageFromGallery() {
        pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


    private val pickImage: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.ivDisplayImage.setImageURI(uri)
                imageUri = uri

            }
        }

    private val takePhotoLauncher: ActivityResultLauncher<Void?> =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                binding.ivDisplayImage.setImageBitmap(bitmap.getImprovedBitmap())
                imageUri = bitmap.toUri(requireActivity().applicationContext)
            }
        }


}