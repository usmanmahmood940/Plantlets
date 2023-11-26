package com.example.plantlets.fragments.user

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.plantlets.R
import com.example.plantlets.activities.LoginActivity
import com.example.plantlets.databinding.FragmentUserSettingBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserSettingFragment : Fragment() {


    lateinit var binding:FragmentUserSettingBinding
    @Inject
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment

        binding =  FragmentUserSettingBinding.inflate(inflater, container, false)

        init()
        return binding.root

    }

    private fun init() {
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            requireActivity().startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }


}