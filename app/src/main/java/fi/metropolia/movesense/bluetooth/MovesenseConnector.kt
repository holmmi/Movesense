package fi.metropolia.movesense.bluetooth

import android.content.Context
import android.util.Log
import com.movesense.mds.*
import com.movesense.mds.Mds.URI_EVENTLISTENER

//some code is from https://bitbucket.org/movesense/movesense-mobile-lib/src/master/android/samples/SensorSample/app/src/main/java/com/movesense/samples/sensorsample/MainActivity.java

class MovesenseConnector(context: Context) {
    private val mds: Mds = Mds.builder().build(context)
    private var mdsSubscription: MdsSubscription? = null

    fun connect(deviceAddress: String, callback: MdsConnectionListener) {
        try {
            mds.connect(deviceAddress, callback)
        } catch (e: Exception) {
            Log.e(TAG, "connect exception ${e.localizedMessage}")
        }
    }

    fun disconnect(deviceAddress: String) = mds.disconnect(deviceAddress)

    fun getInfo(serial: String, callback: MdsResponseListener) {
        val uri = "$SCHEME_PREFIX$serial/Info"
        mds.get(uri, null, callback)
    }

    fun subscribe(serial: String, callback: MdsNotificationListener) {
        val sb = StringBuilder()
        val strContract: String = sb
            .append("{\"Uri\": \"")
            .append(serial).append(URI_MEAS_IMU_9)
            .append("\"}")
            .toString()
        if (mdsSubscription != null) {
            unsubscribe()
        }

        mdsSubscription = mds.subscribe(
            URI_EVENTLISTENER,
            strContract, callback
        )
    }

    private fun unsubscribe() {
        if (mdsSubscription != null) {
            mdsSubscription!!.unsubscribe()
            mdsSubscription = null
        }
    }

    companion object {
        private const val SCHEME_PREFIX = "suunto://";
        private const val URI_MEAS_IMU_9 = "/Meas/IMU9/13"
        private val TAG = MovesenseConnector::class.simpleName
    }
}