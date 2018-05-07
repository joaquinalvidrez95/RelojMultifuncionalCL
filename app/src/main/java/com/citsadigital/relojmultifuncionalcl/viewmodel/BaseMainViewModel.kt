package com.citsadigital.relojmultifuncionalcl.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.support.v7.preference.PreferenceManager
import com.citsadigital.relojmultifuncionalcl.R
import com.citsadigital.relojmultifuncionalcl.util.BluetoothConstants
import com.citsadigital.relojmultifuncionalcl.util.BluetoothService

/**
 * Created by Joaquín Alan Alvidrez Soto on 05/04/2018.
 */
abstract class BaseMainViewModel(application: Application) : AndroidViewModel(application),
        SharedPreferences.OnSharedPreferenceChangeListener, BluetoothService.IBluetoothServiceListener {
    private var isConnectionAutomatic: Boolean = true

    protected val message = MutableLiveData<String>()
    protected val app: Application = getApplication()
    protected val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)

    protected var isConnected = MutableLiveData<Boolean>().apply { value = false }
    private val isTimeToRefreshLayout = MutableLiveData<Boolean>()

    protected val bluetoothService: BluetoothService
    private val connectionState = MutableLiveData<Bundle>()
    private var lastDevice: BluetoothDevice? = null

    private val deviceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

            when (intent?.action) {
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    if (isConnectionAutomatic || device?.address != lastDevice?.address) return
                    sharedPreferences
                            .unregisterOnSharedPreferenceChangeListener(this@BaseMainViewModel)
                    isConnected.value = false
                    connectionState.value = Bundle().apply {
                        putParcelable(
                                app.getString(R.string.key_mainviewmodel_device),
                                device)
                        putInt(
                                context?.getString(R.string.key_mainviewmodel_devicestate),
                                BluetoothConstants.DeviceState.DISCONNECTED
                        )
                        putString(app.getString(R.string.key_mainviewmodel_message),
                                app.getString(R.string.message_connectionlost, device?.name))
                    }
                }
            }

        }
    }


    init {
        BluetoothAdapter.getDefaultAdapter().enable()
        app.registerReceiver(
                deviceReceiver,
                IntentFilter().apply {
                    addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
                    addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                })

        val mHandler = Handler(
                Handler.Callback { msg ->
                    val x = msg.obj as Bundle
                    val readBuf = x.getString(app.getString(R.string.key_mainviewmodel_message))
                    val device: BluetoothDevice = x.getParcelable(app.getString(R.string.key_mainviewmodel_device))

                    val bundle = Bundle().apply {
                        putParcelable(app.getString(R.string.key_mainviewmodel_device), device)
                    }
                    val deviceStateKey = app.getString(R.string.key_mainviewmodel_devicestate)
                    when (readBuf[0].toString()) {
                        BluetoothConstants.Contract.ON_FEEDBACK_RECEIVED -> {
                            isConnectionAutomatic = false
                            PreferenceManager
                                    .getDefaultSharedPreferences(application)
                                    .edit()
                                    .putString(application.getString(R.string.pref_key_deviceaddress), device.address)
                                    .apply()
                            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
                            fixControls(readBuf)
                            isTimeToRefreshLayout.value = true
                            sharedPreferences.registerOnSharedPreferenceChangeListener(this)

                            bundle.apply {
                                putInt(deviceStateKey, BluetoothConstants.DeviceState.DISPLAY_CONNECTED)
                                putString(app.getString(R.string.key_mainviewmodel_message),
                                        app.getString(R.string.message_connected, device.name))
                            }
                            connectionState.value = bundle
                            isConnected.value = true
                        }
                        BluetoothConstants.Contract.ON_PIN_STATUS_RECEIVED -> {
                            if ((readBuf[1] - 48).toInt() == 0) {
                                bundle.apply {
                                    putInt(deviceStateKey, BluetoothConstants.DeviceState.INVALID_PASSWORD)
                                    putString(app.getString(R.string.key_mainviewmodel_message),
                                            app.getString(R.string.error_password))
                                }
                                connectionState.value = bundle
                                if (isConnectionAutomatic) {
                                    isConnectionAutomatic = false
                                    closeConnection()
                                }
                            }
                        }
                        else -> {
                            bundle.apply {
                                putInt(deviceStateKey, BluetoothConstants.DeviceState.INVALID_DEVICE)
                                putString(app.getString(R.string.key_mainviewmodel_message),
                                        app.getString(R.string.error_invaliddevice))
                            }
                            connectionState.value = bundle
                        }


                    }
                    true
                }
        )

        bluetoothService = BluetoothService(mHandler, this)
        val defaultAddress = app.getString(R.string.pref_default_deviceaddress)
        val deviceAddress = PreferenceManager
                .getDefaultSharedPreferences(app)
                .getString(
                        app.getString(R.string.pref_key_deviceaddress),
                        defaultAddress
                )
        if (deviceAddress != defaultAddress && BluetoothAdapter.getDefaultAdapter().isEnabled) {
            onDeviceSelected(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress))
        } else {
            isConnectionAutomatic = false
        }
    }

    fun isConnected(): LiveData<Boolean> = isConnected

    fun getMessage(): LiveData<String> = message

    protected abstract fun fixControls(readBuf: String)

    override fun onCleared() {
        super.onCleared()
        PreferenceManager
                .getDefaultSharedPreferences(getApplication())
                .unregisterOnSharedPreferenceChangeListener(this)

        app.unregisterReceiver(deviceReceiver)
        bluetoothService.stop()
    }

    fun getConnectionState(): LiveData<Bundle> = connectionState


    fun onDeviceSelected(device: BluetoothDevice) {
        bluetoothService.startClient(device, BluetoothConstants.uuid, app)
    }

    override fun onConnectionFailed(device: BluetoothDevice?) {
        if (isConnectionAutomatic) {
            isConnectionAutomatic = false
            return
        }

        connectionState.postValue(Bundle().apply {
            putInt(app.getString(R.string.key_mainviewmodel_devicestate),
                    BluetoothConstants.DeviceState.CONNECTION_FAILED)
            putString(
                    app.getString(R.string.key_mainviewmodel_message),
                    app.getString(R.string.message_connectionfailed, device?.name))
            putParcelable(app.getString(R.string.key_mainviewmodel_device), device)
        })
    }

    override fun onConnectionStarted(device: BluetoothDevice) {
        lastDevice = device
        if (isConnectionAutomatic) {
            sendPassword(sharedPreferences.getString(
                    app.getString(R.string.pref_key_password),
                    app.getString(R.string.pref_default_password)))
        } else {
            connectionState.postValue(Bundle().apply {
                putInt(
                        app.getString(R.string.key_mainviewmodel_devicestate),
                        BluetoothConstants.DeviceState.CONNECTED
                )
                putParcelable(
                        app.getString(R.string.key_mainviewmodel_device),
                        device
                )
            })
        }

    }

    fun sendPassword(passwordText: String) {
        bluetoothService.write("${BluetoothConstants.Contract.REQUEST}$passwordText\n")
    }

    fun closeConnection() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        bluetoothService.stop()
    }


    fun isTimeToRefreshLayout(): LiveData<Boolean> = isTimeToRefreshLayout

}