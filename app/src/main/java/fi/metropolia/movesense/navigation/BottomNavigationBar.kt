package fi.metropolia.movesense.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import fi.metropolia.movesense.R

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val items = listOf(
            NavigationItem.Start,
            NavigationItem.History,
            NavigationItem.Settings
        )

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.navigationIcon, null) },
                label = { Text(stringResource(item.labelText)) },
                selected = currentDestination?.hierarchy?.any {
                    it.route?.startsWith(item.route, true) ?: false
                } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

private sealed class NavigationItem(val route: String, val navigationIcon: ImageVector, val labelText: Int) {
    object Start : NavigationItem(NavigationRoutes.START, Icons.Filled.Home, R.string.start)
    object History : NavigationItem(NavigationRoutes.HISTORY, Icons.Filled.History, R.string.history)
    object Settings : NavigationItem(NavigationRoutes.SETTINGS, Icons.Filled.Settings, R.string.settings)
}