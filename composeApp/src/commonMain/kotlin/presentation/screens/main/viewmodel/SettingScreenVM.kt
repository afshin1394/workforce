package com.irancell.nwg.wfm.presentation.screens.main.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.irancell.nwg.wfm.presentation.model.SelectableItem
import com.irancell.nwg.wfm.presentation.model.SelectableItemStringResource

import presentation.screens.main.events.SettingEvent
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.getSharedPref
import utils.BaseViewModel
import utils.Language
import utils.ModeApp
import utils.UpdateTaskListTypes
import utils.UpdateType

class SettingScreenVM : BaseViewModel() {

    var events = mutableStateOf<SettingEvent>(SettingEvent.Default)


    val mutableChangeLanguageOptions =  mutableStateListOf(
        SelectableItemStringResource(1, MR.strings.english, if (getSharedPref().getString(Language) == "fa") false else true ,  "en"),
        SelectableItemStringResource(2, MR.strings.farsi,  if (getSharedPref().getString(Language) == "en") false else true,"fa"),
    )


    val mutableChangeModeOptions =  mutableStateListOf(
        SelectableItemStringResource(1, MR.strings.online, if (getSharedPref().getString(ModeApp) == "on") false else true,"on"),
        SelectableItemStringResource(2, MR.strings.offline, if (getSharedPref().getString(ModeApp) == "of") false else true,"of"),
    )

    val mutableChangeUpdateTypeOptions =  mutableStateListOf(
        SelectableItemStringResource(1, MR.strings.auto, if (getSharedPref().getString(UpdateType) == UpdateTaskListTypes.Auto) false else true , UpdateTaskListTypes.Auto ),
        SelectableItemStringResource(2, MR.strings.manual,  if (getSharedPref().getString(UpdateType) ==  UpdateTaskListTypes.Manual) false else true,UpdateTaskListTypes.Manual),
    )
}