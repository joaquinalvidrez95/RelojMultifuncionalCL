package com.citsadigital.relojmultifuncionalcl.util

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Bundle
import android.os.Handler
import com.citsadigital.relojmultifuncionalcl.R

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*


class BluetoothService(private val handler: Handler, private val callback: IBluetoothServiceListener) {
    private var connectedThread: ConnectedThread? = null
    private var clientConnectionThread: ClientConnectionThread? = null

    init {
        start()
    }

    fun write(data: String) {
        connectedThread?.write(data)
    }

    fun startClient(hostDevice: BluetoothDevice, uuid: UUID, context: Context) {
        //        Log.d(TAG, "Conexión de cliente: Iniciada.");
        connectedThread?.cancel()
        connectedThread = null

        clientConnectionThread = ClientConnectionThread(hostDevice, uuid, context)
        clientConnectionThread?.start()
    }

    @Synchronized
    private fun start() {
        //        Log.d(TAG, "Bluetooth Service iniciado");

        // Cancel any thread attempting to make a connection
        clientConnectionThread?.cancel()
        clientConnectionThread = null


        // Cancel any thread currently running a connection
        connectedThread?.cancel()
        connectedThread = null
    }

    /**
     * Stop all threads
     */
    @Synchronized
    fun stop() {
        //        Log.d(TAG, "stop");

        clientConnectionThread?.cancel()
        clientConnectionThread = null

        connectedThread?.cancel()
        connectedThread = null
    }

    @Synchronized
    private fun onClientConnectionSucceded(bluetoothSocket: BluetoothSocket, context: Context) {
        connectedThread?.cancel()
        connectedThread = null

        connectedThread = ConnectedThread(bluetoothSocket, handler, context)
        connectedThread?.start()
        callback.onConnectionStarted(bluetoothSocket.remoteDevice)

        if (clientConnectionThread != null) {
            //            clientConnectionThread.cancel();
            clientConnectionThread = null
        }
    }

    interface IBluetoothServiceListener {
        fun onConnectionFailed(device: BluetoothDevice?)

        fun onConnectionStarted(device: BluetoothDevice)
    }

    private inner class ClientConnectionThread(device: BluetoothDevice, uuid: UUID,
                                               private val context: Context) : Thread() {
        private val bluetoothSocket: BluetoothSocket?

        init {
            // Use a temporary object that is later assigned to bluetoothSocket
            // because bluetoothSocket is final.
            var tmp: BluetoothSocket? = null

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // uuid is the app's UUID buffer, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(uuid)
            } catch (e: IOException) {
                //                Log.e(TAG, "Socket's create() method failed", e);
            }

            bluetoothSocket = tmp
        }

        override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery()

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                bluetoothSocket?.connect()
                //                Log.d(TAG, "Conexión de cliente: Exitosa");
                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                if (bluetoothSocket?.isConnected == true)
                    onClientConnectionSucceded(bluetoothSocket, context)
            } catch (connectException: IOException) {
                // Unable to connect; close the socket and return.
                //                Log.e(TAG, "No se pudo man");

                callback.onConnectionFailed(bluetoothSocket?.remoteDevice)
                try {
                    bluetoothSocket?.close()
                } catch (closeException: IOException) {
                    //                    Log.e(TAG, "Could not close the client socket", closeException);
                }

                return
            }


        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                bluetoothSocket?.close()
            } catch (e: IOException) {
                //                Log.e(TAG, "Could not close the client socket", e);
            }

        }

    }

    private inner class ConnectedThread(private val bluetoothSocket: BluetoothSocket,
                                        private val handler: Handler,
                                        private val context: Context) : Thread() {
        private val inputStream: InputStream?
        private val outputStream: OutputStream?
        private var buffer = ""

        init {
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = this.bluetoothSocket.inputStream
            } catch (e: IOException) {
                //                Log.e(TAG, "Error occurred when creating input stream", e);
            }

            try {
                tmpOut = this.bluetoothSocket.outputStream
            } catch (e: IOException) {
                //                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            inputStream = tmpIn
            outputStream = tmpOut
        }

        override fun run() {

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {

                    val inbox = inputStream?.read()
                    if (inbox == '\n'.toInt()) {
                        handler.obtainMessage(
                                0,
                                buffer.length,
                                -1,
                                Bundle().apply {
                                    putParcelable(context.getString(R.string.key_mainviewmodel_device),
                                            bluetoothSocket.remoteDevice)
                                    putString(context.getString(R.string.key_mainviewmodel_message), buffer)
                                }
                        ).sendToTarget()
                        buffer = ""

                    } else buffer += Character.toString((inbox ?: 0).toChar())

                } catch (e: IOException) {
                    //                    Log.e(TAG, "Input stream fue desconectado", e);

                    break
                }

            }
        }

        // Call this from the main activity to sendExchangeValues data to the remote device.
        fun write(bytes: ByteArray) {
            try {
                outputStream?.write(bytes)
                //                Log.d(TAG, "Si se pudo escribir");

            } catch (e: IOException) {
                //                Log.e(TAG, "Error occurred when sending data", e);
            }

        }

        fun write(data: String) {
            try {
                outputStream?.write(data.toByteArray(charset("ISO-8859-1")))

                //android.util.Log.d(javaClass.name, data)

            } catch (e: IOException) {
                //Log.e(TAG, "Error occurred when sending data", e);
            }

        }

        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                //                Log.e(TAG, "No se pudo cerrar el InputStream");
            }

            try {
                outputStream?.close()
            } catch (e: IOException) {
                //                Log.e(TAG, "No se pudo cerrar el OutputStream");
            }

            try {
                bluetoothSocket.close()
                //                Log.d(TAG, "Bluetooth socket cerrado");
            } catch (e: IOException) {
                //                Log.e(TAG, "No se pudo cerrar el socket", e);
            }

        }
    }

}