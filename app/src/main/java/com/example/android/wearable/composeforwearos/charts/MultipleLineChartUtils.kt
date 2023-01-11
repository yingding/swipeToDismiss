@file:JvmName("MultipleLineChartUtils")
package com.example.android.wearable.composeforwearos.charts

import com.github.mikephil.charting.data.Entry
import java.text.SimpleDateFormat

fun needSwitchToNewSeg2(current: TimeScoreData?, last: TimeScoreData?): Boolean {
    return current == null && last != null || current != null && last == null
}

fun loadDiscontinuousLineSegments2(
    dayBeginTS: Long,
    cStressMap: Map<Long, TimeScoreData>,
    localizedTimeFormat: SimpleDateFormat? = null
): DiscontinuousLinesContainer {

    val HR_INTERVAL_LENGTH = 600

    val dayEndTS: Long = dayBeginTS + 86400 // 24 hours later

    // the list collection of all discontinuous lines
    val lines = ArrayList<DiscontinuousLineSegment>()
    // use a single global label array for all discontinuous lines
    val labels = ArrayList<String>()

    var firstIndexWithData = -1
    var lastIndexWithData = -1

    /*
     * Preparing the data and detect how many discontinuous segments exist in the given line
     * every discontinuous part is initialized as a custom DiscontinuousLineSegment obj.
     */

    // need segId to assign the new seg
    var segId = -1
    var current: TimeScoreData?
    var last: TimeScoreData? = null
    var lineSegment: DiscontinuousLineSegment
    // moving reference pointer of the discontinuous line entry list, a line consists of entries
    var lineSegmentEntryList: ArrayList<Entry>? = null

    /* use index and value in range
     * https://stackoverflow.com/questions/48898102/how-to-get-the-current-index-in-for-each-kotlin/55297324#55297324
     *
     * Loop through the whole day every 10 min timestamp
     */
    for ((index, curTS: Long) in (dayBeginTS..dayEndTS step HR_INTERVAL_LENGTH.toLong()).withIndex()) {

        // add label to global label list, which must be large then the segment
        labels.add(TimeConvertionUtil.utcTimestampToLongDaytimeStr(curTS, localizedTimeFormat)!!)

        // get the current computed stress associated with the current timestamp
        current = cStressMap[curTS]

        // for the first timestamp of the day or (one of the current and last is null)
        // a new discontinuous segment shall be initialized
        if (curTS == dayBeginTS || needSwitchToNewSeg2(current, last)) {
            segId++
            lines.add(DiscontinuousLineSegment()) // add a Pair to the discontinuous line
            lineSegment = lines[segId]
            lineSegmentEntryList = lineSegment.valueSegment

            if (current == null && !lineSegment.isAllZero) {
                // set the indicator for all zero line segment
                lineSegment.isAllZero = true
            }
        }
        // do not use the else statement to null the entries since isNewSeg is false case still need to add to entries

        if (current != null) {
            // convert the double value (computedStress) to Float()
            lineSegmentEntryList?.add(Entry(current.score.toFloat(), index))

            if (firstIndexWithData == -1) {
                firstIndexWithData = index
            }
            lastIndexWithData = index
        } else {
            // Adding zero value to the segment
            lineSegmentEntryList?.add(Entry(-0.0f, index))
        }
        last = current
    }

    return DiscontinuousLinesContainer(labels, lines, firstIndexWithData, lastIndexWithData)
}

/**
 * https://stackoverflow.com/questions/47307782/how-to-return-multiple-values-from-a-function-in-kotlin-like-we-do-in-swift/47308092#47308092
 */
data class DiscontinuousLinesContainer(
    val labels: ArrayList<String>,
    val lines: ArrayList<DiscontinuousLineSegment>,
    val firstIdx: Int,
    val lastIdx: Int)

class DiscontinuousLineSegment {
    var isAllZero = false
    val valueSegment = java.util.ArrayList<Entry>()

    fun isEmpty(): Boolean {
        return valueSegment.isEmpty()
    }
}