package com.example.plantlets.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.plantlets.databinding.ItemCartBinding
import com.example.plantlets.databinding.ItemOrderItemBinding
import com.example.plantlets.models.CartItem
import com.example.plantlets.models.SellerItem


class OrderItemAdapter(
    private var cartItemList: List<CartItem> = mutableListOf(),
) : RecyclerView.Adapter<OrderItemAdapter.ViewHolder>() {

    fun setList(list: List<CartItem>) {
        cartItemList = list
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemOrderItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cartItemList[position])
    }

    override fun getItemCount(): Int {
        return cartItemList.size
    }

    inner class ViewHolder(private val binding: ItemOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(orderItem: CartItem) {
            binding.orderItem = orderItem


            binding.executePendingBindings()
        }

    }
}
