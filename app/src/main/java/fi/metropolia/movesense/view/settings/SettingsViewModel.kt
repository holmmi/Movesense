package fi.metropolia.movesense.view.settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.movesense.mds.MdsConnectionListener
import com.movesense.mds.MdsException
import com.movesense.mds.MdsHeader
import com.movesense.mds.MdsResponseListener
import fi.metropolia.movesense.bluetooth.MovesenseCallback
import fi.metropolia.movesense.bluetooth.MovesenseConnector
import fi.metropolia.movesense.bluetooth.MovesenseDevice
import fi.metropolia.movesense.bluetooth.MovesenseScanner
import fi.metropolia.movesense.model.AdvSettingsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

}