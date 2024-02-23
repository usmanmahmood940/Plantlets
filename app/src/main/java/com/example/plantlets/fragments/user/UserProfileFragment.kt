package com.example.plantlets.fragments.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.plantlets.R
import com.example.plantlets.activities.UserHomeActivity
import com.example.plantlets.databinding.FragmentUserProfileBinding
import com.example.plantlets.models.User
import com.example.plantlets.repositories.LocalRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.lang.RuntimeException
import javax.inject.Inject


@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private lateinit var binding : FragmentUserProfileBinding
    @Inject
    lateinit var localRepository: LocalRepository
    @Inject
    lateinit var auth:FirebaseAuth
    private  var user : User? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentUserProfileBinding.inflate(layoutInflater)
        (requireActivity() as UserHomeActivity).changeBottomNavColor()
        setUI()

        return binding.root
    }

    private fun setUI() {

        user = localRepository.getCurrentUserData()
        binding.apply {
            etName.text = auth.currentUser?.displayName
            etEmail.text = user?.email
            etMobileNum.text = user?.mobileNumber
            Glide.with(ivProfileImage).load(user?.image).into(ivProfileImage)
        }
    }


}