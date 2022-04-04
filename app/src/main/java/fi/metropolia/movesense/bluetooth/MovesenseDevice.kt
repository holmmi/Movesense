package fi.metropolia.movesense.bluetooth

data class MovesenseDevice(
    val name: String,
    val macAddress: String,
    val rssi: Int,
    val serial: String? = null
)