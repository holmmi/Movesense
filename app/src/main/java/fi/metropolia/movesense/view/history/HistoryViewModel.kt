package fi.metropolia.movesense.view.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import fi.metropolia.movesense.repository.MeasurementRepository

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val measurementRepository = MeasurementRepository(application.applicationContext)

    fun getMeasurementInformation() = measurementRepository.getMeasurementInformation()
}