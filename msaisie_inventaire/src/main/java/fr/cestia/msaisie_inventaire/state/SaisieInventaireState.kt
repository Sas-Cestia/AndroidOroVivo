package fr.cestia.msaisie_inventaire.state

import android.bluetooth.BluetoothDevice

sealed class SaisieInventaireState {
    data object Initial : SaisieInventaireState()
    data object Loading : SaisieInventaireState()
    data object AppairDevice : SaisieInventaireState()
    data class SelectDevice(val devices: List<BluetoothDevice>) : SaisieInventaireState()
    object Connecting : SaisieInventaireState()
    object Connected : SaisieInventaireState()
    data object Success : SaisieInventaireState()
    data object Error : SaisieInventaireState()
}