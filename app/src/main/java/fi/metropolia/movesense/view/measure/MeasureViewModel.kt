package fi.metropolia.movesense.view.measure

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.movesense.mds.MdsConnectionListener
import com.movesense.mds.MdsException
import com.movesense.mds.MdsResponseListener
import fi.metropolia.movesense.bluetooth.MovesenseConnector
import fi.metropolia.movesense.bluetooth.MovesenseDevice


class MeasureViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application
    private val movesenseConnector = MovesenseConnector(app.applicationContext)
    private lateinit var movesenseDevices: List<MovesenseDevice>

    fun connect(address: String) =
        movesenseConnector.connect(address, object : MdsConnectionListener {
            override fun onConnect(p0: String?) {
                Log.d("btstatus", "device onConnect $p0")
            }

            override fun onConnectionComplete(macAddress: String?, serial: String?) {
                Log.d("btstatus", "device onConnectionComplete $macAddress $serial")
                if (serial != null) {
                    getInfo(serial)
                }
            }

            override fun onError(p0: MdsException?) {
                Log.d("btstatus", "device onError $p0")
            }

            override fun onDisconnect(p0: String?) {
                Log.d("btstatus", "device onDisconnect $p0")
            }
        })

    private fun getInfo(serial: String) {
        movesenseConnector.getInfo(serial, object : MdsResponseListener {
            override fun onSuccess(s: String) {
                Log.i(
                    "BtStatus",
                    "Device $serial /info request succesful: $s"
                )
                // Display info in alert dialog
                val builder = android.app.AlertDialog.Builder(app.applicationContext)
                builder.setTitle("Device info:")
                    .setMessage(s)
                    .show()
            }

            override fun onError(e: MdsException) {
                Log.e(
                    "BtStatus",
                    "Device $serial /info returned error: $e"
                )
            }
        })
    }
}