package com.example.paulo.controleremoto.Home

import android.app.Activity
import android.content.Context
import android.databinding.ObservableField
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ProgressBar
import com.example.paulo.controleremotoarduino.AdapterBluetooth

class HomeViewHolder(val mActivity: Activity, val mContext: Context) {

    var setProgressBar = ObservableField<Boolean>(false)


    fun setRecyclerView(recyclerView: RecyclerView, adapter: AdapterBluetooth, viewManager: LinearLayoutManager){
        recyclerView.apply {
            this.setHasFixedSize(true)
            this.layoutManager = viewManager
            this.adapter = adapter
        }
    }

    fun setToolbar(appCompatActivity: AppCompatActivity, toolBar: Toolbar){
        appCompatActivity.setSupportActionBar(toolBar)
        appCompatActivity.title = "Encontrar dispositivo"
    }

    fun setProgressBar(valueBoolean: Boolean){
        setProgressBar.set(valueBoolean)
    }
}