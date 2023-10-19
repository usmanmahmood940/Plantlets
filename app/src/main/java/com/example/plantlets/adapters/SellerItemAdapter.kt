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
import com.example.plantlets.databinding.ItemSellerItemLayoutBinding
import com.example.plantlets.interfaces.CategoryClickListener
import com.example.plantlets.interfaces.ItemClickListener
import com.example.plantlets.models.Category
import com.example.plantlets.models.SellerItem


class SellerItemAdapter(
     var listener: ItemClickListener
) :
    ListAdapter<SellerItem, SellerItemAdapter.ViewHolder>(SellerItemDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSellerItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val sellerItem = getItem(position)

        holder.apply {
            bind(sellerItem,listener)

        }
    }







    // Holds the views for adding it to image and text
    class ViewHolder(private val binding: ItemSellerItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: SellerItem, listener: ItemClickListener){
            with(binding) {
                setActionLayoutHeight(cvMain, cvActions)
                tvItemName.text = item.name
                tvItemPrice.text = item.price.toString()
                tvStockQuantity.text = "Quantity : " + item.stockQuantity.toString()
                tvSalesCount.text="Sales Count : "+(item.soldCount?:"1").toString()
                Glide.with(ivItemImage.context).load(item.image).into(ivItemImage)
                cvMain.setOnClickListener {

                    val slideInAnimation =
                        AnimationUtils.loadAnimation(cvActions.context, R.anim.top_bottom_anim)

                    cvActions.visibility = View.VISIBLE
                    cvActions.startAnimation(slideInAnimation)

                    slideInAnimation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {
                            // Animation start callback
                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            // Make the layout invisible after the animation ends
                            cvMain.visibility = View.GONE
                        }

                        override fun onAnimationRepeat(animation: Animation?) {
                            // Animation repeat callback
                        }
                    })


                }
                cvActions.setOnClickListener {
                    hideActionLayout(cvActions, cvMain)

                }

                tvView.setOnClickListener {
                    showToast(tvView.context)

                }
                ivView.setOnClickListener {
                }

                tvEdit.setOnClickListener {
                    listener.onEdit(item)
                }
                ivEdit.setOnClickListener {
                    listener.onEdit(item)
                }

                ivDelete.setOnClickListener {
                    listener.onDelete(item)
                }
                tvDelete.setOnClickListener {
                    listener.onDelete(item)
                }
            }
        }

        private fun setActionLayoutHeight(clMain: ConstraintLayout, clActions: ConstraintLayout) {
            clMain.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // Get the height of the first layout after it's laid out
                    val heightOfFirstLayout = clMain.height

                    // Apply this height to the second layout
                    val layoutParams = clActions.layoutParams
                    layoutParams.height = heightOfFirstLayout
                    clActions.layoutParams = layoutParams

                    // Remove the global layout listener to avoid multiple calls
                    clMain.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
        private fun hideActionLayout(clActions: ConstraintLayout, clMain: ConstraintLayout) {
            val slideOutAnimation = AnimationUtils.loadAnimation(clActions.context, R.anim.bottom_top_anim)

            clActions.startAnimation(slideOutAnimation)

            clMain.visibility = View.VISIBLE
            clActions.visibility = View.GONE
        }

        private fun showToast(context: Context) {
            Toast.makeText(context,"View clicked",Toast.LENGTH_SHORT).show()
        }

    }

    class SellerItemDiffCallback : DiffUtil.ItemCallback<SellerItem>() {
        override fun areItemsTheSame(oldItem: SellerItem, newItem: SellerItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SellerItem, newItem: SellerItem): Boolean {
            return oldItem == newItem
        }
    }



}