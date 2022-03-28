package fi.metropolia.movesense.view.measure

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.movesense.mds.MdsConnectionListener
import com.movesense.mds.MdsException
import fi.metropolia.movesense.bluetooth.MovesenseConnector

class MeasureViewModel(application: Application) : AndroidViewModel(application) {
    private val movesenseConnector = MovesenseConnector(application.applicationContext)

    private val connectionCallback = object : MdsConnectionListener {
        override fun onConnect(p0: String?) {
            Log.d("btstatus", "device onConnect $p0")
        }

        override fun onConnectionComplete(p0: String?, p1: String?) {
            Log.d("btstatus", "device onConnectionComplete $p0 $p1")
        }

        override fun onError(p0: MdsException?) {
            Log.d("btstatus", "device onError $p0")
        }

        override fun onDisconnect(p0: String?) {
            Log.d("btstatus", "device onDisconnect $p0")
        }
    }

    fun connect(address: String) = movesenseConnector.connect(address, connectionCallback)
}