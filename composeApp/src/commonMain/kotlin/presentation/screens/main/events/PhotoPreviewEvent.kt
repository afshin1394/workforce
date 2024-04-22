package presentation.screens.main.events

interface PhotoPreviewEvent {
    data object Default : PhotoPreviewEvent
    data object DeletePhoto : PhotoPreviewEvent
    data object RotatePhoto : PhotoPreviewEvent
    data object ShowPhoto : PhotoPreviewEvent

    data object Discard : PhotoPreviewEvent

}