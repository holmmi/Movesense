package fi.metropolia.movesense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import fi.metropolia.movesense.navigation.Navigation
import fi.metropolia.movesense.ui.theme.MovesenseTheme

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovesenseTheme {
                Navigation()
            }
        }
    }
}