package com.aivhich.aivazovsky

import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class DataExchange(mmSocket: BluetoothSocket) : Thread() {
    private val length = 1
    private val mmInStream: InputStream = mmSocket.inputStream
    private val mmOutStream: OutputStream = mmSocket.outputStream
    private val mmBuffer: ByteArray = ByteArray(length)

    fun write(bytes: ByteArray) {
        try {
            mmOutStream.write(bytes)
        } catch (_: IOException) {
        }
    }
    fun read(): String {
        var numBytesReaded = 0
        try {
            while (numBytesReaded < length) {
                val num = mmInStream.read(mmBuffer, numBytesReaded, length - numBytesReaded)
                if (num == -1) {
                    break
                }
                numBytesReaded += num
            }
            return String(mmBuffer, 0, numBytesReaded)
        } catch (e: IOException) {
            return "Произошла ошибка"
        }
    }
}