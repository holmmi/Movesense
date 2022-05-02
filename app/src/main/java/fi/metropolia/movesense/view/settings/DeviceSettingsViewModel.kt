package fi.metropolia.movesense.view.settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.movesense.mds.MdsConnectionListener
import com.movesense.mds.MdsException
import com.movesense.mds.MdsHeader
import com.movesense.mds.MdsResponseListener
import fi.metropolia.movesense.bluetooth.MovesenseCallback
import fi.metropolia.movesense.bluetooth.MovesenseConnector
import fi.metropolia.movesense.bluetooth.MovesenseDevice
import fi.metropolia.movesense.bluetooth.MovesenseScanner
import fi.metropolia.movesense.model.AdvSettingsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DeviceSettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val movesenseConnector = MovesenseConnector(application.applicationContext)
    private var deviceSerial = ""
    private val gson = Gson()

    private val scannerCallback = object : MovesenseCallback {
        override fun onDeviceFound(movesenseDevices: List<MovesenseDevice>) {
            _movesenseDevices.postValue(movesenseDevices)
        }
    }
    private val movesenseScanner = MovesenseScanner(application.applicationContext, scannerCallback)

    private val _movesenseDevices = MutableLiveData<List<MovesenseDevice>?>(null)
    val movesenseDevices: LiveData<List<MovesenseDevice>?>
        get() = _movesenseDevices

    private val _isSearching = MutableLiveData(false)
    val isSearching: LiveData<Boolean>
        get() = _isSearching

    private val _isConnected = MutableLiveData(false)
    val isConnected: LiveData<Boolean>
        get() = _isConnected

    private val _advSettings = MutableLiveData<AdvSettingsResponse>(null)
    val advSettings: LiveData<AdvSettingsResponse>
        get() = _advSettings

    fun connect(address: String) =
        movesenseConnector.connect(address, object : MdsConnectionListener {
            override fun onConnect(p0: String?) {
                Log.d(TAG, "device onConnect $p0")
            }

            override fun onConnectionComplete(macAddress: String?, serial: String?) {
                Log.d(TAG, "device onConnectionComplete $macAddress $serial")
                if (serial != null) {
                    deviceSerial = serial
                    getAdvertisementSettings(serial)
                    _isConnected.postValue(true)
                }
            }

            override fun onError(p0: MdsException?) {
                Log.d(TAG, "device onError $p0")
            }

            override fun onDisconnect(p0: String?) {
                Log.d(TAG, "device onDisconnect $p0")
                _isConnected.postValue(false)
            }
        })

    fun disconnect() = movesenseConnector.disconnect(deviceSerial)

    private fun getAdvertisementSettings(serial: String) {
        movesenseConnector.getAdvertisementSettings(serial, object : MdsResponseListener {
            override fun onSuccess(data: String?, header: MdsHeader?) {
                val settingsResponse: AdvSettingsResponse =
                    gson.fromJson(data, AdvSettingsResponse::class.java)
                if (settingsResponse.content.timeout != null) {
                    _advSettings.postValue(settingsResponse)
                }
            }

            override fun onError(e: MdsException?) {
                Log.e(
                    TAG,
                    "Device $serial adv settings request returned exception: ${e?.localizedMessage}"
                )
            }
        })
    }

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

    fun stopScan() = movesenseScanner.stopScan()

    companion object {
        private val TAG = SettingsViewModel::class.simpleName
        private const val SCAN_TIMEOUT = 10000L
    }
}