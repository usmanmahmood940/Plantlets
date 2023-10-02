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
import com.example.plantlets.models.SellerItem


class SellerItemAdapter(private var sellerItemList: List<SellerItem>) :
    RecyclerView.Adapter<SellerItemAdapter.ViewHolder>(){

    fun setList(list: List<SellerItem>) {
        sellerItemList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_seller_item_layout, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val sellerItem = sellerItemList[position]

        holder.apply {
            setActionLayoutHeight(clMain, clActions)
            tvName.text = sellerItem.name
            tvPrice.text = sellerItem.price.toString()
            tvQuanttity.text = "Quantity : " +sellerItem.stockQuantity.toString()
            clMain.setOnClickListener {

                val slideInAnimation =
                    AnimationUtils.loadAnimation(clActions.context, R.anim.top_bottom_anim)

                clActions.visibility = View.VISIBLE
                clActions.startAnimation(slideInAnimation)

                slideInAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        // Animation start callback
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        // Make the layout invisible after the animation ends
                        clMain.visibility = View.GONE
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                        // Animation repeat callback
                    }
                })


            }
            clActions.setOnClickListener {
                hideActionLayout(clActions,clMain)

            }

            tvView.setOnClickListener {
                showToast(tvView.context)

            }
            ivView.setOnClickListener {
                showToast(ivView.context)
            }
        }
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

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return sellerItemList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(SellerItemView: View) : RecyclerView.ViewHolder(SellerItemView) {
        val tvName: TextView = SellerItemView.findViewById(R.id.tvItemName)
        val tvPrice: TextView = SellerItemView.findViewById(R.id.tvItemPrice)
        val tvQuanttity: TextView = SellerItemView.findViewById(R.id.tvStockQuantity)
        val clMain: ConstraintLayout = SellerItemView.findViewById(R.id.cvMain)
        val clActions: ConstraintLayout = SellerItemView.findViewById(R.id.cvActions)
        val tvView: TextView = SellerItemView.findViewById(R.id.tvEdit)
        val ivView:ImageView = SellerItemView.findViewById(R.id.ivView)


    }


}