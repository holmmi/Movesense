package fi.metropolia.movesense.view.start

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.movesense.mds.MdsConnectionListener
import com.movesense.mds.MdsException
import fi.metropolia.movesense.bluetooth.MovesenseCallback
import fi.metropolia.movesense.bluetooth.MovesenseConnector
import fi.metropolia.movesense.bluetooth.MovesenseDevice
import fi.metropolia.movesense.bluetooth.MovesenseScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StartViewModel(application: Application) : AndroidViewModel(application) {
    private val _movesenseDevices = MutableLiveData<List<MovesenseDevice>?>(null)
    val movesenseDevices: LiveData<List<MovesenseDevice>?>
        get() = _movesenseDevices

    private val _isSearching = MutableLiveData(false)
    val isSearching: LiveData<Boolean>
        get() = _isSearching

    private val scannerCallback = object : MovesenseCallback {
        override fun onDeviceFound(movesenseDevices: List<MovesenseDevice>) {
            _movesenseDevices.postValue(movesenseDevices)
        }
    }

    private val movesenseScanner = MovesenseScanner(application.applicationContext, scannerCallback)

    fun startScan() {
        _movesenseDevices.value = null
        viewModelScope.launch(Dispatchers.Default) {
            movesenseScanner.startScan()
            if (movesenseScanner.startScan()) {
                _isSearching.postValue(true)
            }
            delay(SCAN_TIMEOUT)
            stopScan()
            _isSearching.postValue(false)
        }
    }

    private fun stopScan() = movesenseScanner.stopScan()

    companion object {
        private const val SCAN_TIMEOUT = 10000L
    }
}