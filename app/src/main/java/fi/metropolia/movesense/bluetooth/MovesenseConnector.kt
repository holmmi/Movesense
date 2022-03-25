package fi.metropolia.movesense.bluetooth

import android.bluetooth.BluetoothManager
import android.content.Context
import com.movesense.mds.Mds;
import com.movesense.mds.MdsConnectionListener
import com.movesense.mds.MdsException

class MovesenseConnector(context: Context) : MdsConnectionListener {
    //val mds: Mds = Mds.builder().build(context)
    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter

    override fun onConnect(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun onConnectionComplete(p0: String?, p1: String?) {
        TODO("Not yet implemented")
    }

    override fun onError(p0: MdsException?) {
        TODO("Not yet implemented")
    }

    override fun onDisconnect(p0: String?) {
        TODO("Not yet implemented")
    }

    fun isBluetoothEnabled(): Boolean =
        bluetoothAdapter != null && bluetoothAdapter.isEnabled
}