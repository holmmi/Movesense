package fi.metropolia.movesense.model

import com.google.gson.annotations.SerializedName

class MovesenseDataResponse(@field:SerializedName("Body") val body: Body) {
    class Body(
        @field:SerializedName("Timestamp") val timestamp: Long,
        @field:SerializedName("ArrayAcc") val arrayAcc: kotlin.Array<Array>,
        @field:SerializedName("ArrayGyro") val arrayGyro: kotlin.Array<Array>,
        @field:SerializedName("ArrayMagn") val arrayMagn: kotlin.Array<Array>,
        @field:SerializedName("Headers") val header: Headers
    )

    class Array(
        @field:SerializedName("x") val x: Double,
        @field:SerializedName("y") val y: Double,
        @field:SerializedName("z") val z: Double
    )

    class Headers(@field:SerializedName("Param0") val param0: Int)
}

data class MovesenseLogEntriesResponse(
    @SerializedName("Content")
    val content: Content
) {
    data class Content(
        val elements: List<LogEntry>
    )

    data class LogEntry(
        @SerializedName("Id")
        val id: Long,
        @SerializedName("ModificationTimestamp")
        val modificationTimestamp: String,
        @SerializedName("Size")
        val size: Long?
    )
}

data class MovesenseLogDataResponse(
    @SerializedName("Meas")
    val measurement: Type
) {
    data class Type(
        @SerializedName("Acc")
        val acceleration: List<Sensor>?,
        @SerializedName("Gyro")
        val gyroscope: List<Sensor>?,
        @SerializedName("Magn")
        val magnetometer: List<Sensor>?,
    )

    data class Sensor(
        @SerializedName(value = "ArrayAcc", alternate = ["ArrayGyro", "ArrayMagn"])
        val values: List<Data>,
        @SerializedName("Timestamp")
        val timestamp: Long
    )

    data class Data(
        val x: Double,
        val y: Double,
        val z: Double
    )
}