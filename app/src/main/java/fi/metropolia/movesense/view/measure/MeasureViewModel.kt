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
import fi.metropolia.movesense.model.MovesenseDataResponse

class MeasureViewModel(application: Application) : AndroidViewModel(application) {
    private val movesenseConnector = MovesenseConnector(application.applicationContext)

    private val _dataResp = MutableLiveData<MovesenseDataResponse>()
    val dataResp: LiveData<MovesenseDataResponse>
        get() = _dataResp

    fun connect(address: String) =
        movesenseConnector.connect(address, object : MdsConnectionListener {
            override fun onConnect(p0: String?) {
                Log.d(TAG, "device onConnect $p0")
            }

            override fun onConnectionComplete(macAddress: String?, serial: String?) {
                Log.d(TAG, "device onConnectionComplete $macAddress $serial")
                if (serial != null) {
                    getInfo(serial)
                    subscribe(serial)
                }
            }

            override fun onError(p0: MdsException?) {
                Log.d(TAG, "device onError $p0")
            }

            override fun onDisconnect(p0: String?) {
                Log.d(TAG, "device onDisconnect $p0")
            }
        })

    private fun getInfo(serial: String) =
        movesenseConnector.getInfo(serial, object : MdsResponseListener {
            override fun onSuccess(s: String) {
                Log.i(
                    TAG,
                    "Device $serial /info request succesful: $s"
                )
            }

            override fun onError(e: MdsException) {
                Log.e(
                    TAG,
                    "Device $serial /info returned error: ${e.localizedMessage}"
                )
            }
        })

    private fun subscribe(serial: String) =
        movesenseConnector.subscribe(serial, object : MdsNotificationListener {
            override fun onNotification(data: String?) {
                val accResponse: MovesenseDataResponse =
                    Gson().fromJson(data, MovesenseDataResponse::class.java)
                if (!accResponse.body.arrayAcc.isNullOrEmpty() &&
                    !accResponse.body.arrayGyro.isNullOrEmpty() &&
                    !accResponse.body.arrayMagn.isNullOrEmpty()
                ) {
                    _dataResp.postValue(accResponse)
                }
            }

            override fun onError(e: MdsException?) {
                Log.e(
                    TAG,
                    "MdsNotificationListener serial $serial error ${e!!.localizedMessage}"
                )
            }
        })

    companion object {
        private val TAG = MeasureViewModel::class.simpleName
    }
}