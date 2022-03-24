package fi.metropolia.movesense.bluetooth

interface MovesenseCallback {
    fun onDeviceFound(movesenseDevices: List<MovesenseDevice>)
}