package fi.metropolia.movesense.bluetooth

import android.content.Context
import com.movesense.mds.*
import com.movesense.mds.Mds.URI_EVENTLISTENER

//some code is from https://bitbucket.org/movesense/movesense-mobile-lib/src/master/android/samples/SensorSample/app/src/main/java/com/movesense/samples/sensorsample/MainActivity.java

class MovesenseConnector(private val context: Context) {
    private var mds: Mds? = null
    private var mdsSubscription: MdsSubscription? = null

    fun initMds() {
        mds = Mds.builder().build(context)
    }

    fun connect(deviceAddress: String, callback: MdsConnectionListener) =
        mds?.connect(deviceAddress, callback)

    fun getInfo(serial: String, callback: MdsResponseListener) {
        val uri = "$SCHEME_PREFIX$serial/Info"
        mds?.get(uri, null, callback)
    }

    fun subscribe(serial: String, callback: MdsNotificationListener) {
        val sb = StringBuilder()
        val strContract: String =
            sb.append("{\"Uri\": \"").append(serial).append(URI_MEAS_ACC_13).append("\"}")
                .toString()
        if (mdsSubscription != null) {
            unsubscribe()
        }

        mdsSubscription = mds?.subscribe(
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
        private val SCHEME_PREFIX = "suunto://";
        private const val URI_MEAS_ACC_13 = "/Meas/Acc/13"
    }
}