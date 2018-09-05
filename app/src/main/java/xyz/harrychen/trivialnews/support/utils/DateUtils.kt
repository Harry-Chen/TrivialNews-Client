package xyz.harrychen.trivialnews.support.utils

import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import java.util.Date

class DateUtils{
    companion object {

        private val ISO8601Parser: DateTimeFormatter by lazy {
            ISODateTimeFormat.dateTimeParser()
        }

        private val readableDateTimeFormatter by lazy {
            DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss")
        }

        fun fromISO8601String(str: String): Date {
            return ISO8601Parser.parseDateTime(str).toLocalDateTime().toDate()
        }

        fun toISO8601String(date: Date): String {
            return LocalDateTime(date).toString()
        }

        fun toReadableString(date: Date): String {
            return readableDateTimeFormatter.print(LocalDateTime(date))
        }
    }
}


fun Date.toISO8601String(): String {
    return DateUtils.toISO8601String(this)
}

fun Date.toReadableDateTimeString(): String {
    return DateUtils.toReadableString(this)
}