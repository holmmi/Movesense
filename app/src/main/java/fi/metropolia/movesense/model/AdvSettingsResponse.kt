package fi.metropolia.movesense.model

import com.google.gson.annotations.SerializedName

class AdvSettingsResponse(@field:SerializedName("Content") val content: Content) {
    class Content(
        @field:SerializedName("AdvPacket") val advPacket: Array<Int>?,
        @field:SerializedName("ScanRespPacket") val scanRespPacket: Int?,
        @field:SerializedName("Interval") val interval: Int?,
        @field:SerializedName("Timeout") val timeout: Int?,
    )
}