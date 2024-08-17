package utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.*
fun getCurrentDate() : String {
   return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
}


fun getLocalDateTimeFromLong(long: Long): LocalDateTime {
    return Instant.fromEpochMilliseconds(long).toLocalDateTime(TimeZone.currentSystemDefault())
}

fun  String.parsGpsDateTime() : String{
    val inputDateTime = parseDateTime(this)
    return formatDateTime(inputDateTime)
}


fun parseDateTime(input: String): LocalDateTime {
    val monthMap = mapOf(
        "Jan" to Month.JANUARY, "Feb" to Month.FEBRUARY, "Mar" to Month.MARCH,
        "Apr" to Month.APRIL, "May" to Month.MAY, "Jun" to Month.JUNE,
        "Jul" to Month.JULY, "Aug" to Month.AUGUST, "Sep" to Month.SEPTEMBER,
        "Oct" to Month.OCTOBER, "Nov" to Month.NOVEMBER, "Dec" to Month.DECEMBER
    )

    val parts = input.split(" ", ", ", ":", " ")
    val month = monthMap[parts[0]] ?: throw IllegalArgumentException("Invalid month")
    val day = parts[1].toInt()
    val year = parts[2].toInt()
    val hour = parts[3].toInt()
    val minute = parts[4].toInt()
    val second = parts[5].toInt()

    return LocalDateTime(year, month, day, hour, minute, second)
}

fun formatDateTime(dateTime: LocalDateTime): String {
    val year = dateTime.year
    val month = dateTime.monthNumber.toString().padStart(2, '0')
    val day = dateTime.dayOfMonth.toString().padStart(2, '0')
    val hour = dateTime.hour % 12
    val formattedHour = if (hour == 0) 12 else hour
    val formattedHourString = formattedHour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')
    val second = dateTime.second.toString().padStart(2, '0')
    val amPm = if (dateTime.hour < 12) "AM" else "PM"

    return "$year-$month-$day  $formattedHourString:$minute $amPm"
}