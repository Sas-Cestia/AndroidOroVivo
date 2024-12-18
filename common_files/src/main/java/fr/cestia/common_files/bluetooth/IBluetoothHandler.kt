package fr.cestia.common_files.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.Context
import kotlinx.coroutines.flow.StateFlow

interface IBluetoothHandler {
    val pairedDevices: StateFlow<List<BluetoothDevice>>
    val discoveredDevices: StateFlow<List<BluetoothDevice>>
    val isDiscovering: StateFlow<Boolean>
    val devicesList: StateFlow<List<Pair<String, String>>>
    val connectedDevice: StateFlow<BluetoothDevice?>

    fun initialize()
    fun startDiscovery()
    fun stopDiscovery()
    fun connectToDevice(device: BluetoothDevice): Boolean
    fun configureDevice()
    fun hasPermissions(): Boolean
    fun handlePermissionsResult(
        permissions: Array<out String>,
        grantResults: IntArray,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    )

    fun isBluetoothEnabled(): Boolean
    fun requestEnableBluetooth(context: Context): Boolean
    fun cleanup()
    fun disconnectFromDevice(device: BluetoothDevice): Boolean
}