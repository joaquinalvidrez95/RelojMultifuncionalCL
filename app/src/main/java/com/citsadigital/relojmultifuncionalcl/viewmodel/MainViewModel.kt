package com.citsadigital.relojmultifuncionalcl.viewmodel

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import com.citsadigital.relojmultifuncionalcl.R

import com.citsadigital.relojmultifuncionalcl.util.BluetoothConstants
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : BaseMainViewModel(application) {
    private val timerStopwatch = MutableLiveData<String>()
    override fun fixControls(readBuf: String) {
        sharedPreferences
                .edit()
                .putString(app.getString(R.string.pref_key_workingmode), readBuf[1].toString())
                .putString(app.getString(R.string.pref_key_timerstopwatchformat), readBuf[2].toString())
                .putBoolean(app.getString(R.string.pref_key_power), readBuf[3].toInt() - 48 == 1)
                .putString(app.getString(R.string.pref_key_timeformat), readBuf[4].toString())
                .putInt(app.getString(R.string.pref_key_brightness), readBuf[11].toInt() - 48)
                .putInt(app.getString(R.string.pref_key_volume), readBuf[12].toInt() - 48)
                .apply()

        timerStopwatch.value = readBuf.substring(5, 11)

    }

    private val volume: Int
        get() = sharedPreferences.getInt(
                app.getString(R.string.pref_key_volume),
                app.resources.getInteger(R.integer.pref_default_volume)
        )

    private val brightness: Int
        get() = sharedPreferences
                .getInt(
                        app.getString(R.string.pref_key_brightness),
                        app.resources.getInteger(R.integer.pref_default_brightness)
                )

    private val workingMode: String
        get() = sharedPreferences.getString(
                app.getString(R.string.pref_key_workingmode),
                app.getString(R.string.pref_default_workingmode))

    private val timeFormat: String
        get() = sharedPreferences.getString(
                app.getString(R.string.pref_key_timeformat),
                app.getString(R.string.pref_default_timeformat))

    private val timerStopwatchFormat: String
        get() = sharedPreferences.getString(
                app.getString(R.string.pref_key_timerstopwatchformat),
                app.getString(R.string.pref_default_timerstopwatchformat))

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            app.getString(R.string.pref_key_workingmode) ->
                bluetoothService.write("${BluetoothConstants.Contract.WORKING_MODE}$workingMode\n")
            app.getString(R.string.pref_key_timerstopwatchformat) ->
                bluetoothService.write("${BluetoothConstants.Contract.TIMER_STOPWATCH_FORMAT}$timerStopwatchFormat\n")
            app.getString(R.string.pref_key_power) ->
                bluetoothService.write(BluetoothConstants.Contract.POWER +
                        "${if (sharedPreferences?.getBoolean(key, true) == true) 1 else 0}\n"
                )
            app.getString(R.string.pref_key_timeformat) ->
                bluetoothService.write("${BluetoothConstants.Contract.TIME_FORMAT}$timeFormat\n")

            app.getString(R.string.pref_key_brightness) ->
                bluetoothService.write("${BluetoothConstants.Contract.BRIGHTNESS}$brightness\n")
            app.getString(R.string.pref_key_volume) ->
                bluetoothService.write("${BluetoothConstants.Contract.VOLUME}$volume\n")

        }
    }

    fun sendTimerStopwatch(hours: Int, minutes: Int, seconds: Int) {
        if (timerStopwatchFormat != "1" || hours * 3600 + minutes * 60 + seconds <= 5999) {
            val format = "%02d"
            bluetoothService.write(
                    "${BluetoothConstants.Contract.TIMER_STOPWATCH_TIME
                    }${String.format(format, hours)
                    }${String.format(format, minutes)
                    }${String.format(format, seconds)}\n")
        } else {
            message.value = app.getString(R.string.message_invalidtimerstopwatch)
        }

    }

    fun sendDateTime() {
        val date = Calendar.getInstance().time
        bluetoothService.write("${BluetoothConstants.Contract.DATE_TIME}${SimpleDateFormat("HHmmssddMMyy").format(date)}\n")
        if (isConnected.value == true)
            message.value = app.getString(
                    R.string.message_datetimeupdated,
                    SimpleDateFormat("HH:mm:ss").format(date),
                    SimpleDateFormat("dd/MM/yy").format(date))
    }

    fun getTimerStopwatch(): LiveData<String> = timerStopwatch

    fun sendControlCommand(controlCommand: Int) {
        bluetoothService.write("${BluetoothConstants.Contract.CONTROL_COMMAND}$controlCommand\n")
    }
}