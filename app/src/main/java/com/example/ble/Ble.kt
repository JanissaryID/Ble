package com.example.ble

import android.R
import android.app.ListActivity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.LeScanCallback
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import java.util.logging.Handler


//class DeviceScanActivity : ListActivity() {
//    private var mLeDeviceListAdapter: LeDeviceListAdapter? = null
//    private var mBluetoothAdapter: BluetoothAdapter? = null
//    private var mScanning = false
//    private var mHandler: Handler? = null
//    public override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        actionBar.setTitle(R.string.title_devices)
//        mHandler = Handler()
//        // Use this check to determine whether BLE is supported on the device.  Then you can
//        // selectively disable BLE-related features.
//        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show()
//            finish()
//        }
//        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
//        // BluetoothAdapter through BluetoothManager.
//        val bluetoothManager = getSystemService<Any>(Context.BLUETOOTH_SERVICE) as BluetoothManager
//        mBluetoothAdapter = bluetoothManager.adapter
//        // Checks if Bluetooth is supported on the device.
//        if (mBluetoothAdapter == null) {
//            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show()
//            finish()
//            return
//        }
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.main, menu)
//        if (!mScanning) {
//            menu.findItem(R.id.menu_stop).setVisible(false)
//            menu.findItem(R.id.menu_scan).setVisible(true)
//            menu.findItem(R.id.menu_refresh).setActionView(null)
//        } else {
//            menu.findItem(R.id.menu_stop).setVisible(true)
//            menu.findItem(R.id.menu_scan).setVisible(false)
//            menu.findItem(R.id.menu_refresh).setActionView(
//                R.layout.actionbar_indeterminate_progress
//            )
//        }
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.getItemId()) {
//            R.id.menu_scan -> {
//                mLeDeviceListAdapter!!.clear()
//                scanLeDevice(true)
//            }
//            R.id.menu_stop -> scanLeDevice(false)
//        }
//        return true
//    }
//
//    override fun onResume() {
//        super.onResume()
//        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
//        // fire an intent to display a dialog asking the user to grant permission to enable it.
//        if (!mBluetoothAdapter!!.isEnabled) {
//            if (!mBluetoothAdapter!!.isEnabled) {
//                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
//            }
//        }
//        // Initializes list view adapter.
//        mLeDeviceListAdapter = LeDeviceListAdapter()
//        listAdapter = mLeDeviceListAdapter
//        scanLeDevice(true)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
//        // User chose not to enable Bluetooth.
//        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_CANCELED) {
//            finish()
//            return
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        scanLeDevice(false)
//        mLeDeviceListAdapter!!.clear()
//    }
//
//    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
//        val device = mLeDeviceListAdapter!!.getDevice(position) ?: return
//        val intent = Intent(this, DeviceControlActivity::class.java)
//        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.name)
//        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.address)
//        if (mScanning) {
//            mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
//            mScanning = false
//        }
//        startActivity(intent)
//    }
//
//    private fun scanLeDevice(enable: Boolean) {
//        if (enable) {
//            // Stops scanning after a pre-defined scan period.
//            mHandler.postDelayed(Runnable {
//                mScanning = false
//                mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
//                invalidateOptionsMenu()
//            }, SCAN_PERIOD)
//            mScanning = true
//            mBluetoothAdapter!!.startLeScan(mLeScanCallback)
//        } else {
//            mScanning = false
//            mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
//        }
//        invalidateOptionsMenu()
//    }
//
//    // Adapter for holding devices found through scanning.
//    private inner class LeDeviceListAdapter : BaseAdapter() {
//        private val mLeDevices: ArrayList<BluetoothDevice>
//        private val mInflator: LayoutInflater
//        fun addDevice(device: BluetoothDevice) {
//            if (!mLeDevices.contains(device)) {
//                mLeDevices.add(device)
//            }
//        }
//
//        fun getDevice(position: Int): BluetoothDevice {
//            return mLeDevices[position]
//        }
//
//        fun clear() {
//            mLeDevices.clear()
//        }
//
//        override fun getCount(): Int {
//            return mLeDevices.size()
//        }
//
//        override fun getItem(i: Int): Any {
//            return mLeDevices[i]
//        }
//
//        override fun getItemId(i: Int): Long {
//            return i.toLong()
//        }
//
//        override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
//            var view: View? = view
//            val viewHolder: ViewHolder
//            // General ListView optimization code.
//            if (view == null) {
//                view = mInflator.inflate(R.layout.listitem_device, null)
//                viewHolder = ViewHolder()
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
//
//        init {
//            mLeDevices = ArrayList()
//            mInflator = this@DeviceScanActivity.layoutInflater
//        }
//    }
//
//    // Device scan callback.
//    private val mLeScanCallback =
//        LeScanCallback { device, rssi, scanRecord ->
//            runOnUiThread {
//                mLeDeviceListAdapter!!.addDevice(device)
//                mLeDeviceListAdapter!!.notifyDataSetChanged()
//            }
//        }
//
//    internal class ViewHolder {
//        var deviceName: TextView? = null
//        var deviceAddress: TextView? = null
//    }
//
//    companion object {
//        private const val REQUEST_ENABLE_BT = 1
//
//        // Stops scanning after 10 seconds.
//        private const val SCAN_PERIOD: Long = 10000
//    }
//}