package com.citsadigital.relojmultifuncionalcl.adapter

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.citsadigital.relojmultifuncionalcl.R
import kotlinx.android.synthetic.main.item_bluetoothdevice.view.*


class BluetoothDevicesAdapter(private val values: List<BluetoothDevice>,
                              private val onDeviceClickListener: OnDeviceClickListener? = null) : RecyclerView.Adapter<BluetoothDevicesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_bluetoothdevice, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position], onDeviceClickListener)
    }

    override fun getItemCount(): Int {
        return values.size
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(device: BluetoothDevice, onDeviceClickListener: OnDeviceClickListener?) {
            view.deviceItemName.text = device.name
            view.deviceItemAddress.text = device.address
            view.deviceItem.setOnClickListener { onDeviceClickListener?.onDeviceClick(device) }
        }

    }

    interface OnDeviceClickListener {
        fun onDeviceClick(device: BluetoothDevice)
    }
}
