package fi.metropolia.movesense.view.start

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fi.metropolia.movesense.bluetooth.MovesenseCallback
import fi.metropolia.movesense.bluetooth.MovesenseDevice
import fi.metropolia.movesense.bluetooth.MovesenseScanner

class StartViewModel(application: Application) : AndroidViewModel(application) {
    private val _movesenseDevices = MutableLiveData<List<MovesenseDevice>?>(null)
    val movesenseDevices: LiveData<List<MovesenseDevice>?>
        get() = _movesenseDevices

    private val scannerCallback = object : MovesenseCallback {
        override fun onDeviceFound(ruuviTagDevices: List<MovesenseDevice>) {
            _movesenseDevices.postValue(ruuviTagDevices)
        }
    }

    private val ruuviTagScanner = MovesenseScanner(application.applicationContext, scannerCallback)

    fun startScan(): Boolean {
        _movesenseDevices.value = null
        return ruuviTagScanner.startScan()
    }

    fun stopScan() = ruuviTagScanner.stopScan()
}