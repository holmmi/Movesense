package fi.metropolia.movesense.model.api

import com.google.gson.annotations.SerializedName

data class OrganizationResponse(
    val organizations: List<OrganizationDetails>
)

data class OrganizationDetails(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String? = null
)
