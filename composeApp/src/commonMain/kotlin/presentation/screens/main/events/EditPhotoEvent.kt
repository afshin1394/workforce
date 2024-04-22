package presentation.screens.main.events

interface EditPhotoEvent {

    data object Default : EditPhotoEvent
    data object Discard : EditPhotoEvent
}