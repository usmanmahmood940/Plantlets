package com.example.plantlets.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnDismissListener
import android.content.DialogInterface.OnShowListener
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.example.plantlets.Response.ItemSortOptions
import com.example.plantlets.Response.SortDirection
import com.example.plantlets.models.ItemFillter
import com.example.plantlets.models.SellerItem
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ui.IconGenerator
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*


object Helper {



    fun generateRandomStringWithTime(): String {
        val timestamp = System.currentTimeMillis()
        val randomString = generateRandomString(6)
        return "$timestamp-$randomString"
    }

    fun generateRandomString(length: Int): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val random = Random()
        return (1..length)
            .map { charset[random.nextInt(charset.length)] }
            .joinToString("")
    }


    fun getAddressFromLocation(geocoder: Geocoder, latitude: Double, longitude: Double): String {
        var addressText = ""
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address: Address = addresses[Constants.ZERO]
                val sb = StringBuilder()

                for (i in 0 until address.maxAddressLineIndex + 1) {
                    sb.append(address.getAddressLine(i))
                    if (i < address.maxAddressLineIndex) {
                        sb.append(", ")
                    }
                }

                addressText = sb.toString()
            }
        } catch (e: IOException) {
        }

        return addressText
    }

    fun getCustomMapIcon(context: Context, icon: Int): BitmapDescriptor {
        val iconGenerator = IconGenerator(context)
        iconGenerator.setBackground(
            ContextCompat.getDrawable(
                context,
                icon
            )
        )
        return BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())
    }

    fun googleMapUri(destination: LatLng, source: LatLng): Uri? {
        val destinationLatLng =
            "${destination.latitude},${destination.longitude}"
        val startingLatLng = "${source.latitude},${source.longitude}"
        val uri =
            "https://www.google.com/maps/dir/?api=1&destination=$destinationLatLng&origin=$startingLatLng"

        return Uri.parse(uri)
    }


    fun capitalizeFirstLetterOfEachWord(input: String): String {
        val words = input.split(" ") // Split the input string by spaces
        val capitalizedWords = words.map { it.capitalizeFirstLetter() } // Capitalize the first letter of each word
        return capitalizedWords.joinToString(" ") // Join the words back together
    }

    fun String.capitalizeFirstLetter(): String {
        if (isEmpty()) return this
        return this.substring(0, 1).toUpperCase() + this.substring(1).toLowerCase()
    }

    fun sortBy(items: List<SellerItem>, sortOption: String, sortDirection: String): List<SellerItem> {
        // Create a comparator based on the chosen sort option
        val sortedList = when (sortOption) {
            ItemSortOptions.Name.toString() -> items.sortedBy { it.name }
            ItemSortOptions.Price.toString() -> items.sortedBy  { it.price }
            ItemSortOptions.Popularity.toString() ->items.sortedBy  { it.soldCount }
            ItemSortOptions.Quantity.toString() ->items.sortedBy  { it.stockQuantity }
            else -> {items}
        }

        // Sort the items using the comparator


        // Reverse the list if the sort direction is backward
        return if (sortDirection == SortDirection.Descending.toString()) {
            sortedList.reversed()
        } else {
            sortedList
        }

    }


}