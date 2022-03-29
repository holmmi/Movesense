package fi.metropolia.movesense.bluetooth

import android.content.Context
import android.util.Log
import com.movesense.mds.Mds
import com.movesense.mds.MdsConnectionListener
import com.movesense.mds.MdsException
import com.movesense.mds.MdsResponseListener

class MovesenseConnector(private val context: Context) {
    private val mds: Mds = Mds.builder().build(context)

    fun connect(deviceAddress: String, callback: MdsConnectionListener) =
        mds.connect(deviceAddress, callback)

    fun getInfo(serial: String, callback: MdsResponseListener) {
        val uri: String = "fi.metropolia.movesense/" + serial + "/Info"
        mds.get(uri, null, callback)
    }
}