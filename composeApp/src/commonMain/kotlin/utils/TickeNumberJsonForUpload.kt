package utils

fun createJsonWithTicketNumber(ticketnumber: String): String {
    val extraInfo = """{"ticket_num":"$ticketnumber"}"""
    return extraInfo
}