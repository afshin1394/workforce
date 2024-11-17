package utils

import irancell.nwg.wfm.MR
import irancell.nwg.wfm.getSharedPref
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.*
fun getCurrentDate() : String {
   return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
}
fun getCurrentDateLocalDateTime() : LocalDateTime {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}

fun getLocalDateTimeFromLong(long: Long): LocalDateTime {
    return Instant.fromEpochMilliseconds(long).toLocalDateTime(TimeZone.currentSystemDefault())
}

fun  String.parsGpsDateTime() : String {
    val inputDateTime = parseDateTime(this)
    return formatDateTime(inputDateTime)
}

fun String.parsServerDateTime() : String {
    val inputDateTime = parseServerDateTime(this)
    return formatDateTime(inputDateTime)
}
fun LocalDateTime.localDateTimeToMilliseconds(): Long {
    // Convert LocalDateTime to milliseconds since the epoch
    return this.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
}


fun compareLocalDateTimes(dateTime1: LocalDateTime, dateTime2: LocalDateTime): Int {
    // Negative if dateTime1 < dateTime2, positive if >, 0 if equal
    return dateTime1.compareTo(dateTime2)
}


fun String.parseLocalDateTime(): LocalDateTime? {
    // Trim the input string and split into date and time parts
    val parts = this.trim().split(" ")
    if (parts.size < 3) return null // Invalid format

    val datePart = parts[0] // "2024-09-18"
    val timePart = parts[1] + " " + parts[2] // "12:19 PM"

    // Split date into year, month, and day
    val dateComponents = datePart.split("-")
    if (dateComponents.size != 3) return null

    val year = dateComponents[0].toIntOrNull() ?: return null
    val month = dateComponents[1].toIntOrNull() ?: return null
    val day = dateComponents[2].toIntOrNull() ?: return null

    // Split time into hour and minute and handle AM/PM
    val timeComponents = timePart.split(":")
    if (timeComponents.size != 2) return null

    val hour = timeComponents[0].trim().toIntOrNull() ?: return null
    val minute = timeComponents[1].trim().substring(0, 2).toIntOrNull() ?: return null
    val amPm = parts[3].trim() // "AM" or "PM"

    // Convert to 24-hour format
    val adjustedHour = when (amPm) {
        "AM" -> if (hour == 12) 0 else hour
        "PM" -> if (hour == 12) hour else hour + 12
        else -> return null // Invalid AM/PM format
    }

    return LocalDateTime(year, month, day, adjustedHour, minute)
}


fun parseServerDateTime(input : String) : LocalDateTime {
    val monthMap = mapOf(
        "01" to Month.JANUARY, "02" to Month.FEBRUARY, "03" to Month.MARCH,
        "04" to Month.APRIL, "05" to Month.MAY, "06" to Month.JUNE,
        "07" to Month.JULY, "08" to Month.AUGUST, "09" to Month.SEPTEMBER,
        "10" to Month.OCTOBER, "11" to Month.NOVEMBER, "12" to Month.DECEMBER
    )

    val parts = input.split("-"," ", ":")
    val year = parts[0].toInt()
    val month = monthMap[parts[1]] ?: throw IllegalArgumentException("Invalid month")
    val day = parts[2].toInt()
    val hour = parts[3].toInt()
    val minute = parts[4].toInt()
    val second = parts[5].toInt()

    return LocalDateTime(year, month, day, hour, minute, second)

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


fun Long.convertMillisToTime(): String {
    val days = this / (24 * 60 * 60 * 1000)
    val hours = (this / (60 * 60 * 1000)) % 24
    val minutes = (this / (60 * 1000)) % 60
    val isFarsi  = getSharedPref().getString(Language) == "fa"
    // Helper function to convert digits to Farsi numerals if needed


    // Define text labels based on language
    val dayLabel = if (isFarsi) "روز" else "day"
    val hourLabel = if (isFarsi) "ساعت" else "hour"
    val minuteLabel = if (isFarsi) "دقیقه" else "minute"

    // Collect non-zero parts in order: days, hours, minutes
    val parts = mutableListOf<String>()
    if (days > 0) parts.add("${formatNumber(days)} $dayLabel${if (days > 1 && !isFarsi) "s" else ""}")
    if (hours > 0) parts.add("${formatNumber(hours)} $hourLabel${if (hours > 1 && !isFarsi) "s" else ""}")
    if (minutes > 0) parts.add("${formatNumber(minutes)} $minuteLabel${if (minutes > 1 && !isFarsi) "s" else ""}")

    // Join parts in correct order, defaulting to "0 minutes" or "۰ دقیقه" if all are zero
    return if (parts.isNotEmpty()) parts.joinToString(if (isFarsi) "، " else ", ")
    else "${formatNumber(0)} ${minuteLabel}"
}

fun formatNumber(value: Long): String {
    return if (getSharedPref().getString(Language) == "fa") {
        value.toString().map {
            when (it) {
                '0' -> '۰'
                '1' -> '۱'
                '2' -> '۲'
                '3' -> '۳'
                '4' -> '۴'
                '5' -> '۵'
                '6' -> '۶'
                '7' -> '۷'
                '8' -> '۸'
                '9' -> '۹'
                else -> it
            }
        }.joinToString("")
    } else {
        value.toString()
    }
}