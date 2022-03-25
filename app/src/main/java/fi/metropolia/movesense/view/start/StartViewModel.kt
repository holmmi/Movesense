package fi.metropolia.movesense.view.start

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fi.metropolia.movesense.bluetooth.MovesenseCallback
import fi.metropolia.movesense.bluetooth.MovesenseConnector
import fi.metropolia.movesense.bluetooth.MovesenseDevice
import fi.metropolia.movesense.bluetooth.MovesenseScanner
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StartViewModel(application: Application) : AndroidViewModel(application) {
    private val movesenseConnector =
        MovesenseConnector(application.applicationContext)
    private val _movesenseDevices = MutableLiveData<List<MovesenseDevice>?>(null)
    val movesenseDevices: LiveData<List<MovesenseDevice>?>
        get() = _movesenseDevices

    private val scannerCallback = object : MovesenseCallback {
        override fun onDeviceFound(movesenseDevices: List<MovesenseDevice>) {
            _movesenseDevices.postValue(movesenseDevices)
        }
    }

    private val movesenseScanner = MovesenseScanner(application.applicationContext, scannerCallback)

    fun startScan(): Boolean {
        _movesenseDevices.value = null
        return movesenseScanner.startScan()
    }

    fun stopScan() = movesenseScanner.stopScan()

    fun isBluetoothEnabled(): Flow<Boolean> = flow {
        while (true) {
            emit(movesenseConnector.isBluetoothEnabled())
            delay(CHECK_BLUETOOTH)
        }
    }

    companion object {
        private const val CHECK_BLUETOOTH = 1000L
    }
}