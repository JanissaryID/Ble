package com.example.ble

import android.Manifest
import android.R
import android.app.ListActivity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.app.ActivityCompat
//import java.util.logging.Handler

private const val SCAN_PERIOD: Long = 10000

/**
 * Activity for scanning and displaying available BLE devices.
 */
class DeviceScan(
    private val bluetoothAdapter: BluetoothAdapter,
    private val handler: Handler
) : ListActivity() {

    private var mScanning: Boolean = false

    private var leDeviceListAdapter: LeDeviceListAdapter? =null

    private fun scanLeDevice(enable: Boolean) {
        when (enable) {
            true -> {
                // Stops scanning after a pre-defined scan period.
                handler.postDelayed({
                    mScanning = false
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.BLUETOOTH_SCAN
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return@postDelayed
                    }
                    bluetoothAdapter.stopLeScan(leScanCallback)
                }, SCAN_PERIOD)
                mScanning = true
                bluetoothAdapter.startLeScan(leScanCallback)
            }
            else -> {
                mScanning = false
                bluetoothAdapter.stopLeScan(leScanCallback)
            }
        }
    }

    private val leScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        runOnUiThread {
            leDeviceListAdapter!!.addDevice(device)
            leDeviceListAdapter!!.notifyDataSetChanged()
        }
    }

    private inner class LeDeviceListAdapter : BaseAdapter() {
        private val mLeDevices: ArrayList<BluetoothDevice>
        private val mInflator: LayoutInflater
        fun addDevice(device: BluetoothDevice) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device)
            }
        }

        fun getDevice(position: Int): BluetoothDevice {
            return mLeDevices[position]
        }

        fun clear() {
            mLeDevices.clear()
        }

        override fun getCount(): Int {
            return mLeDevices.size
        }

        override fun getItem(i: Int): Any {
            return mLeDevices[i]
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            TODO("Not yet implemented")
            val device = mLeDevices[position]
            Log.i("Device", ":${device}")
        }

//        override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
//            var view: View? = view
//            val viewHolder: DeviceScanActivity.ViewHolder
//            // General ListView optimization code.
//            if (view == null) {
//                view = mInflator.inflate(R.layout.listitem_device, null)
//                viewHolder = DeviceScanActivity.ViewHolder()
//                viewHolder.deviceAddress = view.findViewById(R.id.device_address)
//                viewHolder.deviceName = view.findViewById(R.id.device_name)
//                view.setTag(viewHolder)
//            } else {
//                viewHolder = view.getTag()
//            }
//            val device = mLeDevices[i]
//            val deviceName = device.name
//            if (deviceName != null && deviceName.length > 0) viewHolder.deviceName!!.text =
//                deviceName else viewHolder.deviceName.setText(R.string.unknown_device)
//            viewHolder.deviceAddress!!.text = device.address
//            return view
//        }

        init {
            mLeDevices = ArrayList()
            mInflator = this@DeviceScan.layoutInflater
        }
    }
}