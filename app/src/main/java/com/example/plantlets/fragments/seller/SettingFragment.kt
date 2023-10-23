package com.example.plantlets.fragments.seller

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.plantlets.R
import com.example.plantlets.activities.LoginActivity
import com.example.plantlets.activities.UserHomeActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SettingFragment : Fragment() {

    @Inject
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
//        auth.signOut()
        requireActivity().startActivity(Intent(requireContext(),UserHomeActivity::class.java))
        requireActivity().finish()

        return inflater.inflate(R.layout.fragment_setting, container, false)
    }


}