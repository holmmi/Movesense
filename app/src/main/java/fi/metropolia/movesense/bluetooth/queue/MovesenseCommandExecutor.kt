package fi.metropolia.movesense.bluetooth.queue

import android.util.Log
import com.movesense.mds.Mds
import com.movesense.mds.MdsException
import com.movesense.mds.MdsHeader
import com.movesense.mds.MdsResponseListener

class MovesenseCommandExecutor(private val mds: Mds) : MdsResponseListener {
    var movesenseCommandExecutorListener: MovesenseCommandExecutorListener? = null

    private var movesenseCommands: List<MovesenseCommand>? = null
    private var commandIndex = 0

    override fun onSuccess(data: String?, header: MdsHeader?) {
        super.onSuccess(data, header)
        movesenseCommandExecutorListener?.onComplete(
            MovesenseCommandResponse(commandIndex, data, false)
        )
        executeNextCommand()
    }

    override fun onError(exception: MdsException?) {
        Log.e(TAG, "Error when executing a command: ${exception?.localizedMessage}")
        movesenseCommandExecutorListener?.onComplete(
            MovesenseCommandResponse(commandIndex, null, true)
        )
        executeNextCommand()
    }

    fun executeCommands(commands: List<MovesenseCommand>) {
        if (movesenseCommands == null) {
            movesenseCommands = commands
            executeNextCommand()
        }
    }

    private fun executeNextCommand() {
        movesenseCommands?.let {
            val command = it[commandIndex]
            when (command.commandMethod) {
                MovesenseCommandMethod.DELETE ->
                    mds.delete(command.uri, command.data, this)
                MovesenseCommandMethod.GET ->
                    mds.get(command.uri, command.data, this)
                MovesenseCommandMethod.POST ->
                    mds.post(command.uri, command.data, this)
                MovesenseCommandMethod.PUT ->
                    mds.put(command.uri, command.data, this)
            }
            if (commandIndex + 1 == it.size) {
                movesenseCommands = null
                commandIndex = 0
                return
            }
            commandIndex ++
        }
    }

    companion object {
        private val TAG = MovesenseCommandExecutor::class.simpleName
    }
}