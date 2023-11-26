package com.example.plantlets.fragments.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.plantlets.databinding.FragmentOrderRatingBinding
import com.example.plantlets.utils.Constants
import com.example.plantlets.utils.Constants.RATING_DATA


class OrderRatingFragment : Fragment() {


    lateinit var binding:FragmentOrderRatingBinding
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private var rating:Int?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentOrderRatingBinding.inflate(inflater, container, false)
        init()

        return binding.root
    }

    private fun init() {
        binding.btnSubmit.setOnClickListener {
            submitRating()
        }
        binding.rlBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun submitRating() {
        val previousFragment = findNavController().previousBackStackEntry
        if (previousFragment != null) {
            rating?.let {
                val data = Bundle().apply {
                    putInt(Constants.RATING, it)
                }
                previousFragment.savedStateHandle.set(RATING_DATA, data)
                findNavController().popBackStack()
            }?: kotlin.run {
                findNavController().navigateUp()
            }

        }
    }

    private fun setupBackButton() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val previousFragment = findNavController().previousBackStackEntry
                if (previousFragment != null) {
                    rating?.let {
                        val data = Bundle().apply {
                            putInt(Constants.RATING, it)
                        }
                        previousFragment.savedStateHandle.set(RATING_DATA, data)
                        findNavController().popBackStack()
                    }?: kotlin.run {
                        findNavController().navigateUp()
                    }

                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

}