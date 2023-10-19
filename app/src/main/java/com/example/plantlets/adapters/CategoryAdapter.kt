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
import com.example.plantlets.interfaces.CategoryClickListener
import com.example.plantlets.models.Category


class CategoryAdapter(
    private val listener: CategoryClickListener
) :
    ListAdapter<Category, CategoryAdapter.ViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category, listener)
    }

    class ViewHolder(CategoryItemView: View) : RecyclerView.ViewHolder(CategoryItemView) {
        val tvCategoryName: TextView = CategoryItemView.findViewById(R.id.tvCategoryName)
        val ivEdit: ImageView = CategoryItemView.findViewById(R.id.ivEdit)
        val ivDelete: ImageView = CategoryItemView.findViewById(R.id.ivDelete)

        fun bind(category: Category, listener: CategoryClickListener) {
            tvCategoryName.text = category.categoryName
            ivEdit.setOnClickListener { listener.onEdit(category) }
            ivDelete.setOnClickListener { listener.onDelete(category) }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.categoryId == newItem.categoryId
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
}
