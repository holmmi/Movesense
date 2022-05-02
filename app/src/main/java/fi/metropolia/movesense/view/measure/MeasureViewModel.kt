package fi.metropolia.movesense.view.measure

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.google.gson.Gson
import com.movesense.mds.*
import fi.metropolia.movesense.bluetooth.MovesenseConnector
import fi.metropolia.movesense.model.MovesenseDataResponse
import fi.metropolia.movesense.type.MeasureType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class MeasureViewModel(application: Application) : AndroidViewModel(application) {
    private val movesenseConnector = MovesenseConnector(application.applicationContext)

    //statuses
    private val _isConnected = MutableLiveData(false)
    val isConnected: LiveData<Boolean>
        get() = _isConnected

    private val _combineAxis = MutableLiveData(false)
    val combineAxis: LiveData<Boolean>
        get() = _combineAxis

    private val _clearData = MutableLiveData(false)
    val clearData: LiveData<Boolean>
        get() = _clearData

    //averages of 10 measurements
    private val _dataAvg = MutableLiveData<MovesenseDataResponse.Array?>(null)
    val dataAvg: LiveData<MovesenseDataResponse.Array?>
        get() = _dataAvg

    private val _measureType = MutableLiveData(MeasureType.Acceleration)
    val measureType: LiveData<MeasureType>
        get() = _measureType

    private val _rpm = MutableLiveData(0)
    val rpm: LiveData<Int>
        get() = _rpm

    private val _pitch = MutableLiveData(0.0)
    val pitch: LiveData<Double>
        get() = _pitch

    private val _roll = MutableLiveData(0.0)
    val roll: LiveData<Double>
        get() = _roll

    val gson = Gson()

    // Graph data entries, x is also used for combined data
    private val _entriesX = MutableLiveData<List<Entry>>()
    val entriesX: LiveData<List<Entry>>
        get() = _entriesX
    private val _entriesY = MutableLiveData<List<Entry>>()
    val entriesY: LiveData<List<Entry>>
        get() = _entriesY
    private val _entriesZ = MutableLiveData<List<Entry>>()
    val entriesZ: LiveData<List<Entry>>
        get() = _entriesZ

    fun connect(address: String) =
        movesenseConnector.connect(address, object : MdsConnectionListener {
            override fun onConnect(p0: String?) {
            }

            override fun onConnectionComplete(macAddress: String?, serial: String?) {
                if (serial != null) {
                    _isConnected.postValue(true)
                    subscribe(serial)
                }
            }

            override fun onError(p0: MdsException?) {
                Log.e(TAG, "device onError $p0")
            }

            override fun onDisconnect(p0: String?) {
                _isConnected.postValue(false)
            }
        })

    fun disconnect(deviceAddress: String) = movesenseConnector.disconnect(deviceAddress)

    fun changeMeasureType(measureType: MeasureType) {
        _measureType.postValue(measureType)
    }

    fun toggleCombineAxis() {
        _combineAxis.postValue(!combineAxis.value!!)
    }

    fun toggleClearData() {
        indexGraphData = 0
        _clearData.postValue(!_clearData.value!!)
        _entriesX.postValue(listOf())
        _entriesY.postValue(listOf())
        _entriesZ.postValue(listOf())
    }

    fun calculateRotation() {
        if (!entriesX.value.isNullOrEmpty() &&
            !entriesY.value.isNullOrEmpty() &&
            !entriesZ.value.isNullOrEmpty()
        ) {
            val x = entriesX.value?.last()?.y ?: 0.0F
            val y = entriesY.value?.last()?.y ?: 0.0F
            val z = entriesZ.value?.last()?.y ?: 0.0F

            _pitch.postValue(atan2(-x, sqrt(y * y + z * z)) * 180 / PI)
            _roll.postValue(atan2(y, z) * 180 / PI)
        }
    }

    private fun subscribe(serial: String) =
        movesenseConnector.subscribe(serial, object : MdsNotificationListener {
            override fun onNotification(data: String?) {
                val dataResponse: MovesenseDataResponse =
                    gson.fromJson(data, MovesenseDataResponse::class.java)
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
                    viewModelScope.launch(Dispatchers.Default) {
                        calculateRPM(dataResponse.body.arrayGyro)
                        setGraphData(selectedData)
                        calculateAverage(selectedData)
                    }
                }
            }

            override fun onError(e: MdsException?) {
                Log.e(
                    TAG,
                    "MdsNotificationListener serial $serial error ${e!!.localizedMessage}"
                )
            }
        })

    private var indexGraphData: Int = 0
    private fun setGraphData(selectedData: Array<MovesenseDataResponse.Array>?) {
        val x = selectedData?.get(0)?.x ?: 0.0
        val y = selectedData?.get(0)?.y ?: 0.0
        val z = selectedData?.get(0)?.z ?: 0.0

        if (!entriesX.value.isNullOrEmpty() ||
            !entriesY.value.isNullOrEmpty() &&
            !entriesZ.value.isNullOrEmpty()
        ) {
            if (combineAxis.value == false) {
                //if entries already contain data, add new entry
                _entriesX.postValue(
                    entriesX.value!!
                        .plus(Entry(indexGraphData.toFloat(), x.toFloat()))
                )
                _entriesY.postValue(
                    entriesY.value!!
                        .plus(Entry(indexGraphData.toFloat(), y.toFloat()))
                )
                _entriesZ.postValue(
                    entriesZ.value!!
                        .plus(Entry(indexGraphData.toFloat(), z.toFloat()))
                )
            } else {
                _entriesX.postValue(
                    //if combineaxis is on and x already contains values, add to it
                    entriesX.value!!
                        .plus(
                            Entry(
                                indexGraphData.toFloat(),
                                sqrt(
                                    x.pow(2) +
                                            y.pow(2) +
                                            z.pow(2)
                                ).minus(if (measureType.value == MeasureType.Acceleration) G else 0.0) //subtract gravity if acceleration is selected
                                    .toFloat()
                            )
                        )
                )
            }
        } else {
            //initialize entries if empty
            if (combineAxis.value == false) {
                _entriesX.postValue(listOf(Entry(indexGraphData.toFloat(), x.toFloat())))
                _entriesY.postValue(listOf(Entry(indexGraphData.toFloat(), y.toFloat())))
                _entriesZ.postValue(listOf(Entry(indexGraphData.toFloat(), z.toFloat())))
            } else {
                //if x axis entries are empty and combineaxis is true, initialize combined data for x entrie
                _entriesX.postValue(
                    listOf(
                        Entry(
                            indexGraphData.toFloat(),
                            sqrt(
                                x.pow(2) +
                                        y.pow(2) +
                                        z.pow(2)
                            ).minus(if (measureType.value == MeasureType.Acceleration) G else 0.0)
                                .toFloat()
                        )
                    )
                )
            }

        }
        indexGraphData++
    }

    private var indexRPM: Int = 0
    private var degrees: Double = 0.0
    private fun calculateRPM(gyroData: Array<MovesenseDataResponse.Array>) {
        indexRPM++
        degrees += gyroData[0].z
        if (indexRPM > 9) {
            _rpm.postValue(((degrees / indexRPM) / 6).toInt())
            indexRPM = 0
            degrees = 0.0
        }
    }

    private var indexAvg: Int = 0
    private fun calculateAverage(selectedData: Array<MovesenseDataResponse.Array>?) {
        indexAvg++
        if (indexAvg > 9) {
            val xAvg = selectedData?.map { it.x }?.toTypedArray()?.average()
            val yAvg = selectedData?.map { it.y }?.toTypedArray()?.average()
            val zAvg = selectedData?.map { it.z }?.toTypedArray()?.average()
            if (xAvg != null && yAvg != null && zAvg != null) {
                _dataAvg.postValue(MovesenseDataResponse.Array(xAvg, yAvg, zAvg))
            }
            indexAvg = 0
        }
    }

    companion object {
        private val TAG = MeasureViewModel::class.simpleName
        private const val G = 9.81
    }
}