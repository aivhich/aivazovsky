package com.aivhich.aivazovsky

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.aivhich.aivazovsky.states.SendingState
import com.aivhich.aivazovsky.ui.theme.AivazovskyTheme
import com.chaquo.python.Python
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID


class MainActivity : ComponentActivity() {
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter
        val status = mutableStateOf("Инициализация")

        val handler = Handler(Looper.getMainLooper()) { msg ->
            when (msg.what) {
                CONNECTION_FAILED -> {
                    status.value = "Робот не найден"
                    true
                }
                CONNECTION_SUCCESS -> {
                    status.value = "Робот подключен"
                    true
                }
                else -> false
            }
        }
        val blutoothPermission = android.Manifest.permission.BLUETOOTH_CONNECT

        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->
            if (isGranted) {
                status.value = "Проверка разрешений"
                status.value = connectHC05( bluetoothAdapter, handler)
            } else {
                status.value = "Включите доступ к Bluetooth"
            }
        }
        if (ContextCompat.checkSelfPermission(applicationContext,blutoothPermission) == PackageManager.PERMISSION_GRANTED) {
            status.value = "Подключение..."
            status.value = connectHC05( bluetoothAdapter, handler)
        } else {
            status.value = "Включите разрешения для доступа к Bluetooth"
            requestPermissionLauncher.launch(blutoothPermission)
        }
        val mainVM= MainVM(this)
        setContent {
            AivazovskyTheme {
                MainScreen(mainVM, status)
            }
        }
    }
}



@SuppressLint("MissingPermission")
private fun connectHC05(bluetoothAdapter: BluetoothAdapter?, handler: Handler): String {
    val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
    val hc05Device = pairedDevices?.find { it.name == "HC-05" }
    if (hc05Device != null) {
        ConnectThread(hc05Device, handler).start()
        return "Попытка подключения..."
    }else {
        return "Робот не найден"
    }
}


val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
const val CONNECTION_FAILED: Int = 0
const val CONNECTION_SUCCESS: Int = 1
@SuppressLint("MissingPermission")
class ConnectThread(private val monDevice: BluetoothDevice, private val handler: Handler) : Thread() {
    private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        monDevice.createRfcommSocketToServiceRecord(MY_UUID)
    }
    override fun run() {
        mmSocket?.let { socket ->
            try {
                socket.connect()
                handler.obtainMessage(CONNECTION_SUCCESS).sendToTarget()
            } catch (e: Exception) {
                handler.obtainMessage(CONNECTION_FAILED).sendToTarget()
            }
            dataExchaneInstance = DataExchange(socket)
        }
    }
}
var dataExchaneInstance: DataExchange? = null