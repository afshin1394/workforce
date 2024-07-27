package presentation.screens.main.events

sealed interface TicketProcessEvent :Event {

    data object Default : TicketProcessEvent
    data object PhotoPreview : TicketProcessEvent
    data object DeletePhoto : TicketProcessEvent
    data object EditPhoto : TicketProcessEvent

}