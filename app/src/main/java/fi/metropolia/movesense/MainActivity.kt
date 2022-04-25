package fi.metropolia.movesense

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.app.ActivityCompat
import fi.metropolia.movesense.navigation.Navigation
import fi.metropolia.movesense.ui.theme.MovesenseTheme

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkBt()
        setContent {
            MovesenseTheme {
                Navigation()
            }
        }
    }

    private fun checkBt() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        ActivityCompat.startActivityForResult(
            this,
            enableBtIntent,
            ENABLE_BT,
            null
        )
    }

    companion object {
        private const val ENABLE_BT = 1
    }
}

