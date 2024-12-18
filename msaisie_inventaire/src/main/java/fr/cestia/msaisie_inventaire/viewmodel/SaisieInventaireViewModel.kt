package fr.cestia.msaisie_inventaire.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.cestia.common_files.bluetooth.IBluetoothHandler
import fr.cestia.common_files.rfid.RFIDManager
import fr.cestia.msaisie_inventaire.state.SaisieInventaireState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class SaisieInventaireViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bluetoothHandler: IBluetoothHandler,
    private val rfidManager: RFIDManager,
) : ViewModel() {

    // Liste des appareils appairés et découverts
    val pairedDevices: StateFlow<List<BluetoothDevice>> = bluetoothHandler.pairedDevices
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = bluetoothHandler.discoveredDevices
    val isDiscovering: StateFlow<Boolean> = bluetoothHandler.isDiscovering

    // État du lecteur RFID
    val isConnected = rfidManager.isConnected

    val _scannedTags: MutableLiveData<List<String>> = MutableLiveData(emptyList())
    val scannedTags: LiveData<List<String>> = _scannedTags

    val rfidErrorMessage = rfidManager.errorMessage

    init {
        viewModelScope.launch {
            rfidManager.scannedTags.collect { tagList ->
                _scannedTags.value = tagList
                Log.d("SaisieInventaireViewModel", "Scanned tags: ${_scannedTags.value}")
            }
        }
    }

    private val _saisieInventaireState =
        MutableLiveData<SaisieInventaireState>(SaisieInventaireState.Loading)
    val saisieInventaireState: LiveData<SaisieInventaireState> = _saisieInventaireState

    private val _loadingMessage = MutableLiveData("Chargement...")
    val loadingMessage: LiveData<String> = _loadingMessage

    private val _alertMessage = MutableLiveData("")
    val alertMessage: LiveData<String> = _alertMessage

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    private val _uniqueScannedTags = MutableLiveData<List<String>>()
    val uniqueScannedTags: LiveData<List<String>> = _uniqueScannedTags

    // Gestion Bluetooth
    fun startDiscovery() {
        bluetoothHandler.startDiscovery()
    }

    fun stopDiscovery() {
        bluetoothHandler.stopDiscovery()
    }

    fun connectToDevice(device: BluetoothDevice) {
        viewModelScope.launch {
            bluetoothHandler.connectToDevice(device)
        }
    }

    fun disconnectFromDevice(device: BluetoothDevice) {
        viewModelScope.launch {
            bluetoothHandler.disconnectFromDevice(device)
        }
    }

    fun connectToReader(device: BluetoothDevice) {
        viewModelScope.launch {
            rfidManager.connectToDevice(device)
        }
    }

    fun clearScannedTags() {
        rfidManager.clearTags()
    }

    suspend fun initialize() {
        _saisieInventaireState.value = SaisieInventaireState.Loading

        bluetoothHandler.initialize()

        _loadingMessage.value = "Recherche des lecteurs disponibles..."

        // Étape 1 : Vérification de l'état du Bluetooth
        if (!bluetoothHandler.isBluetoothEnabled()) {
            _alertMessage.value = "Le Bluetooth n'est pas activé. Voulez-vous l'activer ?"
            val isActivated = bluetoothHandler.requestEnableBluetooth(context)
            if (!isActivated) {
                _errorMessage.value = "Bluetooth requis pour utiliser cette fonction."
                _saisieInventaireState.value = SaisieInventaireState.Error
                return
            }
        }

        // Étape 2 : Recherche des appareils disponibles
        bluetoothHandler.startDiscovery()
        delay(5000) // Attendre que la découverte termine (ou utilisez un écouteur)
        bluetoothHandler.stopDiscovery()

        val allDevices =
            bluetoothHandler.pairedDevices.value + bluetoothHandler.discoveredDevices.value

        // Filtrage des appareils dont le nom commence par "RFD40"
        val filteredDevices = allDevices.filter { it.name?.startsWith("RFD40") == true }

        when {
            filteredDevices.isEmpty() -> {
                _errorMessage.value = "Aucun appareil disponible. Veuillez appairer un appareil."
                _saisieInventaireState.value = SaisieInventaireState.AppairDevice
                // TODO : Proposer une action pour aller dans les paramètres Bluetooth
            }

            filteredDevices.size == 1 -> {
                // Étape 3 : Connexion automatique si un seul appareil
                val device = filteredDevices.first()
                connectToReader(device)
                _saisieInventaireState.value = SaisieInventaireState.Initial
            }

            filteredDevices.size > 1 -> {
                // Demander à l'utilisateur de sélectionner un appareil
                _alertMessage.value = "Plusieurs appareils détectés. Veuillez en sélectionner un."
                _saisieInventaireState.value = SaisieInventaireState.SelectDevice(filteredDevices)
            }
        }
        _loadingMessage.value = ""
    }

    init {
        viewModelScope.launch {
            initialize()
        }
    }

    // Méthode appelée lorsque l'utilisateur sélectionne un appareil
    @SuppressLint("MissingPermission")
    fun onDeviceSelected(device: BluetoothDevice) {
        viewModelScope.launch {
            connectToReader(device)
            _saisieInventaireState.value = SaisieInventaireState.Initial
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Libérer les ressources si nécessaire
        viewModelScope.launch {
            bluetoothHandler.stopDiscovery()
            rfidManager.disconnect()
        }
    }
}
