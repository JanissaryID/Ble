package com.example.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import java.lang.Exception
import java.util.*

class BleViewModel: ViewModel() {

    val SERVICE_UUID = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b")
    val CHARACTERISTICS_UUID = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8")
    val CLIENT_CHARACTERISTICS_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    var bluetoothGatt: BluetoothGatt? = null

    var ActivityApp : MainActivity? = null

    var context : Context? = null

    var returnStat : Boolean = false

    var bleDevice : BluetoothDevice? = null

    var scanFilter : ScanFilter? = null

    var scanSettings :ScanSettings? = null

    fun createInstance(appCompatActivity: MainActivity){
        bluetoothManager = appCompatActivity.applicationContext.getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        activityResultLauncher = appCompatActivity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                Log.i("Bluetooth", ":request permission result ok")
            } else {
                Log.i("Bluetooth", ":request permission result canceled / denied")
            }
        }


        ActivityApp = appCompatActivity
        context = appCompatActivity
    }

    fun requestBluetoothPermission() {
        if (bluetoothAdapter?.isEnabled == false) {
            Log.i("Bluetooth", ":Bluetooth Off Condition Wanna Turn On?")
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activityResultLauncher.launch(enableBluetoothIntent)
        }
        else{
            Log.i("Bluetooth", ":Bluetooth On Condition")
        }
    }

    fun checkBluetoothCompatible(){
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.i("Bluetooth", ":Device not Supported BLuetooth")
        }
        else{
            Log.i("Bluetooth", ":Device Supported BLuetooth")
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun ScanDevice(){
        val scanFilter = ScanFilter.Builder().build()

        val scanFilters: MutableList<ScanFilter> = mutableListOf()
        scanFilters.add(scanFilter)

        val scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()

        bluetoothAdapter.bluetoothLeScanner.startScan(scanFilters, scanSettings, BleScanCallBack)
    }

    @SuppressLint("MissingPermission")
    fun ConnectSpesificDevice(){
        Log.i("Device", "Specific")
        scanFilter = ScanFilter.Builder().setDeviceName("ESP32").build()

        val scanFilters: MutableList<ScanFilter> = mutableListOf()
        scanFilters.clear()
        scanFilter?.let { scanFilters.add(it) }
        Log.i("Device", "${scanFilters}")

        scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()

        bluetoothAdapter.bluetoothLeScanner.startScan(scanFilters, scanSettings, BleScanCallBack)

//        Log.i("Device", "Device ${bleDevice}")
    }

    private val BleScanCallBack : ScanCallback by lazy{
        object : ScanCallback(){
            @SuppressLint("MissingPermission", "NewApi")
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)

                bleDevice = result?.device
                if(bleDevice != null){
                    Log.i("Device", "${bleDevice!!.name} -- ${bleDevice!!.address} -- ${bleDevice!!.alias}")
//                    connect(address = bleDevice.address, device = bleDevice)
                    ConnectDevice(device = bleDevice!!)
                    Log.i("Device", "Connect to Device")

//                    AsyncScanConnect(bleDevice)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun ConnectDevice(device: BluetoothDevice){
        Log.i("Gatt", "Connect to Device")
        bluetoothGatt = device.connectGatt(context, false, BleGattallBack)
//        bluetoothGatt = device
    }

//    @SuppressLint("MissingPermission")
    @SuppressLint("MissingPermission")
    fun DisconnectDevice(){
        Log.i("Gatt", "Disconnect to Device")
        bluetoothGatt?.disconnect()
        scanFilter = null
        scanSettings = null
//        bleDevice = null
        Log.i("Gatt", "Disconnect ${bluetoothGatt}")
    }

    @SuppressLint("MissingPermission")
    private fun SendMessage(){
        val myService :BluetoothGattService? = bluetoothGatt?.getService(SERVICE_UUID)
        val myCharacteristicSEnd = myService?.getCharacteristic(CHARACTERISTICS_UUID)
        val message = "Ready"

        var messageBytes = ByteArray(0)
        messageBytes = message.toByteArray(charset("UTF-8"))
        myCharacteristicSEnd!!.value = messageBytes
        bluetoothGatt?.writeCharacteristic(myCharacteristicSEnd)
    }

    @SuppressLint("MissingPermission")
    fun stopGetData(){
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    private val BleGattallBack: BluetoothGattCallback by lazy{
        object : BluetoothGattCallback(){
            @SuppressLint("MissingPermission")
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                Log.i("Gatt", "Connection State Change")
                if(newState == BluetoothProfile.STATE_CONNECTED){
                    bluetoothGatt?.discoverServices()
//                    bluetoothGatt?.setCharacteristicNotification(CHARACTERISTICS_UUID, true)
                }
            }

            @SuppressLint("MissingPermission")
            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
//                super.onServicesDiscovered(gatt, status)
                Log.i("Gatt", "Service Discovered")

                val myServiceGatt = gatt!!.getService(SERVICE_UUID)
                Log.i("Gatt", "${myServiceGatt.characteristics}")
                val myCharacteristic = myServiceGatt.getCharacteristic(CHARACTERISTICS_UUID)
                myCharacteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                gatt.setCharacteristicNotification(myCharacteristic, true)

                setCharacteristicNotification(characteristic = myCharacteristic, enabled = true)

//                SendMessage()
                Log.i("Gatt", "Service Discovered 2")
            }

            override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                super.onCharacteristicRead(gatt, characteristic, status)
                Log.i("Gatt", "Characteristic Read")
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?
            ) {
                super.onCharacteristicChanged(gatt, characteristic)
                Log.i("Gatt", "Characteristic Changed ${characteristic?.getStringValue(0)}")
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun setCharacteristicNotification(
        characteristic: BluetoothGattCharacteristic,
        enabled: Boolean
    ) {
        bluetoothGatt?.let { gatt ->
            Log.i("Gatt", "Get Gatt $gatt")
            Log.i("Gatt", "Get Gatt Characteristic Descriptor ${characteristic.descriptors}")
            gatt.setCharacteristicNotification(characteristic, enabled)
//
//            // This is specific to Heart Rate Measurement.
//            characteristic.
//            c455fb05-7836-4219-af25-20a70d80bc52
            if (CHARACTERISTICS_UUID == characteristic.uuid) {
                val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTICS_UUID)
                Log.i("Gatt", "Get Gatt ${descriptor}")
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
            }
        } ?: run {
            Log.i("Gatt", "BluetoothGatt not initialized")
        }
    }

    fun AsyncScan(){
        GlobalScope.async {
            try {
//                ScanDevice()
                ConnectSpesificDevice()
//                delay(100)
            }
            catch (e: Exception){
                Log.i("Device", "Error $e")
            }
        }
    }

    fun AsyncScanConnect(device: BluetoothDevice){
        GlobalScope.async {
            try {
                ConnectDevice(device = device)
                delay(10)
            }
            catch (e: Exception){
                Log.i("Device", "Error $e")
            }
        }
    }
}