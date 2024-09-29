package presentation.screens.main.events

sealed interface AboutEvent:Event {
    data object Default : AboutEvent
    data object ForceUpdate : AboutEvent
    data object NormUpdate : AboutEvent

}