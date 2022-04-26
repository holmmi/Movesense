package fi.metropolia.movesense.model.api

data class LoginRequest(
    val username: String,
    val password: String,
)