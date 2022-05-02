package fi.metropolia.movesense.model.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class RegisterResponse(
    @SerializedName("username")
    @Expose
    val username: Username? = null,

    @SerializedName("password")
    @Expose
    val password: Password? = null,

    @SerializedName("name")
    @Expose
    val name: Name? = null,

    @SerializedName("organizationId")
    @Expose
    val organizationId: OrganizationId? = null,
)

class Name {
    @SerializedName("value")
    @Expose
    val value: String? = null

    @SerializedName("msg")
    @Expose
    val msg: String? = null

    @SerializedName("param")
    @Expose
    val param: String? = null

    @SerializedName("location")
    @Expose
    val location: String? = null
}

data class OrganizationId(
    @SerializedName("msg")
    @Expose
    val msg: String? = null,

    @SerializedName("param")
    @Expose
    val param: String? = null,

    @SerializedName("location")
    @Expose
    val location: String? = null
)

data class Password(
    @SerializedName("value")
    @Expose
    val value: String? = null,

    @SerializedName("msg")
    @Expose
    val msg: String? = null,

    @SerializedName("param")
    @Expose
    val param: String? = null,

    @SerializedName("location")
    @Expose
    val location: String? = null
)

data class Username(
    @SerializedName("value")
    @Expose
    val value: String? = null,

    @SerializedName("msg")
    @Expose
    val msg: String? = null,

    @SerializedName("param")
    @Expose
    val param: String? = null,

    @SerializedName("location")
    @Expose
    val location: String? = null
)



