package com.example.paulo.controleremoto.Home

import android.Manifest
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
import android.os.CountDownTimer
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.widget.Toast
import com.example.paulo.controleremoto.BR
import com.example.paulo.controleremoto.R
import com.example.paulo.controleremotoarduino.AdapterBluetooth
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.toolber_activity.*


class HomeActivity : AppCompatActivity() {

    private lateinit var mBluetooth : BluetoothAdapter
    private val REQUEST_ENABLE_BLUETOOTH = 110
    private lateinit var mBluetoothDevicePaired: Set<BluetoothDevice>

    private lateinit var mHomeViewHolder: HomeViewHolder

    private lateinit var mAdapterBluetooth: AdapterBluetooth
    private lateinit var mViewManager: LinearLayoutManager
    private var mArrayAdapter: ArrayList<ConstructorAdapter> = ArrayList()

    private lateinit var mBroadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHomeViewHolder = HomeViewHolder(this@HomeActivity, this)

        val binding = DataBindingUtil.setContentView<ViewDataBinding>(this, R.layout.activity_home)
        binding.setVariable(BR.viewModel, mHomeViewHolder)

        mAdapterBluetooth = AdapterBluetooth(this, this, mArrayAdapter)
        mViewManager = LinearLayoutManager(this)
        mHomeViewHolder.setRecyclerView(recycler_view_home, mAdapterBluetooth, mViewManager)
        mHomeViewHolder.setToolbar(this@HomeActivity, toolbarID)

        mBluetooth = BluetoothAdapter.getDefaultAdapter()

        if(mBluetooth != null){
            if(!mBluetooth!!.isEnabled){
                var enableBluetoothAdapter = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBluetoothAdapter, REQUEST_ENABLE_BLUETOOTH)
            }
            findDevices()
        }else{
            Toast.makeText(this, "Seu dispostivo não suporta bluetooth", Toast.LENGTH_LONG).show()
        }
    }


    private fun findDevices() {
        mHomeViewHolder.setProgressBar(true)

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
        getReceiveDevices()
    }

    private fun getReceiveDevices(){
        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (action == BluetoothDevice.ACTION_FOUND) {
                    var device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    runOnUiThread({
                        object : CountDownTimer(12000, 1000){
                            override fun onTick(millisUntilFinished: Long) {
                                mArrayAdapter.add(ConstructorAdapter(if(device.name.isNullOrEmpty()) "Sem nome" else device.name, if(device.address.isNullOrEmpty()) "Sem mac" else device.address))
                            }

                            override fun onFinish() {
                                if(mArrayAdapter.isNotEmpty())
                                    mArrayAdapter.add(ConstructorAdapter(if(device.name.isNullOrEmpty()) "Sem nome" else device.name, if(device.address.isNullOrEmpty()) "Sem mac" else device.address))
                                else
                                    dialogMessageArrayDevicesIsEmpty()

                                mHomeViewHolder.setProgressBar(false)
                            }
                        }
                    })
                }
            }
        }
    }

    private fun dialogMessageArrayDevicesIsEmpty(){

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Nenhum dispositivo encontrado")
        builder.setMessage("Deseja reinicializar à procura por novos dispositivos")
        builder.setPositiveButton("Sim", {dialog, which ->
            findDevices()
        })
        builder.setNegativeButton("Não", {dialog, which ->

        })

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun deviceAlreadyPaired(){
        mBluetoothDevicePaired = mBluetooth!!.bondedDevices

        if(mBluetoothDevicePaired.isNotEmpty()){
            mBluetoothDevicePaired.forEach{
                mArrayAdapter.add(ConstructorAdapter(it.name,it.address))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkBluetoothPermission(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            var permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION")
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION")
            if (permissionCheck != 0) {
                this.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1001)
            }
        }
    }
//
//    interface OnResponseFindDevices{
//        fun
//    }

}


