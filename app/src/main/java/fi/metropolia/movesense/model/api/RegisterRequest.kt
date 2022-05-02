package fi.metropolia.movesense.model.api

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("passwordConfirmation")
    val passwordConfirmation: String,
    @SerializedName("organizationId")
    val organizationId: Int
)
