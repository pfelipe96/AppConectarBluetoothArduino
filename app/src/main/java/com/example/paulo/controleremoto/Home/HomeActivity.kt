package com.example.paulo.controleremoto.Home

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.example.paulo.controleremoto.BR
import com.example.paulo.controleremoto.R
import com.example.paulo.controleremotoarduino.AdapterBluetooth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private lateinit var mBluetooth : BluetoothAdapter
    private val REQUEST_ENABLE_BLUETOOTH = 110
    private lateinit var mReceiver: BroadcastReceiver
    private lateinit var mBluetoothDevicePaired: Set<BluetoothDevice>

    private lateinit var mHomeViewHolder: HomeViewHolder

    private lateinit var mAdapterBluetooth: AdapterBluetooth
    private lateinit var mViewManager: LinearLayoutManager
    private var mArrayAdapter: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHomeViewHolder = HomeViewHolder(this@HomeActivity, this)


        val binding = DataBindingUtil.setContentView<ViewDataBinding>(this, R.layout.activity_home)
        binding.setVariable(BR.viewModel, mHomeViewHolder)

        mAdapterBluetooth = AdapterBluetooth(this, this, mArrayAdapter)
        mViewManager = LinearLayoutManager(this)
        mHomeViewHolder.setRecyclerView(recycler_view_home, mAdapterBluetooth, mViewManager)

        mBluetooth = BluetoothAdapter.getDefaultAdapter()

        if(mBluetooth != null){
            if(!mBluetooth!!.isEnabled){
                val enableBluetoothAdapter = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBluetoothAdapter, REQUEST_ENABLE_BLUETOOTH)
            }else{
                findDevices()
                deviceAlreadyPaired()
            }
        }else{
            Toast.makeText(this, "Seu dispostivo não suporta bluetooth", Toast.LENGTH_LONG).show()
        }
    }


    private fun findDevices() {
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (BluetoothDevice.ACTION_FOUND == action) {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    mArrayAdapter.add(device.name + "\n" + device.address)
                    mArrayAdapter.add("Teste")
                    mAdapterBluetooth.notifyDataSetChanged()
                }
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter)
    }

    private fun deviceAlreadyPaired(){
        mBluetoothDevicePaired = mBluetooth!!.bondedDevices

        if(mBluetoothDevicePaired.isNotEmpty()){
            mBluetoothDevicePaired.forEach{
                mArrayAdapter.add(it.toString())
            }
        }
    }
}


