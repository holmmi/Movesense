package fi.metropolia.movesense.bluetooth.queue

interface MovesenseCommandExecutorListener {
    fun onComplete(movesenseCommandResponse: MovesenseCommandResponse)

    fun onEachCommandCompleted()
}