package fr.cestia.common_files.barcode

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_NOT_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DWManager @Inject constructor(
    @ApplicationContext private val context: Context
) : IBarcodeScannedListener {

    companion object {
        const val DW_PROFILE_NAME = "SinexORV"
    }

    // DataWedge Extras
    private val EXTRA_CREATE_PROFILE = "com.symbol.datawedge.api.CREATE_PROFILE"
    private val EXTRA_SET_CONFIG = "com.symbol.datawedge.api.SET_CONFIG"

    // DataWedge Actions
    private val ACTION_DATAWEDGE = "com.symbol.datawedge.api.ACTION"

    val packageName = context.packageName

    private val _scannedCode = MutableStateFlow<String>("")
    val scannedCode: StateFlow<String> = _scannedCode

    fun configure() {
        // Send DataWedge intent with extra to create profile
        // Use CREATE_PROFILE: http://techdocs.zebra.com/datawedge/latest/guide/api/createprofile/
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE, EXTRA_CREATE_PROFILE, DW_PROFILE_NAME)

        // Configure created profile to apply to this app
        val profileConfig = Bundle()

        // Configure barcode input plugin
        profileConfig.putString("PROFILE_NAME", DW_PROFILE_NAME)
        profileConfig.putString("PROFILE_ENABLED", "true")
        profileConfig.putString("CONFIG_MODE", "UPDATE") // Update specified settings in profile

        // PLUGIN_CONFIG bundle properties
        val rfidConfig = Bundle()
        rfidConfig.putString("PLUGIN_NAME", "RFID")
        rfidConfig.putString("RESET_CONFIG", "true")

        // PARAM_LIST bundle properties
        val rfidProps = Bundle()
        rfidProps.putString("rfid_input_enabled", "false")
        rfidConfig.putBundle("PARAM_LIST", rfidProps)
        profileConfig.putBundle("PLUGIN_CONFIG", rfidConfig)

        // Apply configs
        // Use SET_CONFIG: http://techdocs.zebra.com/datawedge/latest/guide/api/setconfig/
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig)

        // Configure intent output for captured data to be sent to this app
        val intentConfig = Bundle()
        intentConfig.putString("PLUGIN_NAME", "INTENT")
        intentConfig.putString("RESET_CONFIG", "true")
        val intentProps = Bundle()
        intentProps.putString("intent_output_enabled", "true")
        intentProps.putString("intent_action", "$packageName.ACTION")
        intentProps.putString("intent_delivery", "2")
        intentConfig.putBundle("PARAM_LIST", intentProps)
        profileConfig.putBundle("PLUGIN_CONFIG", intentConfig)
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig)

        val appConfig = Bundle()
        appConfig.putString("PACKAGE_NAME", packageName) //  Associate the profile with this app
        appConfig.putStringArray("ACTIVITY_LIST", arrayOf("*"))
        profileConfig.putParcelableArray("APP_LIST", arrayOf(appConfig))
        sendDataWedgeIntentWithExtra(ACTION_DATAWEDGE, EXTRA_SET_CONFIG, profileConfig)
    }

    private fun sendDataWedgeIntentWithExtra(action: String, extraKey: String, extras: Bundle) {
        val dwIntent = Intent()
        dwIntent.action = action
        dwIntent.putExtra(extraKey, extras)
        context.sendBroadcast(dwIntent)
    }


    private fun sendDataWedgeIntentWithExtra(action: String, extraKey: String, extraValue: String) {
        val dwIntent = Intent()
        dwIntent.action = action
        dwIntent.putExtra(extraKey, extraValue)
        context.sendBroadcast(dwIntent)
    }

    private val dataWedgeReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (action == "$packageName.ACTION") {
                val decodedData: String? = intent.getStringExtra("com.symbol.datawedge.data_string")
                newBarcodeScanned(decodedData)
            }
        }
    }

    fun registerDWReceiver() {
        val filter = IntentFilter()
        filter.addAction("com.symbol.datawedge.api.NOTIFICATION_ACTION") // for notification result
        filter.addAction("com.symbol.datawedge.api.RESULT_ACTION") // for error code result
        filter.addCategory(Intent.CATEGORY_DEFAULT) // needed to get version info

        // register to received broadcasts via DataWedge scanning
        filter.addAction("$packageName.ACTION")
        filter.addAction("$packageName.service.ACTION")
        context.registerReceiver(dataWedgeReceiver, filter, RECEIVER_NOT_EXPORTED)
    }

    fun unregisterDWReceiver() {
        context.unregisterReceiver(dataWedgeReceiver)
    }

    override fun newBarcodeScanned(barcode: String?) {
        _scannedCode.value = barcode ?: ""
    }
}