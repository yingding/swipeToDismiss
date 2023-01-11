package com.example.android.wearable.composeforwearos.charts

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.MotionEvent
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

object GenSampleDataHelper {
    private val dawn = TimeConvertionUtil.getCurrentDayUTCtimestampInSecs()
    private val interval = TimeUnit.MINUTES.toSeconds(10) // 10 min interval per Stress Score
    private val hour14 = dawn + TimeUnit.HOURS.toSeconds(14)
    private val hour15 = dawn + TimeUnit.HOURS.toSeconds(15)

    fun genChartTimeScoreMap(): Map<Long, TimeScoreData> {
        val stressScoreList14 = arrayListOf(80.0, 50.0)
        val stressScoreList15 = arrayListOf(60.0, 70.0, 80.0, 90.0, 90.0, 110.0, 90.0, 80.0, 60.0, 75.0, 60.0)
        val myMap = mutableMapOf<Long, TimeScoreData>()

        for (i in 0 until stressScoreList14.size) {
            myMap[hour14 + i * interval] = TimeScoreData(hour14, stressScoreList14[i])
        }

        for (i in 0 until stressScoreList15.size) {
            myMap[hour15 + i * interval] = TimeScoreData(hour15, stressScoreList15[i])
        }
        return myMap
    }
}

class TimeScoreData(
    var timestamp: Long = 0,
    var score: Double = 0.0,
)

object DraggableLineChartHelper {
    private const val TAG = "HomeActivity"
    /**
     * initialize the layout of the lineChart view
     */
    fun onInit(
        lineChart: LineChart,
        yAxisColorInt: Int,
        xLabelColorInt: Int,
        indicatorColorInt: Int,
    ) {
        onGraphInit(lineChart, yAxisColorInt, xLabelColorInt)
        drawIndicationLine(
            lineChart = lineChart,
            indicatorValue = 80.0f,
            colorInt = indicatorColorInt,
            redrawAtOnce = false
        )
    }

    private fun onGraphInit(lineChart: LineChart, yAxisColorInt: Int, xLabelColorInt: Int) {
        /*
         * deactivate all the chart gesture to make the drag better
         * https://stackoverflow.com/questions/31844471/android-dragging-in-mpandroidchart-is-unresponsive-when-contained-in-scrollvie/59769402#59769402
         */
        lineChart.onChartGestureListener = object : OnChartGestureListener {
            /* Deactivate all the gestures of chart */
            override fun onChartGestureStart(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
                // disable the InterceptTouchEvent(true)
            }
            override fun onChartGestureEnd(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
                // do not highlight any value
                lineChart.highlightValues(null)
            }
            override fun onChartLongPressed(me: MotionEvent?) = Unit
            override fun onChartDoubleTapped(me: MotionEvent?) = Unit
            override fun onChartSingleTapped(me: MotionEvent?) = Unit
            override fun onChartFling(
                me1: MotionEvent?,
                me2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ) = Unit
            override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) = Unit
            override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) = Unit
        }
        lineChart.setDescription("")

        /* set back ground layout */
        // lineChart.setDrawGridBackground(true) // false
        lineChart.setDrawGridBackground(false)
        lineChart.setTouchEnabled(true) // must set to be true to enable drag
        lineChart.isDragEnabled = true // use the compose horizontal scroll
        lineChart.isScaleXEnabled = true // enable zoom in x axis
        lineChart.isScaleYEnabled = false // disable zoom in y axis
        lineChart.isDoubleTapToZoomEnabled = false // no double tap to zoom in and out

        /*
         * 10 min interval gives a stress point,
         * 1 hour has 6 points, 24 hour is 24 x 6 = 144 point, also need 1 more to show both 00:00 and 24:00
         * 24 x 6 + 1 = 145 (original was 144 and the last show was 23:50, but not 24:00)
         */
        lineChart.setMaxVisibleValueCount(145)
        lineChart.setPinchZoom(true)

        /* The legend doc:
         * https://github.com/PhilJay/MPAndroidChart/wiki/Legend
         */
        val legend = lineChart.legend
        legend.isEnabled = false // disable legend

        /* set axis */
        val yl = lineChart.axisLeft
        // deactivated background lines, styled  like the stress indicator dashed line
        yl.enableGridDashedLine(4f, 8f, 30f)
        yl.setDrawGridLines(false) // false, GridLines are some background horizontal lines
        yl.setDrawAxisLine(true) // draw the left y axis
        // show y-labels
        yl.setDrawLabels(false) // do not draw labels of the left y axis

        /* Stress Graph Y axis max/min values */
        val stressGraphMaxYvalue = 110f
        val stressGraphMinYvalue = 25f // 35f
        yl.setAxisMaxValue(stressGraphMaxYvalue) // set y axis display max
        yl.setAxisMinValue(stressGraphMinYvalue) // set y axis display min
        yl.setDrawLimitLinesBehindData(false) // false to set the stress indicator limited line before the data
        yl.axisLineColor = yAxisColorInt // same color as label and stress indicator line
        yl.spaceTop = 30f // no top axis space // 30f
        yl.spaceBottom = 30f // 30 for the labels
        // false, since the stressGraphMinYvalues does shows zeros, the graph only shows Y from 25f to 110f
        yl.setDrawZeroLine(false) // false, the zero line is draw with the zero points in the graphic

        /* disable the multi Y-axis in line chart for multi lines */
        lineChart.axisRight.isEnabled = false // disable the right y axis

        /* set the x axis */
        val xl = lineChart.xAxis
        // set the style of xl grid line to make the y axis dashed line
        xl.enableGridDashedLine(4f, 8f, 30f)
        xl.position = XAxis.XAxisPosition.BOTTOM // XAxis is a static component of AndroidMPChart, must be imported separately
        xl.setDrawAxisLine(false)
        xl.setDrawGridLines(false)
        // val xLabelIntColor = ContextCompat.getColor(ctx, R.color.white)
        xl.textColor = xLabelColorInt
        xl.setAvoidFirstLastClipping(false) // avoid the first and last time label be clipped
        xl.textSize = 10f // make text size 10f

        xl.spaceBetweenLabels = 3 // every 6 label will be show, to show the full hour label
        /* rotate the label, for small screen */
        xl.labelRotationAngle = -90f // -60f // -90f

        // turn off the margin in line chart, if the GridBackground is true, you will see some margin, to turn it off.
        // https://github.com/PhilJay/MPAndroidChart/issues/1190
        lineChart.setViewPortOffsets(
            0f,
            0f,
            0f,
            0f /* important: do not use this bottom padding,
                       * it will also influence the box composable size, which wraps the AndroidView composable
                       */
        )
    }

    /* updated lineChart with data */
    fun onUpdate(lineChart: LineChart,
                 timeFormat: SimpleDateFormat,
                 dataMap: Map<Long, TimeScoreData>?,
                 beginTimestamp: Long,
                 lineCurveColorInt: Int,
                 dataCircleColorInt: Int,
                 fillGradientDrawable: Drawable,
                 redrawAtOnce: Boolean = false
    ) {
        drawMultipleLines(
            lineChart = lineChart,
            timeFormat = timeFormat,
            dataMap = dataMap ?: mapOf(),
            beginTimestamp = beginTimestamp,
            lineCurveColorInt = lineCurveColorInt,
            dataCircleColorInt = dataCircleColorInt,
            fillGradientDrawable = fillGradientDrawable,
            showFillGradient = true, // false,
            redrawAtOnce = false
        )
        // redraw
        if(redrawAtOnce) {
            lineChart.invalidate()
        }
    }

    private fun drawIndicationLine(
        lineChart: LineChart,
        indicatorValue: Float,
        colorInt: Int,
        redrawAtOnce: Boolean = false,
    ) {

        val yl = lineChart.axisLeft
        val limitLine = LimitLine(indicatorValue)
        limitLine.lineColor = colorInt
        limitLine.enableDashedLine(4f, 8f,0f)
        // add a line over the line chart
        yl.addLimitLine(limitLine)

        // call invalidate() to redraw.
        if (redrawAtOnce) {
            lineChart.invalidate()
        }
    }

    private fun drawMultipleLines(
        lineChart: LineChart,
        timeFormat: SimpleDateFormat,
        dataMap: Map<Long, TimeScoreData>,
        beginTimestamp: Long,
        lineCurveColorInt: Int,
        dataCircleColorInt: Int,
        fillGradientDrawable: Drawable,
        showFillGradient: Boolean = true,
        redrawAtOnce: Boolean = false,

        ) {
        val (labels, discontinuousLines, firstIndexWithData, lastIndexWithData) =
            loadDiscontinuousLineSegments2(beginTimestamp, dataMap, timeFormat)
        Log.v(TAG,
            "drawMultipleLines:\n" +
                    "the discontinuous line has ${discontinuousLines.size} parts!\n" +
                    "the labels size is ${labels.size}!"
        )

        /*
         * Setting up the Chart Layout
         * every LineSetData represents a continuous line segment
         * https://github.com/PhilJay/MPAndroidChart/issues/2128
         */
        var firstLineInit = true
        var lineData: LineData? = null

        for (seg in discontinuousLines) {
            // since empty seg are not initialized, the most seg shall be not empty
            if (!seg.isEmpty()) {
                // do not set the legend for the discontinuous LineDataSet, otherwise it will show in legend multiple times
                val dataSet = LineDataSet(seg.valueSegment, "")
                // LineDataSet dataSet = new LineDataSet(seg, dayCurveLegendText);
                dataSet.setDrawValues(false) //do not show values as text on the graph

                // Deactivate the circle of data on the graph, needed for a single line
                dataSet.setCircleColor(dataCircleColorInt)
                dataSet.circleRadius = 2.2f // 1.7f is not to strong, 0.2f large then the line stroke LineWidth
                dataSet.circleHoleRadius =  1f // 1f
                dataSet.setCircleColorHole(dataCircleColorInt) // make the hole color the same as line color, instead of white

                // 0.2f seems a better cubic interpolation intensity, this line can also be commented out
                dataSet.cubicIntensity = 0.2f
                if (seg.isAllZero) {
                    // make the stroke of zero segment line very small is segment contains only zeros
                    dataSet.lineWidth = 0.2f
                    // dataSet.setDrawCircles(false) //don not show value points on the all zero discontinuous line
                } else {
                    dataSet.lineWidth = 2.5f // extra solid stress line for wear // 1.5f line board with
                    // dataSet.setDrawCircles(false) //don not show value points on the all zero discontinuous line
                }
                // set not data circles on the graph
                // activate the circle
                dataSet.setDrawCircles(true)

                //set the line color
                dataSet.color = lineCurveColorInt

                dataSet.fillDrawable = fillGradientDrawable // fill the background with the gradient drawable
                dataSet.setDrawFilled(showFillGradient) // draw it filled

                dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER // draw the lines smoothly
                // dataSet.setDrawCubic(true); //draw the lines smoothly
                if (firstLineInit) {
                    lineData = LineData(labels, dataSet)
                    // deactivate the Init
                    firstLineInit = false
                } else {
                    lineData!!.addDataSet(dataSet)
                }
            }
        }

        // set line Data with discontinuous parts
        lineChart.data = lineData
        // set labels
        // mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

        // let the chart know it's data has changed
        lineChart.notifyDataSetChanged()
        // limit the number of visible entries
        lineChart.setVisibleXRangeMaximum(145f)
        lineChart.fitScreen() //resets previous zooming
        // lineChart.isScaleYEnabled = false
        // var offset = 0
        if (lastIndexWithData > 15) {
            val offset = 15 // offset of 15 data points/index
            lineChart.moveViewToX((lastIndexWithData - offset).toFloat())
            lineChart.zoom(4f, 0f, (lastIndexWithData - offset).toFloat(), 0f)
        } else {
            lineChart.moveViewToX(firstIndexWithData.toFloat())
            lineChart.zoom(4f, 0f, firstIndexWithData.toFloat(), 0f)
        }

        // call invalidate() to redraw.
        if (redrawAtOnce) {
            lineChart.invalidate()
        }
    }
}