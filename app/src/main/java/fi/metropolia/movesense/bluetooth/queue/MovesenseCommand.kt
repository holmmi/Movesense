package fi.metropolia.movesense.bluetooth.queue

enum class MovesenseCommandMethod {
    DELETE,
    GET,
    POST,
    PUT
}

data class MovesenseCommand(
    val commandMethod: MovesenseCommandMethod,
    val uri: String,
    val data: String?
)

data class MovesenseCommandResponse(
    val commandIndex: Int,
    val response: String?,
    val failed: Boolean,
    val isLastCommand: Boolean
)