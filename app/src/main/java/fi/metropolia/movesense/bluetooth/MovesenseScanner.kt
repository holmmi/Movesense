package fi.metropolia.movesense.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context

@SuppressLint("MissingPermission")
class MovesenseScanner(context: Context, private val scannerCallback: MovesenseCallback) {
    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val leScanner = bluetoothAdapter.bluetoothLeScanner

    private var leScanResults = mutableListOf<MovesenseDevice>()

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.let {
                if (leScanResults.all { result -> result.macAddress != it.device.address }) {
                    leScanResults.add(
                        MovesenseDevice(
                            it.device.name ?: "-",
                            it.device.address,
                            it.rssi
                        )
                    )
                    scannerCallback.onDeviceFound(leScanResults)
                }
            }
        }
    }

    fun startScan(): Boolean {
        if (!isBluetoothEnabled()) {
            return false
        }
        leScanResults = mutableListOf()
        val scanFilter = ScanFilter.Builder()
            //.setManufacturerData(SUUNTO_MANUFACTURER_ID, byteArrayOf())
            .build()
        val scanSettings = ScanSettings.Builder()
            .build()
        leScanner.startScan(listOf(scanFilter), scanSettings, leScanCallback)
        return true
    }

    fun stopScan() {
        leScanner.stopScan(leScanCallback)
    }

    fun isBluetoothEnabled(): Boolean =
        bluetoothAdapter != null && bluetoothAdapter.isEnabled

    companion object {
        private const val SUUNTO_MANUFACTURER_ID = 0x009F
    }
}