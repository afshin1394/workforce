package presentation.screens.ticket_process.events

sealed interface StepEvent {
    data object INITIAL : StepEvent
    data object START : StepEvent
    data object END : StepEvent
    data object IN_PROCESS : StepEvent
}