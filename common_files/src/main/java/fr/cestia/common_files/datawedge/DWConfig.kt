package fr.cestia.common_files.datawedge

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DWConfig @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        const val DATAWEDGE_INTENT_ACTION = "fr.cestia.sinex_orvx.DATAWEDGE_ACTION"
        const val DATAWEDGE_PROFILE_NAME = "SinexORV"
    }

    val setConfigBundle = Bundle().apply {
        putString("PROFILE_NAME", DATAWEDGE_PROFILE_NAME)
        putString("PROFILE_ENABLED", "true")
        putString("CONFIG_MODE", "CREATE_IF_NOT_EXIST")
    }

    val appConfig = Bundle().apply {
        putString("PACKAGE_NAME", context.packageName)
        putStringArray(
            "ACTIVITY_LIST", arrayOf(
                "${context.packageName}.MainActivity",
            )
        )
    }

    val barcodeParamList = Bundle().apply {
        putString("scanner_input_enabled", "true")
        putString("scanner_selection", "auto")
        putString("charset_name", "ISO-8859-1")
        putString("auto_charset_preferred_order", "UTF-8;GB2312")
        putString("auto_charset_failure_option", "UTF-8")
        putString("volume_slider_type", "3")
    }

    val barcodeConfigBundle = Bundle().apply {
        putString("PLUGIN_NAME", "BARCODE")
        putString("RESET_CONFIG", "true")
    }

    val intentParamList = Bundle().apply {
        putString("intent_output_enabled", "true")
        putString("intent_action", DATAWEDGE_INTENT_ACTION)
        putString("intent_delivery", "2")
    }

    val intentConfigBundle = Bundle().apply {
        putString("PLUGIN_NAME", "INTENT")
        putString("RESET_CONFIG", "true")
    }

    val rfidParamList = Bundle().apply {
        putString("rfid_input_enabled", "true")
        putString("rfid_beeper_enable", "true")
        putString("rfid_led_enable", "true")
        putString("rfid_antenna_transmit_power", "30")
        putString("rfid_memory_bank", "0")
        putString("rfid_session", "1")
        putString("rfid_hardware_trigger_enabled", "true")
        putString("rfid_trigger_mode", "0")
        putString("rfid_filter_duplicate_tags", "true")
        putString("rfid_hardware_trigger_enabled", "true")
        putString("rfid_tag_read_duration", "250")
        putString("rfid_link_profile", "5")
    }

    val rfidConfigBundle = Bundle().apply {
        putString("PLUGIN_NAME", "RFID")
        putString("RESET_CONFIG", "true")
    }

    val keystrokeParamList = Bundle().apply {
        putString("keystroke_output_enabled", "false")
    }

    val keystrokeConfigBundle = Bundle().apply {
        putString("PLUGIN_NAME", "KEYSTROKE")
        putString("RESET_CONFIG", "true")
    }

    private fun setAppList() {
        setConfigBundle.putParcelableArray(
            "APP_LIST", arrayOf(
                appConfig
            )
        )
    }

    private fun setPluginConfig(setConfigBundle: Bundle) {
        setConfigBundle.remove("PLUGIN_CONFIG")
        barcodeConfigBundle.putBundle("PARAM_LIST", barcodeParamList)
        intentConfigBundle.putBundle("PARAM_LIST", intentParamList)
        rfidConfigBundle.putBundle("PARAM_LIST", rfidParamList)
        keystrokeConfigBundle.putBundle("PARAM_LIST", keystrokeParamList)

        setConfigBundle.putParcelableArrayList(
            "PLUGIN_CONFIG", arrayListOf(
                barcodeConfigBundle,
                intentConfigBundle,
                rfidConfigBundle,
                keystrokeConfigBundle
            )
        )
    }

    private fun sendConfig(bundle: Bundle) {
        val intent = Intent().apply {
            action = "com.symbol.datawedge.api.ACTION"
            putExtra("com.symbol.datawedge.api.SET_CONFIG", bundle)
        }

        context.sendBroadcast(intent)
    }

    fun initialize(): Boolean {
        try {
            Log.d("DWConfig", "Initializing DataWedge")
            disableRfidOnDefaultProfile()
            setAppList()
            setPluginConfig(setConfigBundle)
            sendConfig(setConfigBundle)
            Log.d("DWConfig", "DataWedge initialized successfully")
            return true

        } catch (e: Exception) {

            Log.e("DWConfig", "Error initializing DataWedge", e)
            return false

        }
    }

    fun disableDatawedgeConfig() {
        val bundle = Bundle().apply {
            putString("PROFILE_NAME", DATAWEDGE_PROFILE_NAME)
            putString("PROFILE_ENABLED", "false")
            putString("CONFIG_MODE", "UPDATE")
        }
        setPluginConfig(bundle)
        sendConfig(bundle)
        Log.d("DWConfig", "DataWedge disabled successfully")
    }

    fun disableRfidOnDefaultProfile() {
        val bundle = Bundle().apply {
            putString("PROFILE_NAME", "Profile0 (default)")
            putString("PROFILE_ENABLED", "true")
            putString("CONFIG_MODE", "UPDATE")
        }

        val rfidParamList = Bundle().apply {
            putString("rfid_input_enabled", "false")
        }

        val rfidConfigBundle = Bundle().apply {
            putString("PLUGIN_NAME", "RFID")
        }

        rfidConfigBundle.putBundle("PARAM_LIST", rfidParamList)

        bundle.putParcelableArrayList(
            "PLUGIN_CONFIG", arrayListOf(
                rfidConfigBundle
            )
        )

        sendConfig(bundle)
        Log.d("DWConfig", "RFID désactivé sur le profil par défaut")
    }
}