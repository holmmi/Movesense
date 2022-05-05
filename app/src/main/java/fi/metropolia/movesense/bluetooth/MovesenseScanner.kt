package fi.metropolia.movesense.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context

@SuppressLint("MissingPermission")
class MovesenseScanner(val context: Context, private val scannerCallback: MovesenseCallback) {
    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val leScanner = bluetoothAdapter.bluetoothLeScanner

    private var leScanResults = mutableListOf<MovesenseDevice>()

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.let {
                if (result.device.name.startsWith(MOVESENSE_PREFIX) &&
                    leScanResults.all { result -> result.macAddress != it.device.address }) {
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
        leScanner.startScan(leScanCallback)
        return true
    }

    fun stopScan() = leScanner.stopScan(leScanCallback)

    private fun isBluetoothEnabled(): Boolean =
        bluetoothAdapter != null && bluetoothAdapter.isEnabled

    companion object {
        private const val MOVESENSE_PREFIX = "Movesense"
    }
}