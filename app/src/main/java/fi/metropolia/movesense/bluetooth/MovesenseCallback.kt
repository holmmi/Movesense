package fi.metropolia.movesense.bluetooth

interface MovesenseCallback {
    fun onDeviceFound(ruuviTagDevices: List<MovesenseDevice>)
}