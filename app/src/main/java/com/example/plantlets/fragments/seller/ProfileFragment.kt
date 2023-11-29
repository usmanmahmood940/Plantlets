package com.example.plantlets.fragments.seller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.plantlets.R
import com.example.plantlets.activities.UserHomeActivity
import com.example.plantlets.databinding.FragmentProfileBinding
import com.example.plantlets.databinding.FragmentUserProfileBinding
import com.example.plantlets.models.User
import com.example.plantlets.repositories.LocalRepository
import javax.inject.Inject


class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding
    @Inject
    lateinit var localRepository: LocalRepository
    private  var user : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentProfileBinding.inflate(layoutInflater)
        (requireActivity() as UserHomeActivity).changeBottomNavColor()
        setUI()

        return binding.root

    }

    private fun setUI() {

        user = localRepository.getCurrentUserData()

        binding.apply {
            user?.apply {
                etName.text = name
                etEmail.text = email
                etMobileNum.text = mobileNumber
                Glide.with(ivProfileImage).load(image).into(ivProfileImage)
            }
        }
    }

}