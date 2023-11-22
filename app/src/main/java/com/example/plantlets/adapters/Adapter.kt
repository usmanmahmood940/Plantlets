package com.example.plantlets.adapters

import android.opengl.Visibility
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.plantlets.R


@BindingAdapter("imageFromUrl")
fun ImageView.imageFromUrl(url: String){
    Glide.with(this.context).load(url).into(this)
}

@BindingAdapter("imageByQuantity")
fun ImageView.imageByQuantity(quantity: Int){
    if(quantity > 1) {
        this.setImageResource(R.drawable.ic_minus_image)
    }
    else{
        this.setImageResource(R.drawable.ic_minus_disabled_image)
    }

}

@BindingAdapter("imageByVisibility")
fun ImageView.imageByVisibility(visibility: Boolean){
    if(visibility) {
        this.setImageResource(R.drawable.ic_arrow_up)
    }
    else{
        this.setImageResource(R.drawable.ic_arrow_down)
    }

}



