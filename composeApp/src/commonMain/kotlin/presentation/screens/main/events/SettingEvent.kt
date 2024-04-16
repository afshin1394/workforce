package presentation.screens.main.events


sealed interface SettingEvent : Event {
   data object Default : SettingEvent
   data object ChangeLanguage : SettingEvent
   data object ChangeMode : SettingEvent
}