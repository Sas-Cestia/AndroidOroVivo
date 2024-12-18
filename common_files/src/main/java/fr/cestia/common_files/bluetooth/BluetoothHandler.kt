package fr.cestia.common_files.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("MissingPermission")
@Singleton
class BluetoothHandler @Inject constructor(
    @ApplicationContext private val context: Context,
) : IBluetoothHandler {

    companion object {
        private val BLUETOOTH_PERMISSION_REQUEST_CODE = 100
        private val ACCESS_FINE_LOCATION_REQUEST_CODE = 99
    }

    private val bluetoothManager = context.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter

    // StateFlows exposés
    private val _pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDevice>> get() = _pairedDevices

    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    override val discoveredDevices: StateFlow<List<BluetoothDevice>> get() = _discoveredDevices

    private val _isDiscovering = MutableStateFlow(false)
    override val isDiscovering: StateFlow<Boolean> get() = _isDiscovering

    private val _devicesList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    override val devicesList: StateFlow<List<Pair<String, String>>> get() = _devicesList

    private val _connectedDevice = MutableStateFlow<BluetoothDevice?>(null)
    override val connectedDevice: StateFlow<BluetoothDevice?> get() = _connectedDevice

    // Liste des permissions nécessaires
    private val bluetoothPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    // BroadcastReceiver pour détecter les changements d'état de Bluetooth
    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                if (state == BluetoothAdapter.STATE_ON) {
                    // Bluetooth activé, démarrez la découverte
                    startDiscovery()
                }
            }
        }
    }

    // BroadcastReceiver pour détecter les nouveaux appareils
    private val discoveryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        _discoveredDevices.value = _discoveredDevices.value + it
                    }
                }

                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    _isDiscovering.value = true
                }

                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    _isDiscovering.value = false
                }
            }
        }
    }

    override fun initialize() {
        // Enregistrer le receiver
        val discoveryFilter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        context.registerReceiver(discoveryReceiver, discoveryFilter)

        val btStateFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(bluetoothStateReceiver, btStateFilter)

        // Initialiser les appareils déjà appairés
        bluetoothAdapter?.bondedDevices?.let {
            _pairedDevices.value = it.toList()
        }

        _devicesList.value = (_pairedDevices.value + _discoveredDevices.value)
            .filter { it.name.startsWith("RFD40") } // Filtre les éléments dont le nom commence par "RFD40"
            .map { it.name to it.address } // Crée une paire (nom, adresse)
    }

    override fun startDiscovery() {
        bluetoothAdapter?.takeIf { !it.isDiscovering }?.startDiscovery()
    }

    override fun stopDiscovery() {
        bluetoothAdapter?.takeIf { it.isDiscovering }?.cancelDiscovery()
    }

    override fun connectToDevice(device: BluetoothDevice): Boolean {
        // Implémentation spécifique pour établir une connexion (BluetoothSocket par exemple)
        return true
    }

    override fun disconnectFromDevice(device: BluetoothDevice): Boolean {
        // Implémentation spécifique pour déconnecter un appareil
        return true
    }

    override fun hasPermissions(): Boolean {
        return bluetoothPermissions.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Gère les résultats des permissions
    override fun handlePermissionsResult(
        permissions: Array<out String>,
        grantResults: IntArray,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            onGranted()
        } else {
            onDenied()
        }
    }

    override fun configureDevice() {
        // Logique de configuration Bluetooth ici
        Log.d("BluetoothHandler", "Bluetooth configuré.")
    }

    override fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled ?: false
    }

    override fun requestEnableBluetooth(context: Context): Boolean {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        if (context is Activity) {
            context.startActivityForResult(intent, BLUETOOTH_PERMISSION_REQUEST_CODE)
            return true // Attendez le retour pour valider
        }
        return false
    }

    override fun cleanup() {
        context.unregisterReceiver(discoveryReceiver)
    }
}