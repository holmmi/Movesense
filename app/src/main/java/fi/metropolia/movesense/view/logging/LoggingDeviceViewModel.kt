package fi.metropolia.movesense.view.logging

import android.app.Application
import android.icu.util.Measure
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.movesense.mds.MdsConnectionListener
import com.movesense.mds.MdsException
import fi.metropolia.movesense.bluetooth.MovesenseConnector
import fi.metropolia.movesense.bluetooth.queue.MovesenseCommand
import fi.metropolia.movesense.bluetooth.queue.MovesenseCommandExecutorListener
import fi.metropolia.movesense.bluetooth.queue.MovesenseCommandMethod
import fi.metropolia.movesense.bluetooth.queue.MovesenseCommandResponse
import fi.metropolia.movesense.database.MeasurementAccelerometer
import fi.metropolia.movesense.database.MeasurementGyroscope
import fi.metropolia.movesense.database.MeasurementInformation
import fi.metropolia.movesense.database.MeasurementMagnetometer
import fi.metropolia.movesense.model.MovesenseDataLoggerConfig
import fi.metropolia.movesense.model.MovesenseLogDataResponse
import fi.metropolia.movesense.model.MovesenseLogEntriesResponse
import fi.metropolia.movesense.repository.MeasurementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoggingDeviceViewModel(application: Application) : AndroidViewModel(application) {
    private val movesenseConnector = MovesenseConnector(application.applicationContext)
    private var deviceSerial: String? = null

    private val gson = Gson()

    private val measurementRepository = MeasurementRepository(application.applicationContext)

    private val _loggingStarted = MutableLiveData(false)
    val loggingStarted: LiveData<Boolean>
        get() = _loggingStarted

    private var operationType = OperationType.INITIAL_READ

    private val _operationsAllowed = MutableLiveData(false)
    val operationsAllowed: LiveData<Boolean>
        get() = _operationsAllowed

    private val mdsConnectionListener = object : MdsConnectionListener {
        override fun onConnect(p0: String?) {
        }

        override fun onConnectionComplete(macAddress: String?, serial: String?) {
            deviceSerial = serial
            val commands = listOf(
                MovesenseCommand(
                    MovesenseCommandMethod.GET,
                    "$SCHEME_PREFIX$serial/Mem/DataLogger/State",
                    null
                )
            )
            movesenseConnector.executeMovesenseCommands(commands, movesenseCommandExecutorListener)
        }

        override fun onError(p0: MdsException?) {
        }

        override fun onDisconnect(p0: String?) {
        }
    }

    private val movesenseCommandExecutorListener = object : MovesenseCommandExecutorListener {
        override fun onComplete(movesenseCommandResponse: MovesenseCommandResponse) {
            if (!movesenseCommandResponse.failed) {
                when (operationType) {
                    OperationType.INITIAL_READ, OperationType.STOP_LOGGING ->
                        _loggingStarted.postValue(movesenseCommandResponse.response?.contains("3"))
                    OperationType.START_LOGGING -> {
                        if (movesenseCommandResponse.response != null) {
                            _loggingStarted.postValue(movesenseCommandResponse.response.contains("3"))
                        }
                    }
                    OperationType.RETRIEVE_LOG_ENTRIES -> {
                        val logEntries = gson.fromJson(movesenseCommandResponse.response, MovesenseLogEntriesResponse::class.java)
                        val commands = logEntries.content.elements.map {
                            MovesenseCommand(
                                MovesenseCommandMethod.GET,
                                "${SCHEME_PREFIX}MDS/Logbook/$deviceSerial/byId/${it.id}/Data",
                                null
                            )
                        }
                        addMovesenseCommands(commands, OperationType.RETRIEVE_LOG_DATA)
                    }
                    OperationType.RETRIEVE_LOG_DATA -> {
                        val logData = gson.fromJson(movesenseCommandResponse.response, MovesenseLogDataResponse::class.java)
                        viewModelScope.launch(Dispatchers.IO) {
                            val informationId = measurementRepository.addMeasurementInformation(
                                MeasurementInformation(date = System.currentTimeMillis())
                            )
                            // Insert accelerometer data
                            logData.measurement.acceleration?.let { accelerationData ->
                                accelerationData.forEach {
                                    measurementRepository.addAccelerometerData(
                                        it.values.map { value ->
                                            MeasurementAccelerometer(
                                                informationId = informationId,
                                                x = value.x,
                                                y = value.y,
                                                z = value.z
                                            )
                                        }
                                    )
                                }
                            }
                            // Insert gyroscope data
                            logData.measurement.gyroscope?.let { gyroscopeData ->
                                gyroscopeData.forEach {
                                    measurementRepository.addGyroscopeData(
                                        it.values.map { value ->
                                            MeasurementGyroscope(
                                                informationId = informationId,
                                                x = value.x,
                                                y = value.y,
                                                z = value.z
                                            )
                                        }
                                    )
                                }
                            }
                            // Insert magnetometer data
                            logData.measurement.magnetometer?.let { magnetometerData ->
                                magnetometerData.forEach {
                                    measurementRepository.addMagnetometerData(
                                        it.values.map { value ->
                                            MeasurementMagnetometer(
                                                informationId = informationId,
                                                x = value.x,
                                                y = value.y,
                                                z = value.z
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        override fun onEachCommandCompleted() {
            _operationsAllowed.postValue(true)
        }
    }

    fun connect(deviceAddress: String) {
        movesenseConnector.connect(deviceAddress, mdsConnectionListener)
    }

    fun deleteLogs() {
        _operationsAllowed.value = false
        operationType = OperationType.DELETE_LOGS
        movesenseConnector.executeMovesenseCommands(
            listOf(
                MovesenseCommand(
                    MovesenseCommandMethod.DELETE,
                    "$SCHEME_PREFIX$deviceSerial/Mem/Logbook/Entries/",
                    null
                )
            ),
            movesenseCommandExecutorListener
        )
    }

    fun startLogging() {
        val dataLoggerConfig =
            MovesenseDataLoggerConfig(
                MovesenseDataLoggerConfig.Config(
                    MovesenseDataLoggerConfig.DataEntries(
                        SENSOR_PATHS.map { MovesenseDataLoggerConfig.DataEntry("$it/${SAMPLE_RATES[0]}") }
                    )
                )
            )
        val commands = listOf(
            MovesenseCommand(
                MovesenseCommandMethod.PUT,
            "$SCHEME_PREFIX$deviceSerial/Mem/DataLogger/Config/",
                gson.toJson(dataLoggerConfig)
            ),
            MovesenseCommand(
                MovesenseCommandMethod.PUT,
                "$SCHEME_PREFIX$deviceSerial/Mem/DataLogger/State/",
                "{\"newState\":3}"
            ),
            MovesenseCommand(
                MovesenseCommandMethod.GET,
                "$SCHEME_PREFIX$deviceSerial/Mem/DataLogger/State",
                null
            )
        )
        executeMovesenseCommands(commands, OperationType.START_LOGGING)
    }

    fun stopLogging() {
        executeMovesenseCommands(
            listOf(
                MovesenseCommand(
                    MovesenseCommandMethod.PUT,
                    "$SCHEME_PREFIX$deviceSerial/Mem/DataLogger/State/",
                    "{\"newState\":2}"
                )
            ),
            OperationType.STOP_LOGGING
        )
    }

    fun retrieveLogs() {
        executeMovesenseCommands(
            listOf(
                MovesenseCommand(
                    MovesenseCommandMethod.GET,
                    "$SCHEME_PREFIX$deviceSerial/Mem/Logbook/Entries/",
                    null
                )
            ),
            OperationType.RETRIEVE_LOG_ENTRIES
        )
    }

    private fun executeMovesenseCommands(commands: List<MovesenseCommand>, operation: OperationType) {
        _operationsAllowed.value = false
        operationType = operation
        movesenseConnector.executeMovesenseCommands(commands, movesenseCommandExecutorListener)
    }

    private fun addMovesenseCommands(commands: List<MovesenseCommand>, operation: OperationType) {
        operationType = operation
        movesenseConnector.addMovesenseCommands(commands)
    }

    private enum class OperationType {
        DELETE_LOGS, INITIAL_READ, RETRIEVE_LOG_DATA, RETRIEVE_LOG_ENTRIES, START_LOGGING, STOP_LOGGING
    }

    companion object {
        private val SAMPLE_RATES = arrayOf(13, 26, 52, 104, 208, 416, 833, 1666)
        private const val SCHEME_PREFIX = "suunto://"
        private val SENSOR_PATHS = arrayOf("/Meas/Acc", "/Meas/Gyro", "/Meas/Magn")
        private val TAG = LoggingDeviceViewModel::class.simpleName
    }
}