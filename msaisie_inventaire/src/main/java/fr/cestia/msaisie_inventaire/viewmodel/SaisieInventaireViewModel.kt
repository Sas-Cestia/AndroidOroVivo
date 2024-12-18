package fr.cestia.msaisie_inventaire.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.cestia.common_files.bluetooth.IBluetoothHandler
import fr.cestia.common_files.rfid.RFIDManager
import fr.cestia.data.dao.inventaire.InventaireDao
import fr.cestia.data.models.inventaire.Saisie
import fr.cestia.msaisie_inventaire.state.SaisieInventaireState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class SaisieInventaireViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bluetoothHandler: IBluetoothHandler,
    private val rfidManager: RFIDManager,
    private val inventaireDao: InventaireDao
) : ViewModel() {

    // Liste des appareils appairés et découverts
    val pairedDevices: StateFlow<List<BluetoothDevice>> = bluetoothHandler.pairedDevices
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = bluetoothHandler.discoveredDevices
    val isDiscovering: StateFlow<Boolean> = bluetoothHandler.isDiscovering

    // État du lecteur RFID
    val isConnected = rfidManager.isConnected

    val _scannedTags: MutableLiveData<List<Pair<String, String>>> = MutableLiveData(emptyList())
    val scannedTags: LiveData<List<Pair<String, String>>> = _scannedTags

    val _newTagScanned: MutableLiveData<Pair<String, String?>?> = MutableLiveData(null)
    val newTagScanned: LiveData<Pair<String, String?>?> = _newTagScanned

    val _tagCount: MutableLiveData<Int> = MutableLiveData(0)
    val tagCount: LiveData<Int> = _tagCount

    val _listVitrine: MutableLiveData<List<String>> = MutableLiveData(emptyList())
    val listVitrine: LiveData<List<String>> = _listVitrine

    val _scannedTagsByVitrine: MediatorLiveData<Map<String, Int>> = MediatorLiveData()
    val scannedTagsByVitrine: LiveData<Map<String, Int>> = _scannedTagsByVitrine

    val rfidErrorMessage = rfidManager.errorMessage

    init {
        viewModelScope.launch {
            rfidManager.scannedTags.collect { tagList ->
                handleNewTagScanned(tagList)
                Log.d("SaisieInventaireViewModel", "Scanned tags: ${_scannedTags.value}")
            }

        }
        viewModelScope.launch {
            _scannedTagsByVitrine.addSource(scannedTags) { tags ->
                _scannedTagsByVitrine.value = tags.groupBy { tag ->
                    tag.first.substring(10) // Extraire le code vitrine
                }.mapValues { (_, tagList) ->
                    tagList.size // Compter les tags pour chaque vitrine
                }
            }
        }
//        viewModelScope.launch {
//            rfidManager.newTagScanned.collect { tagPair ->
//                handleNewTagScanned(tagPair)
//                Log.d("SaisieInventaireViewModel", "New tag scanned: $tagPair")
//            }
//        }
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

            rfidManager.connectToDevice(device)

    }

    fun clearScannedTags() {
        rfidManager.clearTags()
    }

    fun initialize() {


        bluetoothHandler.initialize()

        // Vérification de l'état du Bluetooth
        if (!bluetoothHandler.isBluetoothEnabled()) {
            _alertMessage.value = "Le Bluetooth n'est pas activé. Voulez-vous l'activer ?"
            val isActivated = bluetoothHandler.requestEnableBluetooth(context)
            if (!isActivated) {
                _errorMessage.value = "Bluetooth requis pour utiliser cette fonction."
                _saisieInventaireState.value = SaisieInventaireState.Error
                return
            }
        }

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
                // Connexion automatique si un seul appareil
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

    suspend fun handleNewTagScanned(tagList: List<Pair<String, String>>) {
        _scannedTags.value = tagList
        if (tagList.isNotEmpty()) {
            tagList.forEach { tagPair ->

                try {
                    val (tag, tid) = tagPair
                    val codeArticle = "00" + tag.substring(0, 8)
                    val quantite = 1
                    val codeVitrine = tag.substring(10)
                    val codeMatiere = tag.substring(8, 9)
                    val codeFamille = tag.substring(9, 10)
                    val idRfid = tid
                    val saisie = Saisie(
                        idRfid = idRfid,
                        codeArticle = codeArticle,
                        quantite = quantite.toFloat(),
                        codeVitrine = codeVitrine,
                        codeMatiere = "A",
                        codeFamille = codeFamille
                    )
                    inventaireDao.insertSaisie(saisie)
                    if (_listVitrine.value != null && !_listVitrine.value?.contains(codeVitrine)!!) {
                        _listVitrine.value = _listVitrine.value?.plus(codeVitrine)
                    }

                    _saisieInventaireState.value = SaisieInventaireState.Success

                } catch (e: Exception) {
                    Log.e("SaisieInventaireViewModel", "Erreur lors de la saisie de l'article", e)
                    _errorMessage.value = "Erreur lors de la saisie de l'article"
                    _saisieInventaireState.value = SaisieInventaireState.Error
                }

            }
        }

    }

    init {
        _loadingMessage.value = "Recherche des lecteurs disponibles..."
        _saisieInventaireState.value = SaisieInventaireState.Loading
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
            bluetoothHandler.cleanup()
            rfidManager.disconnect()
            rfidManager.clearTags()
        }
    }
}
