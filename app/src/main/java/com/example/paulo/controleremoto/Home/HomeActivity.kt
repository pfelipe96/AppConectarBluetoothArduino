package com.example.paulo.controleremoto.Home

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.example.paulo.controleremoto.BR
import com.example.paulo.controleremoto.R
import com.example.paulo.controleremotoarduino.AdapterBluetooth
import kotlinx.android.synthetic.main.activity_home.*
import android.Manifest.permission
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED
import android.support.v4.app.FragmentActivity


class HomeActivity : AppCompatActivity() {

    private lateinit var mBluetooth : BluetoothAdapter
    private val REQUEST_ENABLE_BLUETOOTH = 110
    private lateinit var mBluetoothDevicePaired: Set<BluetoothDevice>

    private lateinit var mHomeViewHolder: HomeViewHolder

    private lateinit var mAdapterBluetooth: AdapterBluetooth
    private lateinit var mViewManager: LinearLayoutManager
    private var mArrayAdapter: ArrayList<String> = ArrayList()

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == BluetoothDevice.ACTION_FOUND) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                mArrayAdapter.add(device.name + "\n" + device.address)
                mArrayAdapter.add("Teste")
                mAdapterBluetooth.notifyDataSetChanged()
            }
        }
    }

    private val mBroadcastReceiver1 = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            // When discovery finds a device
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

                when (state) {
                    BluetoothAdapter.STATE_OFF -> Log.d("TAG", "onReceive: STATE OFF")
                    BluetoothAdapter.STATE_TURNING_OFF -> Log.d("TAG", "mBroadcastReceiver1: STATE TURNING OFF")
                    BluetoothAdapter.STATE_ON -> Log.d("TAG", "mBroadcastReceiver1: STATE ON")
                    BluetoothAdapter.STATE_TURNING_ON -> Log.d("TAG", "mBroadcastReceiver1: STATE TURNING ON")
                }
            }
        }
    }

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
                var enableBluetoothAdapter = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBluetoothAdapter, REQUEST_ENABLE_BLUETOOTH)

            }

            val BTIntent = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
            registerReceiver(mBroadcastReceiver1, BTIntent)
            findDevices()

        }else{
            Toast.makeText(this, "Seu dispostivo não suporta bluetooth", Toast.LENGTH_LONG).show()
        }
    }


    private fun findDevices() {
        if(mBluetooth.isDiscovering) {
            mBluetooth.cancelDiscovery()

            if(Build.VERSION.SDK_INT > 22) checkBluetoothPermission()

            mBluetooth.startDiscovery()
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            registerReceiver(mBroadcastReceiver, filter)
        }

        if(!mBluetooth.isDiscovering) {
            if(Build.VERSION.SDK_INT > 22) checkBluetoothPermission()

            mBluetooth.startDiscovery()
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            registerReceiver(mBroadcastReceiver, filter)
        }
    }

    private fun deviceAlreadyPaired(){
        mBluetoothDevicePaired = mBluetooth!!.bondedDevices

        if(mBluetoothDevicePaired.isNotEmpty()){
            mBluetoothDevicePaired.forEach{
                mArrayAdapter.add(it.name+"\n"+it.address)
            }
        }
    }

    private fun setDiscoverable(){
        val intentDiscoverable = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkBluetoothPermission(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            var permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION")
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION")
            if (permissionCheck != 0) {
                this.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1001) //Any number
            }
        }
    }

}


