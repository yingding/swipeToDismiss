package com.example.android.wearable.composeforwearos.charts

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object TimeConvertionUtil {
    fun getCurrentDayUTCtimestampInSecs(): Long {
        return getDayBeginTimestampInSecsOf(System.currentTimeMillis() / 1000L)
    }

    fun getDayBeginTimestampInSecsOf(timestampUTCInSec: Long): Long {
        return dateLongStrToUTCtimeStamp(
            utcTimestampToNormalDateStr(timestampUTCInSec) + " " + "00:00:00"
        )
    }

    @SuppressLint("SimpleDateFormat")
    fun utcTimestampToLongDaytimeStr(
        timestampInSecs: Long?,
        vararg dateFormats: DateFormat?
    ): String? {
        val dateFormat: SimpleDateFormat?
        dateFormat = if (dateFormats.size != 0 && dateFormats[0] != null) {
            dateFormats[0] as SimpleDateFormat?
        } else {
            SimpleDateFormat("HH:mm")
        }
        return utcTimestampToDateStr(
            timestampInSecs,
            dateFormat!!
        )
    }

    @SuppressLint("SimpleDateFormat")
    fun utcTimestampToNormalDateStr(timestampInSecs: Long): String? {
        return utcTimestampToDateStr(
            timestampInSecs,
            SimpleDateFormat("yyyy-MM-dd")
        )
    }

    private fun utcTimestampToDateStr(
        timestampInSec: Long?,
        dateFormatNoTZ: SimpleDateFormat
    ): String? {
        return utcTimestamp2LocalDatetimeStrHelper(
            timestampInSec, getDateFormatWithLocalTZ(dateFormatNoTZ)
        )
    }

    private fun utcTimestamp2LocalDatetimeStrHelper(
        utcInSec: Long?,
        sdfWithTZ: SimpleDateFormat
    ): String? {
        return if (utcInSec != null) {
            sdfWithTZ.format(Date(utcInSec * 1000))
        } else {
            null
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun dateLongStrToUTCtimeStamp(timeStr: String): Long {
        return dateStrToUTCtimeStamp(
            timeStr,
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        )
    }

    private fun dateStrToUTCtimeStamp(timeStr: String, dateFormat: SimpleDateFormat): Long {
        var dateWithTZ: Date? = null
        var timeStampInSec: Long = 0
        // date is with local timezone info
        dateWithTZ = timeStrToDate(timeStr, dateFormat)
        if (dateWithTZ != null) {
            /*
         * DateWithTZ.getTime() returns in Android in milliseconds
         * Since the timezone info is already added to Date object, the returned time is the UCT time (GMT + 0)
         * Division of 1000 to get seconds
         */
            timeStampInSec = dateWithTZ.time / 1000
        }
        return timeStampInSec
    }

    fun timeStrToDate(timeStr: String?, dateFormatNoTZ: SimpleDateFormat): Date? {
        var dateWithTZ: Date? = null
        if (timeStr != null) {
            try {
                // make short time to a long time format
                // parse with the local time zone info.
                dateWithTZ = getDateFormatWithLocalTZ(dateFormatNoTZ)
                    .parse(timeStr)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        return dateWithTZ
    }

    fun getDateFormatWithLocalTZ(sdfNoTZ: SimpleDateFormat): SimpleDateFormat {
        val sdf = sdfNoTZ.clone() as SimpleDateFormat
        sdf.timeZone = Calendar.getInstance().timeZone
        return sdf
    }
}