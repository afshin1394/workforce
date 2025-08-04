package presentation.screens.splash.events


sealed interface CheckVersionEvent {

    data object Default : CheckVersionEvent
    data object ForceUpdate : CheckVersionEvent
    data object NormalUpdate : CheckVersionEvent
    data object InvalidToken : CheckVersionEvent
    data object OkVersion : CheckVersionEvent
    data object NavigateToDownload : CheckVersionEvent
    data object NavigateToMain : CheckVersionEvent
}