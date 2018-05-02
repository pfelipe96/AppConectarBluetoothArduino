package com.example.paulo.controleremotoarduino

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.paulo.controleremoto.R
import kotlinx.android.synthetic.main.card_view_device_bluetooth.view.*

class CardViewInformationDevice(itemView: View) : RecyclerView.ViewHolder(itemView){
    val mNameDevice = itemView.name_device
}

class AdapterBluetooth(val mActivity: Activity, val mContext : Context, val mDataSet : ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.card_view_device_bluetooth, parent, false)
        return CardViewInformationDevice(view)
    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mHolder = holder as CardViewInformationDevice

        mHolder.mNameDevice.text = mDataSet[position]

    }

}