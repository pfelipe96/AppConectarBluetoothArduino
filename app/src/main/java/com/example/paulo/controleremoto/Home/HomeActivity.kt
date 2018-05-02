package com.example.paulo.controleremoto.Home

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
import android.util.Log
import android.widget.Toast
import com.example.paulo.controleremoto.BR
import com.example.paulo.controleremoto.R
import com.example.paulo.controleremotoarduino.AdapterBluetooth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private var mBluetooth : BluetoothAdapter? = null
    private val REQUEST_ENABLE_BLUETOOTH = 110
    private lateinit var mReceiver: BroadcastReceiver
    private lateinit var mAdapterBluetooth: AdapterBluetooth
    private lateinit var mHomeViewHolder: HomeViewHolder
    private var mArrayAdapter: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHomeViewHolder = HomeViewHolder(this@HomeActivity, this)

        val binding = DataBindingUtil.setContentView<ViewDataBinding>(this, R.layout.activity_home)
        binding.setVariable(BR.viewModel, mHomeViewHolder)

        mAdapterBluetooth = AdapterBluetooth(this, this, mArrayAdapter)
        mHomeViewHolder.setRecyclerView(recycler_view_home, mAdapterBluetooth)


        mBluetooth = BluetoothAdapter.getDefaultAdapter()

        if(mBluetooth != null){
            if(!mBluetooth!!.isEnabled){
                var enableBluetoothAdapter = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBluetoothAdapter, REQUEST_ENABLE_BLUETOOTH)
            }else{
                findDevices()
            }
        }else{
            Toast.makeText(this, "Seu dispostivo não suporta bluetooth", Toast.LENGTH_LONG).show()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
                Log.v("Result", "OK")
                findDevices()
            }else{
                Log.v("Result", "BAD")
                Toast.makeText(applicationContext, "Ocorreu um erro inesperado, por gentileza tente novamente", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Ocorreu um erro inesperado, por gentileza tente novamente", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun findDevices() {
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (BluetoothDevice.ACTION_FOUND == action) {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    mArrayAdapter.add(device.name + "\n" + device.address)
                    mAdapterBluetooth.notifyDataSetChanged()
                }
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter)
    }
}

