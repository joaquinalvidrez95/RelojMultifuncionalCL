package com.citsadigital.relojmultifuncionalcl.util

import java.util.*


object BluetoothConstants {
    val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    object Contract {
        const val DATE_TIME = "A"
        const val CONTROL_COMMAND = "B"
        const val WORKING_MODE = "C"
        const val TIMER_STOPWATCH_FORMAT = "D"
        const val POWER = "E"
        const val TIME_FORMAT = "F"
        const val TIMER_STOPWATCH_TIME = "G"
        const val BRIGHTNESS = "H"
        const val VOLUME = "I"
        const val REQUEST = "R"
        const val ON_FEEDBACK_RECEIVED = "V"
        const val ON_PIN_STATUS_RECEIVED = "J"
    }

    object DeviceState {
        const val DISCONNECTED = 0
        const val CONNECTED = 1
        const val DISPLAY_CONNECTED = 2
        const val CONNECTION_FAILED = 3
        const val INVALID_PASSWORD = 4
        const val INVALID_DEVICE = 5
        //const val DataReceived = 6
    }

    object ControlCommand {
        const val START = 0
        const val STOP = 1
        const val RESET = 2
    }


}