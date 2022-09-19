package com.example.ble

import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionsRequired

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsRequiredState(
    multiplePermissionState: MultiplePermissionsState
) {
    PermissionsRequired(
        multiplePermissionsState = multiplePermissionState,
        permissionsNotGrantedContent = { /* ... */ },
        permissionsNotAvailableContent = { /* ... */ }
    ) {
        // Use location
    }
}