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
import fi.metropolia.movesense.types.MeasureType

class MeasureViewModel(application: Application) : AndroidViewModel(application) {
    private val movesenseConnector = MovesenseConnector(application.applicationContext)

    private val _isConnected = MutableLiveData(false)
    val isConnected: LiveData<Boolean>
        get() = _isConnected

    private val _graphData = MutableLiveData<List<MovesenseDataResponse.Array?>>(null)
    val graphData: LiveData<List<MovesenseDataResponse.Array?>>
        get() = _graphData

    private val _combineAxis = MutableLiveData(false)
    val combineAxis: LiveData<Boolean>
        get() = _combineAxis

    //sum of from all axis
    private val _combinedData = MutableLiveData<List<Double>>(listOf(0.0))
    val combinedData: LiveData<List<Double>>
        get() = _combinedData

    //averages of 10 measurements
    private val _dataAvg = MutableLiveData<MovesenseDataResponse.Array?>(null)
    val dataAvg: LiveData<MovesenseDataResponse.Array?>
        get() = _dataAvg

    private val _measureType = MutableLiveData(MeasureType.Acceleration)
    val measureType: LiveData<MeasureType>
        get() = _measureType

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
                    _isConnected.postValue(true)
                }
            }

            override fun onError(p0: MdsException?) {
                Log.d(TAG, "device onError $p0")
            }

            override fun onDisconnect(p0: String?) {
                Log.d(TAG, "device onDisconnect $p0")
                Log.d("btstatus", "device onDisconnect $p0")
                _isConnected.postValue(false)
            }
        })

    fun changeMeasureType(measureType: MeasureType) {
        //_graphData.postValue(listOf(null))
       _measureType.postValue(measureType)
    }

    fun toggleCombineAxis() {
        _combineAxis.postValue(!combineAxis.value!!)
    }

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
                val dataResponse: MovesenseDataResponse =
                    Gson().fromJson(data, MovesenseDataResponse::class.java)
                if (!dataResponse.body.arrayAcc.isNullOrEmpty() &&
                    !dataResponse.body.arrayGyro.isNullOrEmpty() &&
                    !dataResponse.body.arrayMagn.isNullOrEmpty()
                ) {
                    val selectedData = when (measureType.value) {
                        MeasureType.Acceleration -> {
                            dataResponse.body.arrayAcc
                        }

                        MeasureType.Gyro -> {
                            dataResponse.body.arrayGyro
                        }

                        MeasureType.Magnetic -> {
                            dataResponse.body.arrayGyro
                        }

                        null -> {
                            dataResponse.body.arrayAcc
                        }
                    }
                    Log.d(TAG, "selecteddata ${selectedData?.get(0)?.x}")
                    Log.d(TAG, "graphData ${graphData.value?.get(0)}")

                    if (graphData.value != null) {
                        _graphData.postValue(graphData.value?.plus(selectedData?.get(0)))
                    } else {
                        _graphData.postValue(listOf(selectedData?.get(0)))
                    }
                    calculateAverage(selectedData)
                }
            }

            override fun onError(e: MdsException?) {
                Log.e(
                    TAG,
                    "MdsNotificationListener serial $serial error ${e!!.localizedMessage}"
                )
            }
        })

    private var index: Int = 0

    private fun calculateAverage(selectedData: Array<MovesenseDataResponse.Array>?) {
        index++

        if (index > 9) {
            val xAvg = selectedData?.map { it.x }?.toTypedArray()?.average()
            val yAvg = selectedData?.map { it.y }?.toTypedArray()?.average()
            val zAvg = selectedData?.map { it.z }?.toTypedArray()?.average()
            if (xAvg != null && yAvg != null && zAvg != null) {
                _dataAvg.postValue(MovesenseDataResponse.Array(xAvg, yAvg, zAvg))
            }
            index = 0
        }
    }

    companion object {
        private val TAG = MeasureViewModel::class.simpleName
    }
}