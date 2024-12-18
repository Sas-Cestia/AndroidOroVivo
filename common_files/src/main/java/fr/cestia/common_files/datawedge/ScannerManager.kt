package fr.cestia.common_files.datawedge

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScannerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _scannedData = MutableLiveData<String>()
    val scannedData: LiveData<String> = _scannedData
    private val scanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "${context.packageName}.ACTION") {
                val scanData = intent.getStringExtra("com.symbol.datawedge.data_string")
                Log.d("ScannerManager", "Scanned data:\n$scanData")
                if (!scanData.isNullOrEmpty()) {
                    _scannedData.postValue(scanData)
                }
            }
        }
    }

    private var isReceiverRegistered = false

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun registerReceiver() {
        if (isReceiverRegistered) return // Déjà enregistré
        resetScannedData()
        val filter = IntentFilter("${context.packageName}.ACTION")
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(scanReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                context.registerReceiver(scanReceiver, filter)
            }
            isReceiverRegistered = true
            Log.d("ScannerManager", "Receiver registered")
        } catch (e: Exception) {
            Log.e("ScannerManager", "Failed to register receiver: ${e.message}")
        }

    }

    fun unregisterReceiver() {
        if (!isReceiverRegistered) return // Pas besoin de désenregistrer
        resetScannedData()
        try {
            context.unregisterReceiver(scanReceiver)
            isReceiverRegistered = false
            Log.d("ScannerManager", "Receiver unregistered")
        } catch (e: IllegalArgumentException) {
            Log.e("ScannerManager", "Receiver déjà désenregistré ou non initialisé : ${e.message}")
        }
    }

    fun resetScannedData() {
        _scannedData.value = ""
    }
}