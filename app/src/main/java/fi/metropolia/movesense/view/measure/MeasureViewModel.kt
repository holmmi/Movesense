package fi.metropolia.movesense.view.measure

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.movesense.mds.MdsConnectionListener
import com.movesense.mds.MdsException
import com.movesense.mds.MdsNotificationListener
import com.movesense.mds.MdsResponseListener
import fi.metropolia.movesense.bluetooth.MovesenseConnector
import fi.metropolia.movesense.model.AccDataResponse


class MeasureViewModel(application: Application) : AndroidViewModel(application) {
    private val movesenseConnector = MovesenseConnector(application.applicationContext)

    private val _accData = MutableLiveData<AccDataResponse>()
    val accData: LiveData<AccDataResponse>
        get() = _accData


    init {
        movesenseConnector.initMds()
    }

    fun connect(address: String) =
        movesenseConnector.connect(address, object : MdsConnectionListener {
            override fun onConnect(p0: String?) {
                Log.d("btstatus", "device onConnect $p0")
            }

            override fun onConnectionComplete(macAddress: String?, serial: String?) {
                Log.d("btstatus", "device onConnectionComplete $macAddress $serial")
                if (serial != null) {
                    getInfo(serial)
                    subscribe(serial)
                }
            }

            override fun onError(p0: MdsException?) {
                Log.d("btstatus", "device onError $p0")
            }

            override fun onDisconnect(p0: String?) {
                Log.d("btstatus", "device onDisconnect $p0")
            }
        })

    private fun getInfo(serial: String) =
        movesenseConnector.getInfo(serial, object : MdsResponseListener {
            override fun onSuccess(s: String) {
                Log.i(
                    "btstatus",
                    "Device $serial /info request succesful: $s"
                )
            }

            override fun onError(e: MdsException) {
                Log.e(
                    "btstatus",
                    "Device $serial /info returned error: $e"
                )
            }
        })

    private fun subscribe(serial: String) =
        movesenseConnector.subscribe(serial, object : MdsNotificationListener {
            override fun onNotification(data: String?) {
                val accResponse: AccDataResponse =
                    Gson().fromJson(data, AccDataResponse::class.java)
                if (accResponse.body.array.isNotEmpty()) {
                    _accData.postValue(accResponse)
                }
            }

            override fun onError(p0: MdsException?) {
                TODO("Not yet implemented")
            }

        })

}