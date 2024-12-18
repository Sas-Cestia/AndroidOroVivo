package fr.cestia.common_files.rfid

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import com.zebra.rfid.api3.ACCESS_OPERATION_CODE
import com.zebra.rfid.api3.ACCESS_OPERATION_STATUS
import com.zebra.rfid.api3.BEEPER_VOLUME
import com.zebra.rfid.api3.ENUM_NEW_KEYLAYOUT_TYPE
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.HANDHELD_TRIGGER_EVENT_TYPE
import com.zebra.rfid.api3.InvalidUsageException
import com.zebra.rfid.api3.MEMORY_BANK
import com.zebra.rfid.api3.OperationFailureException
import com.zebra.rfid.api3.RFIDReader
import com.zebra.rfid.api3.RFIDResults
import com.zebra.rfid.api3.ReaderDevice
import com.zebra.rfid.api3.Readers
import com.zebra.rfid.api3.RfidEventsListener
import com.zebra.rfid.api3.RfidReadEvents
import com.zebra.rfid.api3.RfidStatusEvents
import com.zebra.rfid.api3.START_TRIGGER_TYPE
import com.zebra.rfid.api3.STATUS_EVENT_TYPE
import com.zebra.rfid.api3.STOP_TRIGGER_TYPE
import com.zebra.rfid.api3.SupportedRegions
import com.zebra.rfid.api3.TagAccess
import com.zebra.rfid.api3.TriggerInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.cestia.common_files.tools.decodeHex
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.ArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RFIDManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : IRFIDManager, RfidEventsListener {
    private val tag: String = RFIDManager::class.java.simpleName

    private var readers: Readers? = null
    private var availableRFIDReaderList: ArrayList<ReaderDevice>? = null
    lateinit var readerDevice: ReaderDevice
    lateinit var reader: RFIDReader

    // Expose l'état et les événements via des StateFlows
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _scannedTags = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val scannedTags: MutableStateFlow<List<Pair<String, String>>> = _scannedTags

    private val _newTagScanned = MutableStateFlow<Pair<String, String>?>(null)
    val newTagScanned: StateFlow<Pair<String, String>?> = _newTagScanned

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Liste les appareils Bluetooth appairés
    @SuppressLint("MissingPermission")
    fun getPairedDevices(bluetoothAdapter: BluetoothAdapter?): Map<String, BluetoothDevice> {
        if (bluetoothAdapter == null) {
            return emptyMap()
        }
        if (!bluetoothAdapter.isEnabled) {
            return emptyMap()
        }
        val pairedDevices = bluetoothAdapter.bondedDevices
        val devicesList = pairedDevices.map { it.name to it }.toMap()
        return devicesList
    }

    fun connectToDevice(selectedDevice: BluetoothDevice?): Boolean {

        try {
            if (selectedDevice == null) {
                Log.e(tag, "No device selected.")
                return false
            }

            // Init
            if (readers == null) {
                readers = Readers(context, ENUM_TRANSPORT.BLUETOOTH)
            }

            // Liste des appareils disponibles
            val availableReaders = readers?.GetAvailableRFIDReaderList()
            if (availableReaders.isNullOrEmpty()) {
                Log.e(tag, "Aucun lecteur disponible.")
                return false
            }

            readerDevice = availableReaders.find { it.address == selectedDevice.address }!!
            reader = readerDevice.rfidReader

            // Connectez-vous au lecteur
            if (!reader.isConnected) {
                reader.connect()
                configureReader()
                return true
            }
        } catch (e: InvalidUsageException) {
            Log.e(tag, "InvalidUsageException: ${e.info} ${e.vendorMessage}")
            e.printStackTrace()
            return false
        } catch (e: OperationFailureException) {
            Log.e(tag, "OperationFailureException: ${e.statusDescription} ${e.vendorMessage}")
            if (e.statusDescription == "RFID_READER_REGION_NOT_CONFIGURED") {
                setRegion()
                configureReader()
                return true
            }
            return false
        }
        Log.d(tag, "RFID Reader connection error!")
        return false
    }

    private fun setRegion(): Boolean {
        try {
            val regulatoryConfig = reader.Config.regulatoryConfig
            val supportedRegions = getSupportedRegions()
            val numberOfSupportedRegions = supportedRegions.length()
            for (i in 0 until numberOfSupportedRegions) {
                val regionInfo = reader.ReaderCapabilities.SupportedRegions.getRegionInfo(i)
                if (regionInfo.regionCode == "ETSI") {
                    regulatoryConfig.region = regionInfo.regionCode
                    regulatoryConfig.setIsHoppingOn(regionInfo.isHoppingConfigurable)
                    regulatoryConfig.setEnabledChannels(arrayOf("865700"))
                    reader.Config.setRegulatoryConfig(regulatoryConfig)
                    Log.d(tag, "Configuration de la région réussie: ETSI")
                    return true
                }
            }
            return false
        } catch (e: InvalidUsageException) {
            Log.e(
                tag,
                "Erreur lors de la configuration de la région : ${e.info} ${e.vendorMessage}"
            )
            return false
        } catch (e: OperationFailureException) {
            Log.e(tag, "Échec de l'opération : ${e.statusDescription} ${e.vendorMessage}")
            return false
        } catch (e: Exception) {
            Log.e(tag, "Erreur inattendue : ${e.message}")
            return false
        }
    }

    private fun configureReader() {
        if (reader.isConnected) {
            val triggerInfo = TriggerInfo()
            triggerInfo.StartTrigger.triggerType = START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE
            triggerInfo.StopTrigger.triggerType = STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE
            try {
                // receive events from reader
                reader.Events.addEventsListener(this)
                // HH event
                reader.Events.setHandheldEvent(true)
                // tag event with tag data
                reader.Events.setTagReadEvent(true)
                // application will collect tag using getReadTags API
                reader.Events.setAttachTagDataWithReadEvent(false)

                // set start and stop triggers
                reader.Config.startTrigger = triggerInfo.StartTrigger
                reader.Config.stopTrigger = triggerInfo.StopTrigger

                reader.Config.beeperVolume = BEEPER_VOLUME.QUIET_BEEP

                // Terminal scan, use trigger for scanning!
                val upperTrigger = ENUM_NEW_KEYLAYOUT_TYPE.RFID
                val lowerTrigger = ENUM_NEW_KEYLAYOUT_TYPE.SCAN_NOTIFY

                val result = reader.Config.setKeylayoutType(upperTrigger, lowerTrigger)
                if (result == RFIDResults.RFID_API_SUCCESS) {
                    Log.d(tag, "Configuration Keylayout réussie: $result")
                } else {
                    Log.e(tag, "Erreur de configuration Keylayout: $result")
                }

            } catch (e: InvalidUsageException) {
                Log.e(tag, "InvalidUsageException: ${e.info} ${e.vendorMessage}")
                e.printStackTrace()
            } catch (e: OperationFailureException) {
                Log.e(tag, "OperationFailureException: ${e.statusDescription} ${e.vendorMessage}")
                e.printStackTrace()
            }
        }
    }

    // Status Event Notification
    override fun eventStatusNotify(rfidStatusEvents: RfidStatusEvents) {
        Thread {
            Log.d(tag, "Status Notification: " + rfidStatusEvents.StatusEventData.statusEventType)
            if (rfidStatusEvents.StatusEventData.statusEventType === STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT) {
                if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.handheldEvent === HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED) {
                    try {
                        // Read all memory banks
                        val memoryBanksToRead =
                            arrayOf(MEMORY_BANK.MEMORY_BANK_EPC, MEMORY_BANK.MEMORY_BANK_TID);
                        for (bank in memoryBanksToRead) {
                            val ta = TagAccess()
                            val sequence = ta.Sequence(ta)
                            val op = sequence.Operation()
                            op.accessOperationCode = ACCESS_OPERATION_CODE.ACCESS_OPERATION_READ
                            op.ReadAccessParams.memoryBank =
                                bank ?: throw IllegalArgumentException("bank must not be null")
                            reader.Actions.TagAccess.OperationSequence.add(op)
                        }

                        reader.Actions.TagAccess.OperationSequence.performSequence()

                    } catch (e: Exception) {
                        Log.e(tag, "Exception: ${e.message}")
                        e.printStackTrace()
                    }
                } else if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.handheldEvent === HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED) {
                    try {
                        reader.Actions.TagAccess.OperationSequence.stopSequence()
                    } catch (e: InvalidUsageException) {
                        Log.e(tag, "InvalidUsageException: ${e.info} ${e.vendorMessage}")
                    } catch (e: OperationFailureException) {
                        Log.e(
                            tag,
                            "OperationFailureException: ${e.statusDescription} ${e.vendorMessage}"
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()
    }

    // Read Event Notification
    override fun eventReadNotify(e: RfidReadEvents) {
        Thread {
            // Each access belong to a tag.
            // Therefore, as we are performing an access sequence on 3 Memory Banks, each tag could be reported 3 times
            // Each tag data represents a memory bank
            val readTags = reader.Actions.getReadTags(10)
            if (readTags != null) {
                val readTagsList = readTags.toList()
                val tagReadGroup = readTagsList.groupBy { it.tagID }.toMutableMap()

                var epc = ""
                var tid = ""
                for (tagKey in tagReadGroup.keys) {
                    val tagValueList = tagReadGroup[tagKey]

                    for (tagData in tagValueList!!) {
                        if (tagData.opCode == ACCESS_OPERATION_CODE.ACCESS_OPERATION_READ) {
                            when (tagData.memoryBank.ordinal) {
                                MEMORY_BANK.MEMORY_BANK_EPC.ordinal -> epc =
                                    getMemBankData(tagData.memoryBankData, tagData.opStatus)

                                MEMORY_BANK.MEMORY_BANK_TID.ordinal -> tid =
                                    getMemBankData(tagData.memoryBankData, tagData.opStatus)
                                //MEMORY_BANK.MEMORY_BANK_USER.ordinal -> usr = getMemBankData(tagData.memoryBankData, tagData.opStatus)
                            }
                        }
                    }
                    newTagRead(tagKey, tid)
                }
            }
        }.start()

    }

    fun getMemBankData(memoryBankData: String?, opStatus: ACCESS_OPERATION_STATUS): String {
        return if (opStatus != ACCESS_OPERATION_STATUS.ACCESS_SUCCESS) {
            opStatus.toString()
        } else
            memoryBankData!!
    }

    private fun getSupportedRegions(): SupportedRegions {
        val supportedRegions = reader.ReaderCapabilities.SupportedRegions
        Log.d(tag, "Supported Regions: $supportedRegions")
        return supportedRegions
    }

    fun disconnect() {
        try {
            if (reader.isConnected) {
                reader.Events?.removeEventsListener(this)
                reader.disconnect()
                reader.Dispose()
                readers?.Dispose()
            }
        } catch (e: InvalidUsageException) {
            Log.e(tag, "InvalidUsageException: ${e.info} ${e.vendorMessage}")
            e.printStackTrace()
        } catch (e: OperationFailureException) {
            Log.e(tag, "OperationFailureException: ${e.statusDescription} ${e.vendorMessage}")
            e.printStackTrace()
        } catch (e: Exception) {
            Log.e(tag, "Exception: ${e.message}")
            e.printStackTrace()
        }
    }

    fun clearTags() {
        _scannedTags.value = mutableListOf()
    }

    override fun newTagRead(tag: String, idTag: String) {
        val decodedTag = tag.decodeHex()
        Log.d(this.tag, "Tag lu: $decodedTag")
        val existingTags = _scannedTags.value.map { it.first }
        val isExistingTag = existingTags.contains(decodedTag)
        if (decodedTag.isNotEmpty() && !isExistingTag) {
            _scannedTags.value = _scannedTags.value + Pair(decodedTag, idTag)
//            _newTagScanned.value = Pair(decodedTag, idTag)
            Log.d(this.tag, "Tag ajouté: $decodedTag")
            Log.d(this.tag, "Liste des tags scannés: ${_scannedTags.value}")
        }
    }
}