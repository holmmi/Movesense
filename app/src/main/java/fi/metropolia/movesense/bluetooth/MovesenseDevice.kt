package fi.metropolia.movesense.bluetooth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovesenseDevice(
    val name: String,
    val macAddress: String,
    val rssi: Int,
    val serial: String? = null
) : Parcelable