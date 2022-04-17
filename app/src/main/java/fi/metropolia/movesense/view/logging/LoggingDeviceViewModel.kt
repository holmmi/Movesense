package fi.metropolia.movesense.view.logging

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.movesense.mds.MdsConnectionListener
import com.movesense.mds.MdsException
import fi.metropolia.movesense.bluetooth.MovesenseConnector

class LoggingDeviceViewModel(application: Application) : AndroidViewModel(application) {
    private val movesenseConnector = MovesenseConnector(application.applicationContext)
    private var deviceSerial: String? = null

    private val mdsConnectionListener = object : MdsConnectionListener {
        override fun onConnect(p0: String?) {
        }

        override fun onConnectionComplete(macAddress: String?, serial: String?) {
            deviceSerial = serial
        }

        override fun onError(p0: MdsException?) {
        }

        override fun onDisconnect(p0: String?) {
        }

    }

    fun connect(deviceAddress: String) {
        movesenseConnector.connect(deviceAddress, mdsConnectionListener)
    }

    companion object {
        private val TAG = LoggingDeviceViewModel::class.simpleName
    }
}