package irancell.nwg.wfm

import kotlinx.datetime.LocalDateTime

expect object DatePickerFormat {
    fun LocalDateTime.format(format: String): String
    fun getDateTime(string: String, format: String): LocalDateTime
}