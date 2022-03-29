package fi.metropolia.movesense.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import fi.metropolia.movesense.bluetooth.MovesenseDevice

class MovesenseParamType : NavType<MovesenseDevice>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): MovesenseDevice? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): MovesenseDevice {
        return Gson().fromJson(value, MovesenseDevice::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: MovesenseDevice) {
        bundle.putParcelable(key, value)
    }
}