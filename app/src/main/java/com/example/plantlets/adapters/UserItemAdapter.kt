package com.example.plantlets.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plantlets.R
import com.example.plantlets.databinding.ItemPlantUserBinding
import com.example.plantlets.databinding.ItemSellerItemLayoutBinding
import com.example.plantlets.interfaces.CategoryClickListener
import com.example.plantlets.interfaces.ItemClickListener
import com.example.plantlets.interfaces.UserItemClickListener
import com.example.plantlets.models.Category
import com.example.plantlets.models.SellerItem


class UserItemAdapter(
     var listener: UserItemClickListener?=null,
    var fromSearch:Boolean = false
) :
    ListAdapter<SellerItem, UserItemAdapter.ViewHolder>(UserItemDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlantUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        if(!fromSearch) {
            val recyclerViewWidth = parent.measuredWidth
            val desiredItemWidth = (0.6 * recyclerViewWidth).toInt()

            // Set the layout parameters for the CardView
            val layoutParams =
                ViewGroup.MarginLayoutParams(desiredItemWidth, ViewGroup.LayoutParams.MATCH_PARENT)
            layoutParams.setMargins(0, 10, 0, 10)
            binding.root.layoutParams = layoutParams
        }
        else{
            val recyclerViewWidth = parent.measuredWidth
            val desiredItemWidth = (0.5 * recyclerViewWidth).toInt()

            // Set the layout parameters for the CardView
            val layoutParams =
                ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(20, 40, 20, 40)
            binding.root.layoutParams = layoutParams
        }
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val sellerItem = getItem(position)

        holder.apply {
            bind(sellerItem,listener)

        }
    }







    // Holds the views for adding it to image and text
    class ViewHolder(private val binding: ItemPlantUserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SellerItem, listener: UserItemClickListener?){
            with(binding) {

                tvItemName.text = item.name
                tvItemPrice.text = item.price?.toInt().toString()
                Glide.with(ivItemImage.context).load(item.image).into(ivItemImage)
                cvMain.setOnClickListener {
                    listener?.onClick(item)
                }


            }
        }





    }

    class UserItemDiffCallback : DiffUtil.ItemCallback<SellerItem>() {
        override fun areItemsTheSame(oldItem: SellerItem, newItem: SellerItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SellerItem, newItem: SellerItem): Boolean {
            return oldItem == newItem
        }
    }



}