package presentation.screens.main.events


sealed interface MainEvent : Event {
   data object Default : MainEvent
   data object Logout : MainEvent
   data object ActionFilter : MainEvent
   data object AvailabilityStatus : MainEvent
   data object MoreOptions : MainEvent
   data object SuspendTicket : MainEvent
   data object SuspendReason : MainEvent
   data object CancelTicket : MainEvent
   data object CancelReason : MainEvent
   data object AcceptTicket : MainEvent
   data object Exit : MainEvent
   data object PhotoPreview:MainEvent
   data object DeletePhoto:MainEvent
   data object EditPhoto:MainEvent
   data object DiscardSuspendTicket:MainEvent
}

