package com.irancell.nwg.wfm.presentation.screens.main.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.irancell.nwg.wfm.presentation.model.SelectableItem

import presentation.screens.main.events.SettingEvent
import dev.icerock.moko.mvvm.viewmodel.ViewModel

class SettingScreenVM : ViewModel() {

    var events = mutableStateOf<SettingEvent>(SettingEvent.Default)


    val mutableChangeLanguageOptions =  mutableStateListOf(
        SelectableItem(1, "English", false),
        SelectableItem(2, "Farsi", false),
    )
}