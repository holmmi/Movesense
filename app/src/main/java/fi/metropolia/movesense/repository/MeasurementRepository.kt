package fi.metropolia.movesense.repository

import android.content.Context
import androidx.lifecycle.asLiveData
import fi.metropolia.movesense.database.MeasurementInformation
import fi.metropolia.movesense.database.MovesenseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MeasurementRepository(context: Context) {
    private val measurementDao = MovesenseDatabase.getInstance(context).measurementDao()

    suspend fun addMeasurementInformation(vararg measurementInformation: MeasurementInformation) =
        withContext(Dispatchers.IO) {
            measurementDao.addMeasurementInformation(*measurementInformation)
        }

    suspend fun deleteMeasurementInformation(id: Long) = withContext(Dispatchers.IO) {
        measurementDao.deleteMeasurementInformation(id)
    }

    fun getMeasurementInformation() = measurementDao.getMeasurementInformation().asLiveData()

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