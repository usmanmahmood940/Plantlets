package com.example.plantlets.adapters

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
import androidx.recyclerview.widget.RecyclerView
import com.example.plantlets.R
import com.example.plantlets.interfaces.ItemClickListener
import com.example.plantlets.models.Category


class CategoryAdapter(
    private var categoryItemList: List<Category> = emptyList(),
    private var listener: ItemClickListener
) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>(){

    fun setList(list: List<Category>) {
        categoryItemList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val category = categoryItemList[position]
        holder.apply {
            tvCategoryName.text = category.categoryName
            ivEdit.setOnClickListener{
                listener.onEdit(category)
            }
            ivDelete.setOnClickListener {
                listener.onDelete(category)
            }
        }


    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return categoryItemList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(CategoryItemView: View) : RecyclerView.ViewHolder(CategoryItemView) {
        val tvCategoryName: TextView = CategoryItemView.findViewById(R.id.tvItemName)
        val ivEdit:ImageView = CategoryItemView.findViewById(R.id.ivEdit)
        val ivDelete:ImageView = CategoryItemView.findViewById(R.id.ivDelete)
    }


}