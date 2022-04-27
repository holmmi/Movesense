package fi.metropolia.movesense.model.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class OrganizationResponse(
    @SerializedName("id")
    @Expose
    val id: Int? = null,
    @SerializedName("name")
    @Expose
    val name: String? = null
)