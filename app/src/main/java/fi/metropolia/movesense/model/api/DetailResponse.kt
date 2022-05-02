package fi.metropolia.movesense.model.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DetailResponse(
    @SerializedName("id")
    @Expose
    val id: Int? = null,

    @SerializedName("username")
    @Expose
    val username: String? = null,

    @SerializedName("name")
    @Expose
    val name: String? = null,

    @SerializedName("organization_id")
    @Expose
    val organization_id: Int? = null,
)
