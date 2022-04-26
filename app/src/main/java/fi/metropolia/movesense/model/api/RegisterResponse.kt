package fi.metropolia.movesense.model.api

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("msg")
    val msg: String? = null
)
