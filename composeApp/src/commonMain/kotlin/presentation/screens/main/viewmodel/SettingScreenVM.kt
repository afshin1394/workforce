package com.irancell.nwg.wfm.presentation.screens.main.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.irancell.nwg.wfm.presentation.model.SelectableItem
import com.irancell.nwg.wfm.presentation.model.SelectableItemStringResource

import presentation.screens.main.events.SettingEvent
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.MR
import utils.BaseViewModel

class SettingScreenVM : BaseViewModel() {

    var events = mutableStateOf<SettingEvent>(SettingEvent.Default)


    val mutableChangeLanguageOptions =  mutableStateListOf(
        SelectableItemStringResource(1, MR.strings.english, false),
        SelectableItemStringResource(2, MR.strings.farsi, false),
    )


    val mutableChangeModeOptions =  mutableStateListOf(
        SelectableItemStringResource(1, MR.strings.online, false),
        SelectableItemStringResource(2, MR.strings.offline, false),
    )
}