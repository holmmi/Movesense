package fi.metropolia.movesense.view.measure

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.movesense.mds.*
import fi.metropolia.movesense.bluetooth.MovesenseConnector
import fi.metropolia.movesense.model.MovesenseDataResponse
import fi.metropolia.movesense.types.MeasureType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sqrt

class MeasureViewModel(application: Application) : AndroidViewModel(application) {
    private val movesenseConnector = MovesenseConnector(application.applicationContext)

    private val _isConnected = MutableLiveData(false)
    val isConnected: LiveData<Boolean>
        get() = _isConnected

    private val _graphData = MutableLiveData<List<MovesenseDataResponse.Array?>>()
    val graphData: LiveData<List<MovesenseDataResponse.Array?>>
        get() = _graphData

    private val _combineAxis = MutableLiveData(false)
    val combineAxis: LiveData<Boolean>
        get() = _combineAxis

    //sum of all axis
    private val _combinedData = MutableLiveData<List<Double>>()
    val combinedData: LiveData<List<Double>>
        get() = _combinedData

    //averages of 10 measurements
    private val _dataAvg = MutableLiveData<MovesenseDataResponse.Array?>(null)
    val dataAvg: LiveData<MovesenseDataResponse.Array?>
        get() = _dataAvg

    private val _measureType = MutableLiveData(MeasureType.Acceleration)
    val measureType: LiveData<MeasureType>
        get() = _measureType

    private val _clearData = MutableLiveData(false)
    val clearData: LiveData<Boolean>
        get() = _clearData

    fun connect(address: String) =
        movesenseConnector.connect(address, object : MdsConnectionListener {
            override fun onConnect(p0: String?) {
                Log.i(TAG, "device onConnect $p0")
            }

            override fun onConnectionComplete(macAddress: String?, serial: String?) {
                Log.i(TAG, "device onConnectionComplete $macAddress $serial")
                if (serial != null) {
                    getInfo(serial)
                    subscribe(serial)
                    _isConnected.postValue(true)
                }
            }

            override fun onError(p0: MdsException?) {
                Log.e(TAG, "device onError $p0")
            }

            override fun onDisconnect(p0: String?) {
                Log.i(TAG, "device onDisconnect $p0")
                _isConnected.postValue(false)
            }
        })

    fun changeMeasureType(measureType: MeasureType) {
        _measureType.postValue(measureType)
    }

    fun toggleCombineAxis() {
        _combineAxis.postValue(!combineAxis.value!!)
    }

    fun toggleClearData() {
        _graphData.postValue(listOf())
        _clearData.postValue(!_clearData.value!!)
    }

    private fun getInfo(serial: String) =
        movesenseConnector.getInfo(serial, object : MdsResponseListener {
            override fun onSuccess(s: String, header: MdsHeader) {
                Log.i(
                    TAG,
                    "Device $serial /info request successful: $s"
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

                    if (graphData.value != null) {
                        _graphData.postValue(graphData.value?.plus(selectedData[0]))
                    } else {
                        _graphData.postValue(listOf(selectedData[0]))
                    }
                    viewModelScope.launch(Dispatchers.IO) {
                        setCombinedData()
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

    private fun setCombinedData() {
        if (combinedData.value != null && !graphData.value.isNullOrEmpty()) {
            _combinedData.postValue(
                combinedData.value?.plus(
                    sqrt(
                        (graphData.value?.last()!!.x.pow(2)) +
                                (graphData.value?.last()!!.y.pow(2)) +
                                (graphData.value?.last()!!.z.pow(2))
                    ) - G
                )
            )
        } else {
            _combinedData.postValue(listOf(0.0))
        }

    }

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
        private const val G = 9.81
    }
}