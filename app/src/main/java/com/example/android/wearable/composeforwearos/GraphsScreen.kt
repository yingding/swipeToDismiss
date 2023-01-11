package com.example.android.wearable.composeforwearos

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.SwipeToDismissBoxState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.edgeSwipeToDismiss
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import com.example.android.wearable.composeforwearos.theme.WearAppTheme
import com.example.android.wearable.composeforwearos.charts.DraggableLineChartHelper
import com.example.android.wearable.composeforwearos.charts.GenSampleDataHelper
import com.example.android.wearable.composeforwearos.charts.TimeScoreData
import com.example.android.wearable.composeforwearos.charts.TimeConvertionUtil
import com.example.android.wearable.composeforwearos.widgets.SimpleIconButton
import com.github.mikephil.charting.charts.LineChart
import com.google.android.horologist.compose.focus.rememberActiveFocusRequester
import com.google.android.horologist.compose.rotaryinput.rotaryWithFling
import lmu.pms.stila.ui.wear.widget.unswipeable

import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
@Composable
fun GraphsScreen(
    modifier: Modifier = Modifier,
    verticalScrollState: ScalingLazyListState,
    swipeToDismissBoxState: SwipeToDismissBoxState,
    dataMap: Map<Long, TimeScoreData>,
    onNavBack: () -> Unit,
) {
    val focusRequester = rememberActiveFocusRequester()
    val localContext = LocalContext.current

    val whiteColorInt = ContextCompat.getColor(localContext, R.color.white)
    val fillGradientDrawable = ContextCompat.getDrawable(localContext, R.drawable.gradient)

    ScalingLazyColumn(
        modifier = modifier.rotaryWithFling(focusRequester, verticalScrollState),
        contentPadding = PaddingValues(horizontal = 6.dp), // to make less padding
        state = verticalScrollState,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Text("Unswipeable")
        }
        item {
            HorizontalDraggableLineChart(
                androidViewModifier = Modifier.unswipeable(),
                chartColorInt = whiteColorInt,
                dataMap = dataMap,
                fillGradientDrawable = fillGradientDrawable!!
            )
        }
        item {
            Text("EdgeSwipeToDismiss")
        }
        item {
            HorizontalDraggableLineChart(
                androidViewModifier = Modifier.edgeSwipeToDismiss(swipeToDismissBoxState, 0.dp),
                chartColorInt = whiteColorInt,
                dataMap = dataMap,
                fillGradientDrawable = fillGradientDrawable!!
            )
        }

        item {
            Row (modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)) {
                SimpleIconButton(
                    imageVector = Icons.Rounded.Close,
                    onClick = onNavBack
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        // added launchedEffect to request focus so that the bezel event can be detected.
        focusRequester.requestFocus()
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun HorizontalDraggableLineChart(
    androidViewModifier: Modifier = Modifier,
    chartColorInt: Int,
    dataMap: Map<Long, TimeScoreData>?,
    fillGradientDrawable: Drawable,
    timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm")
) {
    /**
     * Make AndroidView unswipeable so that the
     * swipe back gesture is deactivated for the SwipeDismissBox inside SwipeNavHost
     * The slow drag gesture is still possible.
     */
    Box(
        modifier = Modifier
            .height(140.dp)
    ) {
        AndroidView(
            modifier = androidViewModifier
                .align(Alignment.TopStart)
                .height(110.dp)
                .fillMaxWidth()
                .padding(start = 6.dp, top = 0.dp, bottom = 0.dp, end = 0.dp),

            /* init view code */
            factory = { ctx ->
                // initialize a lineChart view and use apply to setup
                LineChart(ctx).apply {
                    DraggableLineChartHelper.onInit(
                        lineChart = this,
                        yAxisColorInt = chartColorInt,
                        xLabelColorInt = chartColorInt,
                        indicatorColorInt = chartColorInt
                    )
                }
            },
            /* callback after the layout is inflated, it will run after the factory block
             * it will also be called if the LazyColumn is scrolled up
             */
            update = { lineChart ->
                // lineChart reload
                DraggableLineChartHelper.onUpdate(
                    lineChart = lineChart,
                    timeFormat = timeFormat,
                    dataMap = dataMap,
                    beginTimestamp = TimeConvertionUtil.getCurrentDayUTCtimestampInSecs(),
                    lineCurveColorInt = chartColorInt,
                    dataCircleColorInt = chartColorInt,
                    fillGradientDrawable = fillGradientDrawable,
                    redrawAtOnce = false // not calling redraw, since the update will invalidate view automatically
                )
            }
        )
    } // end of box
}


/* preview section */
@Preview(
    device = Devices.WEAR_OS_SMALL_ROUND,
    showSystemUi = true,
    showBackground = true
)
@Preview(
    device = Devices.WEAR_OS_SQUARE, // small square
    showSystemUi = true,
    showBackground = true
)
@Composable
fun GraphsScreenPreview() {
    WearAppTheme {
        val scalingLazyListState = rememberScalingLazyListState()
        val swipeToDismissBoxState = rememberSwipeToDismissBoxState()
        GraphsScreen(
            verticalScrollState = scalingLazyListState,
            swipeToDismissBoxState = swipeToDismissBoxState,
            dataMap = GenSampleDataHelper.genChartTimeScoreMap(),
            onNavBack = {}
        )
    }
}
