package com.citsadigital.relojmultifuncionalcl.activity

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager

import com.citsadigital.relojmultifuncionalcl.R
import com.citsadigital.relojmultifuncionalcl.adapter.BluetoothDevicesAdapter
import kotlinx.android.synthetic.main.activity_bluetooth.*


class BluetoothActivity : AppCompatActivity(), BluetoothDevicesAdapter.OnDeviceClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)

        devicesList.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = BluetoothDevicesAdapter(
                    ArrayList(BluetoothAdapter.getDefaultAdapter().bondedDevices),
                    this@BluetoothActivity)
        }

    }

    override fun onDeviceClick(device: BluetoothDevice) {
        if (BluetoothAdapter.getDefaultAdapter().isEnabled) {
            setResult(
                    android.app.Activity.RESULT_OK,
                    Intent().apply { putExtra(getString(R.string.key_mainviewmodel_device), device) })
            finish()
        } else {
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 10)
        }
    }
}
