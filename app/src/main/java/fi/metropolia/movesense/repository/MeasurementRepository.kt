package fi.metropolia.movesense.repository

import android.content.Context
import androidx.lifecycle.asLiveData
import fi.metropolia.movesense.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MeasurementRepository(context: Context) {
    private val measurementDao = MovesenseDatabase.getInstance(context).measurementDao()

    suspend fun addMeasurementInformation(measurementInformation: MeasurementInformation) =
        withContext(Dispatchers.IO) {
            measurementDao.addMeasurementInformation(measurementInformation)
        }

    suspend fun deleteMeasurementInformation(id: Long) = withContext(Dispatchers.IO) {
        measurementDao.deleteMeasurementInformation(id)
    }

    fun getMeasurementInformation() = measurementDao.getMeasurementInformation().asLiveData()

    suspend fun addAccelerometerData(accelerometerData: List<MeasurementAccelerometer>) = withContext(Dispatchers.IO) {
        measurementDao.addAccelerometerData(accelerometerData)
    }

    suspend fun addGyroscopeData(gyroscopeData: List<MeasurementGyroscope>) = withContext(Dispatchers.IO) {
        measurementDao.addGyroscopeData(gyroscopeData)
    }

    suspend fun addMagnetometerData(magnetometerData: List<MeasurementMagnetometer>) = withContext(Dispatchers.IO) {
        measurementDao.addMagnetometerData(magnetometerData)
    }

    suspend fun getAccelerometerData(informationId: Long) = withContext(Dispatchers.IO) {
        measurementDao.getAccelerometerData(informationId)
    }

    suspend fun getGyroscopeData(informationId: Long) = withContext(Dispatchers.IO) {
        measurementDao.getGyroscopeData(informationId)
    }

    suspend fun getMagnetometerData(informationId: Long) = withContext(Dispatchers.IO) {
        measurementDao.getMagnetometerData(informationId)
    }
}