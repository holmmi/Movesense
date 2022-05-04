package fi.metropolia.movesense.view.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import fi.metropolia.movesense.database.MeasurementAccelerometer
import fi.metropolia.movesense.database.MeasurementGyroscope
import fi.metropolia.movesense.database.MeasurementMagnetometer
import fi.metropolia.movesense.model.MovesenseLogDataResponse
import fi.metropolia.movesense.repository.MeasurementRepository
import fi.metropolia.movesense.type.MeasureType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sqrt

class HistoryDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val measurementRepository = MeasurementRepository(application.applicationContext)

    private var accelerationData: List<MeasurementAccelerometer>? = null
    private var gyroData: List<MeasurementGyroscope>? = null
    private var magnData: List<MeasurementMagnetometer>? = null

    private var _measureType = MutableLiveData(MeasureType.Acceleration)
    val measureType: LiveData<MeasureType>
        get() = _measureType

    private var combineAxis = false

    fun toggleCombineAxis() {
        combineAxis = !combineAxis
        getEntries(combineAxis)
    }

    private var _entriesX = MutableLiveData<List<Entry>>()
    val entriesX: LiveData<List<Entry>>
        get() = _entriesX

    private var _entriesY = MutableLiveData<List<Entry>>()
    val entriesY: LiveData<List<Entry>>
        get() = _entriesY

    private var _entriesZ = MutableLiveData<List<Entry>>()
    val entriesZ: LiveData<List<Entry>>
        get() = _entriesZ

    private val _dataIndex = MutableLiveData(0)
    val dataIndex: LiveData<Int>
        get() = _dataIndex

    fun changeMeasureType(measureType: MeasureType) {
        _measureType.postValue(measureType)
        getEntries(combineAxis)
    }

    fun getData(measurementId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            accelerationData =
                measurementRepository.getAccelerometerData(measurementId)
            gyroData =
                measurementRepository.getGyroscopeData(measurementId)
            magnData =
                measurementRepository.getMagnetometerData(measurementId)
            getEntries(combineAxis)
        }
    }

    private fun getEntries(combineAxis: Boolean) {
        val data: List<MovesenseLogDataResponse.Data>? = when (measureType.value!!) {
            MeasureType.Acceleration -> accelerationData?.map { value ->
                MovesenseLogDataResponse.Data(value.x, value.y, value.z)
            }
            MeasureType.Gyro -> gyroData?.map { value ->
                MovesenseLogDataResponse.Data(value.x, value.y, value.z)
            }
            MeasureType.Magnetic -> magnData?.map { value ->
                MovesenseLogDataResponse.Data(value.x, value.y, value.z)
            }
        }
        if (combineAxis) {
            _entriesX.postValue(
                data?.mapIndexed { index, value ->
                    Entry(
                        index.toFloat(),
                        sqrt(
                            value.x.pow(2) +
                                    value.y.pow(2) +
                                    value.z.pow(2)
                        ).minus(if (measureType.value == MeasureType.Acceleration) G else 0.0) //subtract gravity if acceleration is selected
                            .toFloat()
                    )

                }
            )
            _entriesY.postValue(listOf())
            _entriesZ.postValue(listOf())
        } else {
            _entriesX.postValue(data?.mapIndexed { index, value ->
                Entry(accelerationData?.last()?.timestamp?.toFloat() ?: index.toFloat(), value.x.toFloat())
            })
            _entriesY.postValue(data?.mapIndexed { index, value ->
                Entry(index.toFloat(), value.y.toFloat())
            })
            _entriesZ.postValue(data?.mapIndexed { index, value ->
                Entry(index.toFloat(), value.z.toFloat())
            })
        }
    }

    companion object {
        const val G = 9.81
    }
}