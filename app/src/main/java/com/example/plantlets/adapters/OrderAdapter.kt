package com.example.plantlets.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.plantlets.databinding.ItemOrderBinding
import com.example.plantlets.models.CartItem
import com.example.plantlets.models.Order
import com.example.plantlets.models.SellerItem


class OrderAdapter(
    private val listener: OrderClickListener
) : ListAdapter<Order, OrderAdapter.ViewHolder>(OrderDiffCallback()) {

    interface OrderClickListener {

        fun onClick(order: Order)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemOrderBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(getItem(position),listener)
    }



    inner class ViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order,listener: OrderClickListener) {
            binding.order = order
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                listener.onClick(order)
            }
        }

    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}
