package fi.metropolia.movesense.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import fi.metropolia.movesense.view.history.HistoryView
import fi.metropolia.movesense.view.measure.MeasureView
import fi.metropolia.movesense.view.settings.SettingsView
import fi.metropolia.movesense.view.start.StartView

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun Navigation() {
    val navController = rememberAnimatedNavController()
    Scaffold(
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                AnimatedNavHost(
                    navController = navController,
                    startDestination = NavigationRoutes.START
                ) {
                    composable(
                        route = NavigationRoutes.START
                    ) { StartView(navController) }
                    composable(
                        route = NavigationRoutes.MEASURE,
                        arguments = listOf(
                            navArgument("address") { type = NavType.StringType },
                        )
                    ) {
                        MeasureView(navController, it.arguments?.getString("address"))
                    }
                    composable(
                        route = NavigationRoutes.HISTORY
                    ) { HistoryView(navController) }
                    composable(
                        route = NavigationRoutes.SETTINGS
                    ) { SettingsView(navController) }
                }
            }
        },
        bottomBar = { BottomNavigationBar(navController) }
    )
}

object NavigationRoutes {
    const val START = "start"
    const val MEASURE = "measure/{address}"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
}