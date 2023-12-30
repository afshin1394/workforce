package com.irancell.nwg.wfm.presentation.screens.main.events


sealed interface SettingEvent : Event{
    object Default : SettingEvent
    object ChangeLanguage : SettingEvent
}