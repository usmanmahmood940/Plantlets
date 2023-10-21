package com.example.plantlets.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CenterItemZoomScrollListener(private val recyclerView: RecyclerView) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        
        // Calculate the center of the RecyclerView
        val centerX = recyclerView.width / 2

        // Iterate through visible items to find the one closest to the center
        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            val childCenterX = (child.left + child.right) / 2

            // Calculate the distance from the child's center to the center of the RecyclerView
            val distance = Math.abs(centerX - childCenterX)

            // You can adjust the scaling factor as needed
            val scaleFactor = 1 - ((distance.toFloat()/1.5f) / recyclerView.width)

            // Apply the scale transformation to the item
            child.scaleX = scaleFactor
            child.scaleY = scaleFactor

        }
    }


}
