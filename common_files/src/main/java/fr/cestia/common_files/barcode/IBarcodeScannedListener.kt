package fr.cestia.common_files.barcode

interface IBarcodeScannedListener {
    fun newBarcodeScanned(barcode: String?)
}