package com.ws.smarthouse.common

import android.view.View

interface RecyclerViewClickListener {
    fun recyclerViewListClicked(v: View?, position: Int)
}