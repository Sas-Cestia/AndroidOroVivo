package fr.cestia.sinex_orvx.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ScanReceiver(private val onScan: (String) -> Unit = {}) :
    BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        try {
            if (intent?.action == "fr.cestia.ON_SCAN") {
                val scanData = intent.getStringExtra("com.symbol.datawedge.data_string")
                if (scanData != null) {
                    onScan(scanData)
                } else {
                    Log.e("ScanReceiver", "Les données scannées sont nulles.")
                }
            }
        } catch (e: Exception) {
            Log.e("ScanReceiver", "Erreur dans ScanReceiver: ${e.message}")
        }
    }
}