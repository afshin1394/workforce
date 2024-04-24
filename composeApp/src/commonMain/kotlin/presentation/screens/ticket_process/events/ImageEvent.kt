package presentation.screens.ticket_process.events

import presentation.screens.main.events.MainEvent

sealed interface ImageEvent {
    data object Default : ImageEvent
    data object PhotoPreview: ImageEvent
    data object DeletePhoto: ImageEvent
    data object EditPhoto: ImageEvent
    data object OpenCamera : ImageEvent
}