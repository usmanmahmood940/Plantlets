package com.example.plantlets.models

import com.example.plantlets.Response.ItemSortOptions
import com.example.plantlets.Response.SortDirection
import com.example.plantlets.utils.Constants.ALL

data class SellerItemFillter (
    var category:String=ALL,
    var sortOption:String =ItemSortOptions.Date.toString(),
    var sortDirection:String = SortDirection.Forward.toString()
)