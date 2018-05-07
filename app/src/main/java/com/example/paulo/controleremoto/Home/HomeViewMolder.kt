package com.example.paulo.controleremoto.Home

import android.app.Activity
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.example.paulo.controleremotoarduino.AdapterBluetooth

class HomeViewHolder(val mActivity: Activity, val mContext: Context) {

    fun setRecyclerView(recyclerView: RecyclerView, adapter: AdapterBluetooth, viewManager: LinearLayoutManager){

        recyclerView.apply {
            this.setHasFixedSize(true)
            this.layoutManager = viewManager
            this.adapter = adapter
        }

    }
}