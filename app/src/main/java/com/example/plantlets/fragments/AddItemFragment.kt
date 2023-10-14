package com.example.plantlets.fragments

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.plantlets.R
import com.example.plantlets.Response.CustomResponse
import com.example.plantlets.activities.BaseActivity
import com.example.plantlets.activities.SellerHomeActivity
import com.example.plantlets.databinding.FragmentAddItemBinding
import com.example.plantlets.models.Category
import com.example.plantlets.models.SellerItem
import com.example.plantlets.utils.Extensions.checkIsBlank
import com.example.plantlets.utils.Extensions.getImprovedBitmap
import com.example.plantlets.utils.Extensions.getValue
import com.example.plantlets.utils.Extensions.showRequiredError
import com.example.plantlets.utils.Extensions.toUri
import com.example.plantlets.viewmodels.SellerItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddItemFragment : Fragment() {

    private lateinit var binding: FragmentAddItemBinding
    private lateinit var itemViewModel: SellerItemViewModel
    var imageUri: Uri? = null
    private lateinit var categoriesList: List<Category>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentAddItemBinding.inflate(inflater, container, false)
        itemViewModel = ViewModelProvider(requireActivity()).get(SellerItemViewModel::class.java)

        getCategories()
        init()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        itemViewModel.startObservingCateories()
    }

    override fun onPause() {
        super.onPause()
        itemViewModel.stopObservingCategories()
    }

    private fun getCategories() {
        lifecycleScope.launch {
            itemViewModel.categoryList.collect { response ->
                when (response) {
                    is CustomResponse.Success -> {
                        (requireActivity() as SellerHomeActivity).hideProgressBar().also {
                            binding.svAddItem.alpha = 1f
                            binding.btnAction.isEnabled = true
                        }
                        response.data?.let { categoryList ->
                            setSpinner(categoryList)
                            categoriesList = categoryList
                        }
                    }

                    is CustomResponse.Loading -> {
                        (requireActivity() as SellerHomeActivity).showProgressBar().also {
                            binding.svAddItem.alpha = 0.5f
                            binding.btnAction.isEnabled = false
                        }
                    }

                    is CustomResponse.Error -> {
                        (requireActivity() as SellerHomeActivity).apply {
                            hideProgressBar()
                            showAlert(
                                title = getString(R.string.error),
                                message = response.errorMessage
                            )
                        }.also {
                            binding.svAddItem.alpha = 1f
                            binding.btnAction.isEnabled = false
                        }

                    }
                }
            }
        }
    }

    private fun init() {
        binding.apply {
            ivItemImage.setOnClickListener {
                showDialog()
            }
            tvUploadImage.setOnClickListener {
                showDialog()
            }

            btnAction.setOnClickListener {
                if (checkValidations()) {
                    val item = SellerItem(
                        name = etItemName.getValue(),
                        details = etItemDetail.getValue(),
                        categoryId = getCatId(spinnerCategory.selectedItem.toString()),
                        price = etItemPrice.getValue().toDouble(),
                        stockQuantity = etQuantity.getValue().toInt()
                    )
                    itemViewModel.upsertItem(item,imageUri)
                }
            }
        }

    }

    private fun getCatId(categoryName: String): String? {
        return categoriesList.firstOrNull { it.categoryName == categoryName }?.categoryId

    }

    private fun checkValidations(): Boolean {
        with(binding) {
            when {
                etItemName.checkIsBlank() -> {
                    etItemName.showRequiredError()
                }

                etItemDetail.checkIsBlank() -> {
                    etItemDetail.showRequiredError()
                }

                etItemPrice.checkIsBlank() -> {
                    etItemPrice.showRequiredError()
                }

                etQuantity.checkIsBlank() -> {
                    etQuantity.showRequiredError()
                }

                (spinnerCategory.selectedItem == getString(R.string.choose_category)) -> {
                    (requireActivity() as BaseActivity).showAlert(
                        getString(R.string.error),
                        getString(R.string.category_error)
                    )
                }

                (imageUri == null) -> {
                    (requireActivity() as BaseActivity).showAlert(
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

    private fun setSpinner(categories: List<Category>) {
        val spinnerCategories = mutableListOf<String>(getString(R.string.choose_category))
        spinnerCategories.addAll(categories.map {
            it.categoryName
        })


        val categoriesSpinnerAdapter = getSpinnerAdapter(spinnerCategories)
        categoriesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerCategory.adapter = categoriesSpinnerAdapter
        binding.spinnerCategory.onItemSelectedListener = getSpinnerListener(spinnerCategories)
        Toast.makeText(
            requireContext(),
            binding.spinnerCategory.selectedItem.toString(),
            Toast.LENGTH_SHORT
        ).show()

    }

    private fun getSpinnerAdapter(categories: List<String>): ArrayAdapter<String> {
        return object :
            ArrayAdapter<String>(requireContext(), R.layout.item_category_dropdown, categories) {

            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from Spinner
                // First item will be used for hint
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup,
            ): View {
                val view: TextView =
                    super.getDropDownView(position, convertView, parent) as TextView

                view.text = categories[position]
                //set the color of first item in the drop down list to gray
                if (position == 0) {
                    view.setTextColor(Color.GRAY)

                } else {
                    //here it is possible to define color for other items by
                    //view.setTextColor(Color.RED)
                }
                return view
            }
        }

    }

    private fun getSpinnerListener(categories: List<String>): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                val value = parent!!.getItemAtPosition(position).toString()
                if (value == categories[0]) {
                    view?.let {
                        (view as TextView).setTextColor(Color.GRAY)
                    }
                }
            }

        }

    }

    private fun showDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_image_options, null)
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
                binding.tvUploadImage.visibility = View.GONE
                binding.ivItemImage.setImageURI(uri)
                imageUri = uri

            }
        }

    private val takePhotoLauncher: ActivityResultLauncher<Void?> =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                binding.tvUploadImage.visibility = View.GONE
                binding.ivItemImage.setImageBitmap(bitmap.getImprovedBitmap())
                imageUri = bitmap.toUri(requireActivity().applicationContext)
            }
        }

}