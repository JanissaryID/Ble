package com.example.ble.view

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ble.BleViewModel
import com.example.ble.component.ButtonView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomePage(
    multiplePermissionState: MultiplePermissionsState,
    bleViewModel: BleViewModel
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Bluetooth not Active"
            )

            Button(modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth(),
                shape = RoundedCornerShape(40.dp),
                elevation = null,
                onClick = {
                    multiplePermissionState.launchMultiplePermissionRequest()
                    bleViewModel.requestBluetoothPermission()
                }
            ) {
                Text(text = "Active", fontWeight = FontWeight.SemiBold)
            }
        }
//        Spacer(modifier = Modifier.size(16.dp))
//        ButtonView(title = "BLE Enable", enable =true) {
////            bluetoothViewModel.scanLeDevice(appCompatActivity)
//        }
//        Spacer(modifier = Modifier.size(16.dp))
//        ButtonView(title = "BLE Scan", enable =true) {
////            bleViewModel.AsyncScan()
//            bleViewModel.ConnectSpesificDevice()
////            bleViewModel.scanLeDevice()
////            bluetoothViewModel.scanLeDevice(appCompatActivity)
//        }
        Spacer(modifier = Modifier.size(16.dp))
        ButtonView(title = "Connect", enable =true) {
//            bleViewModel.AsyncScan()
//            bleViewModel.scanLeDevice()
            bleViewModel.ConnectSpesificDevice()
//            Log.i("Device", "Connect to device")
//            bluetoothViewModel.scanLeDevice(appCompatActivity)
        }
        Spacer(modifier = Modifier.size(16.dp))
        ButtonView(title = "Disconnect", enable =true) {
            bleViewModel.DisconnectDevice()
//            bleViewModel.AsyncScan()
//            bleViewModel.scanLeDevice()
//            bluetoothViewModel.scanLeDevice(appCompatActivity)
        }
//        Spacer(modifier = Modifier.size(16.dp))
//        ButtonView(title = "Get Value", enable =true) {
////            bleViewModel.AsyncScan()
////            bleViewModel.scanLeDevice()
////            bluetoothViewModel.scanLeDevice(appCompatActivity)
//        }
        Spacer(modifier = Modifier.size(16.dp))
        ButtonView(title = "Stop Get Value", enable =true) {
            bleViewModel.stopGetData()
//            bleViewModel.AsyncScan()
//            bleViewModel.scanLeDevice()
//            bluetoothViewModel.scanLeDevice(appCompatActivity)
        }
    }
}