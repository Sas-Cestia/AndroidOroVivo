package fr.cestia.common_files.tools

fun String.decodeHex(): String {
    require(length % 2 == 0) { "Must have an even length" }
    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
        .toString(Charsets.US_ASCII)  // Or whichever encoding your input uses
}