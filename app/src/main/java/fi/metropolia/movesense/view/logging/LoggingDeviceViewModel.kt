package fi.metropolia.movesense.view.logging

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.movesense.mds.MdsConnectionListener
import com.movesense.mds.MdsException
import fi.metropolia.movesense.bluetooth.MovesenseConnector
import fi.metropolia.movesense.bluetooth.queue.MovesenseCommand
import fi.metropolia.movesense.bluetooth.queue.MovesenseCommandExecutorListener
import fi.metropolia.movesense.bluetooth.queue.MovesenseCommandMethod
import fi.metropolia.movesense.bluetooth.queue.MovesenseCommandResponse
import fi.metropolia.movesense.model.MovesenseDataLoggerConfig

class LoggingDeviceViewModel(application: Application) : AndroidViewModel(application) {
    private val movesenseConnector = MovesenseConnector(application.applicationContext)
    private var deviceSerial: String? = null

    private val gson = Gson()

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
                        if (movesenseCommandResponse.isLastCommand) {
                            _loggingStarted.postValue(movesenseCommandResponse.response?.contains("3"))
                        }
                    }
                    OperationType.RETRIEVE_LOGS -> {
                        Log.d(TAG, "${movesenseCommandResponse.response}")
                    }
                }
            }
            Log.d("TAG", "${movesenseCommandResponse.response} ${movesenseCommandResponse.isLastCommand}")
            _operationsAllowed.postValue(movesenseCommandResponse.isLastCommand)
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
        _operationsAllowed.value = false
        operationType = OperationType.START_LOGGING
        val dataLoggerConfig =
            MovesenseDataLoggerConfig(
                MovesenseDataLoggerConfig.Config(
                    MovesenseDataLoggerConfig.DataEntries(
                        listOf(
                            MovesenseDataLoggerConfig.DataEntry("/Meas/IMU9/13")
                        )
                    )
                )
            )
        Log.d(TAG, "${gson.toJson(dataLoggerConfig)}")
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
        movesenseConnector.executeMovesenseCommands(commands, movesenseCommandExecutorListener)
    }

    fun stopLogging() {
        _operationsAllowed.value = false
        operationType = OperationType.STOP_LOGGING
        movesenseConnector.executeMovesenseCommands(
            listOf(
                MovesenseCommand(
                    MovesenseCommandMethod.PUT,
                    "$SCHEME_PREFIX$deviceSerial/Mem/DataLogger/State/",
                    "{\"newState\":2}"
                )
            ),
            movesenseCommandExecutorListener
        )
    }

    fun retrieveLogs() {
        _operationsAllowed.value = false
        operationType = OperationType.RETRIEVE_LOGS
        movesenseConnector.executeMovesenseCommands(
            listOf(
                MovesenseCommand(
                    MovesenseCommandMethod.GET,
                    "$SCHEME_PREFIX$deviceSerial/Mem/Logbook/Entries/",
                    null
                )
            ),
            movesenseCommandExecutorListener
        )
    }

    private enum class OperationType {
        DELETE_LOGS, INITIAL_READ, RETRIEVE_LOGS, START_LOGGING, STOP_LOGGING
    }

    companion object {
        private const val SCHEME_PREFIX = "suunto://"
        private val TAG = LoggingDeviceViewModel::class.simpleName
    }
}