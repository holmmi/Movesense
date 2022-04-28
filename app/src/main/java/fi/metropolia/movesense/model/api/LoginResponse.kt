package fi.metropolia.movesense.model.api

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token")
    val token: String? = null
)
