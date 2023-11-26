package com.example.plantlets.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.plantlets.R
import com.example.plantlets.databinding.ItemSellerItemLayoutBinding
import com.example.plantlets.databinding.ItemStoreLayoutBinding
import com.example.plantlets.interfaces.CategoryClickListener
import com.example.plantlets.interfaces.StoreClickListener
import com.example.plantlets.models.Category
import com.example.plantlets.models.Store


class StoreItemAdapter(
    private val listener: StoreClickListener
) :
    ListAdapter<Store, StoreItemAdapter.ViewHolder>(StoreDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoreLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = getItem(position)
        holder.bind(store, listener)
    }

    class ViewHolder(private val binding: ItemStoreLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(store: Store, listener: StoreClickListener) {
            with(binding){
               tvName.text = store.storeName
               tvLocation.text = store.storeAddress

                storeLayout.setOnClickListener {
                    listener.onClick(store)
                }

            }

//            ivEdit.setOnClickListener { listener.onEdit(store) }
//            ivDelete.setOnClickListener { listener.onDelete(store) }
        }
    }

    class StoreDiffCallback : DiffUtil.ItemCallback<Store>() {
        override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean {
            return oldItem.email == newItem.email
        }

        override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean {
            return oldItem == newItem
        }
    }
}
