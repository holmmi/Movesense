package fi.metropolia.movesense.bluetooth.queue

import android.util.Log
import com.movesense.mds.Mds
import com.movesense.mds.MdsException
import com.movesense.mds.MdsHeader
import com.movesense.mds.MdsResponseListener

class MovesenseCommandExecutor(private val mds: Mds) : MdsResponseListener {
    private var movesenseCommandExecutorListener: MovesenseCommandExecutorListener? = null

    @Volatile
    private var movesenseCommands: List<MovesenseCommand>? = null
    private var commandIndex = 0

    override fun onSuccess(data: String?, header: MdsHeader?) {
        super.onSuccess(data, header)
        movesenseCommandExecutorListener?.onComplete(
            MovesenseCommandResponse(
                commandIndex,
                data,
                false
            )
        )
        executeNextCommand()
    }

    override fun onError(exception: MdsException?) {
        Log.e(TAG, "Error when executing a command: ${exception?.localizedMessage}")
        movesenseCommandExecutorListener?.onComplete(
            MovesenseCommandResponse(
                commandIndex,
                null,
                true
            )
        )
        executeNextCommand()
    }

    fun executeCommands(
        commands: List<MovesenseCommand>,
        commandExecutorListener: MovesenseCommandExecutorListener
    ) {
        if (movesenseCommands == null) {
            movesenseCommandExecutorListener = commandExecutorListener
            movesenseCommands = commands
            executeNextCommand()
        }
    }

    fun addCommands(newCommands: List<MovesenseCommand>) {
        synchronized(this) {
            movesenseCommands = movesenseCommands?.plus(newCommands)
        }
    }

    private fun executeNextCommand() {
        synchronized(this) {
            movesenseCommands?.let {
                if (commandIndex == it.size) {
                    movesenseCommandExecutorListener?.onEachCommandCompleted()
                    resetCommandExecutor()
                    return
                }
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
                commandIndex ++
            }
        }
    }

    private fun resetCommandExecutor() {
        movesenseCommands = null
        commandIndex = 0
        movesenseCommandExecutorListener = null
    }

    companion object {
        private val TAG = MovesenseCommandExecutor::class.simpleName
    }
}