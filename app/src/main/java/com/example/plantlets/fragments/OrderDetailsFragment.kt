package com.example.plantlets.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.plantlets.databinding.FragmentOrderDetailsBinding


class OrderDetailsFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        binding.orderDetail = true

        binding.arrowOrderDetails.setOnClickListener {
            hideShowOrderDetails()
        }

        return binding.root
    }

    fun hideShowOrderDetails() {
        binding.orderDetail = !(binding.orderDetail)
        binding.invalidateAll()
        if (binding.orderDetail)
            binding.layoutOrderDetails.visibility = View.VISIBLE
        else
            binding.layoutOrderDetails.visibility = View.GONE

    }

}