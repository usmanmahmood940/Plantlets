package com.example.plantlets.fragments.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.plantlets.R
import com.example.plantlets.databinding.FragmentCheckoutBinding
import com.example.plantlets.models.Amounts
import com.example.plantlets.models.SellerItem

class
CheckoutFragment : Fragment() {

    private lateinit var binding:FragmentCheckoutBinding
    private var amount: Amounts? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCheckoutBinding.inflate(inflater,container,false)
        return binding.root
    }

}