package presentation.screens.main.events

sealed interface TicketInfoEvent :Event {

    data object Default : TicketInfoEvent
    data object PhotoPreview:TicketInfoEvent
    data object DeletePhoto:TicketInfoEvent
    data object EditPhoto:TicketInfoEvent





}