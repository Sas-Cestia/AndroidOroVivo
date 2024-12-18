package fr.cestia.common_files.rfid

interface IRFIDManager {
    fun newTagRead(epc: String?)
}