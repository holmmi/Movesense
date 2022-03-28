package fi.metropolia.movesense.view.start

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import fi.metropolia.movesense.bluetooth.MovesenseCallback
import fi.metropolia.movesense.bluetooth.MovesenseDevice
import fi.metropolia.movesense.bluetooth.MovesenseScanner
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StartViewModel(application: Application) : AndroidViewModel(application) {
    private val _movesenseDevices = MutableLiveData<List<MovesenseDevice>?>(null)

    val movesenseDevices: LiveData<List<MovesenseDevice>?>
        get() = _movesenseDevices

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean>
        get() = _isSearching.asStateFlow()

    private val scannerCallback = object : MovesenseCallback {
        override fun onDeviceFound(movesenseDevices: List<MovesenseDevice>) {
            _movesenseDevices.postValue(movesenseDevices)
        }
    }

    private val movesenseScanner = MovesenseScanner(application.applicationContext, scannerCallback)

    fun startScan() {
        _movesenseDevices.value = null
        viewModelScope.launch {
            movesenseScanner.startScan()
            if (movesenseScanner.startScan()) {
                _isSearching.emit(true)
            }
            delay(SCAN_TIMEOUT)
            stopScan()
            _isSearching.emit(false)
        }
    }

    fun stopScan() = movesenseScanner.stopScan()

    companion object {
        private const val SCAN_TIMEOUT = 5000L
    }
}