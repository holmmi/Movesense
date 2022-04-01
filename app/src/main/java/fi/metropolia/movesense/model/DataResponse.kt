package fi.metropolia.movesense.model

import com.google.gson.annotations.SerializedName

class DataResponse(@field:SerializedName("Body") val body: Body) {
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