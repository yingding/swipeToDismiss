package com.example.android.wearable.composeforwearos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.edgeSwipeToDismiss
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.example.android.wearable.composeforwearos.nav.NavMenuScreen
import com.example.android.wearable.composeforwearos.nav.NavScreen
import com.example.android.wearable.composeforwearos.theme.WearAppTheme
import com.example.android.wearable.composeforwearos.charts.GenSampleDataHelper
import com.example.android.wearable.composeforwearos.charts.TimeScoreData
import com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi
import com.google.android.horologist.compose.navscaffold.NavScaffoldViewModel
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.composable
import com.google.android.horologist.compose.navscaffold.scrollable

class MainActivity : ComponentActivity() {
    private var navController: NavHostController? = null
    private var lineChartTimeScoreMap = GenSampleDataHelper.genChartTimeScoreMap()

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            navController = rememberSwipeDismissableNavController()
            WearAppTheme {
                AndroidViewWithNavHostApp(
                    navController = navController!!,
                    dataMap = lineChartTimeScoreMap,
                    onNavBack = this::onNavBack
                )
            }
        }
    }

    private fun onNavBack() {
        // go back one
        navController?.popBackStack()
    }
}

@OptIn(ExperimentalHorologistComposeLayoutApi::class)
@Composable
fun AndroidViewWithNavHostApp(
    navController: NavHostController,
    dataMap: Map<Long, TimeScoreData>,
    onNavBack: () -> Unit
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()
    val swipeDismissableNavHostState = rememberSwipeDismissableNavHostState(swipeToDismissBoxState)

    // SwipeDismissableNavHost has no Scrollable, switched to WearNavScaffold from horologist
    WearNavScaffold(
        startDestination = NavScreen.Menu.route,
        navController = navController,
        state = swipeDismissableNavHostState
    ) {
        scrollable(
            route = NavScreen.Menu.route,
        ) {
            NavMenuScreen(
                navigateToRoute = { route -> navController.navigate(route) },
                scrollState = it.scrollableState
            )
        }

        scrollable(NavScreen.Graphs.route) {
            // ScrollAway, On, Off
            it.timeTextMode = NavScaffoldViewModel.TimeTextMode.ScrollAway
            it.viewModel.vignettePosition =
                NavScaffoldViewModel.VignetteMode.On(VignettePosition.TopAndBottom)
            it.positionIndicatorMode =
                NavScaffoldViewModel.PositionIndicatorMode.On
            /* Composable holds two data */
            GraphsScreen(
                verticalScrollState = it.scrollableState,
                swipeToDismissBoxState = swipeToDismissBoxState,
                dataMap = dataMap,
                onNavBack = onNavBack
            )
        }

        composable(NavScreen.Settings.route) {
            val horizontalScrollState = rememberScrollState()
            Box(modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    modifier = Modifier
                            // disable the SwipeBack on Text Composable
                        .edgeSwipeToDismiss(swipeToDismissBoxState, 0.dp)
                        .horizontalScroll(horizontalScrollState),
                    text = "This text can be scrolled horizontally - " +
                            "to dismiss, swipe " +
                            "right from the left edge of the screen" +
                            " (called Edge Swiping)"
                )
            }
        }

    } // end of wear nav scaffold
}