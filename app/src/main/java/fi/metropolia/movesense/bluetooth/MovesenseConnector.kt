package fi.metropolia.movesense.bluetooth

import android.content.Context
import com.movesense.mds.Mds
import com.movesense.mds.MdsConnectionListener

class MovesenseConnector(context: Context) {
    private val mds: Mds = Mds.builder().build(context)

    fun connect(deviceAddress: String, callback: MdsConnectionListener) =
        mds.connect(deviceAddress, callback)
}