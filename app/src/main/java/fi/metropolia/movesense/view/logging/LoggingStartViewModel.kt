package fi.metropolia.movesense.view.logging

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import fi.metropolia.movesense.bluetooth.MovesenseCallback
import fi.metropolia.movesense.bluetooth.MovesenseDevice
import fi.metropolia.movesense.bluetooth.MovesenseScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoggingStartViewModel(application: Application) : AndroidViewModel(application) {
    private val _movesenseDevices = MutableLiveData<List<MovesenseDevice>?>(null)
    val movesenseDevices: LiveData<List<MovesenseDevice>?>
        get() = _movesenseDevices

    private val _isScanning = MutableLiveData(false)
    val isScanning: LiveData<Boolean>
        get() = _isScanning

    private val movesenseCallback = object : MovesenseCallback {
        override fun onDeviceFound(movesenseDevices: List<MovesenseDevice>) {
            _movesenseDevices.postValue(movesenseDevices)
        }
    }

    private val movesenseScanner = MovesenseScanner(application.applicationContext, movesenseCallback)

    fun startScan() {
        _movesenseDevices.value = null
        viewModelScope.launch(Dispatchers.IO) {
            movesenseScanner.startScan()
            _isScanning.postValue(true)
            delay(SCAN_DELAY)
            movesenseScanner.stopScan()
            _isScanning.postValue(false)
        }
    }

    fun stopScan() {
        movesenseScanner.stopScan()
        _isScanning.postValue(false)
    }

    companion object {
        private const val SCAN_DELAY = 10000L
    }
}