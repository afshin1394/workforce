package presentation.screens.main.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel


import androidx.compose.runtime.mutableStateOf
import irancell.nwg.wfm.getPlatform
import utils.BaseViewModel

class AboutScreenVM : BaseViewModel() {
    private val platform = getPlatform()
    val currentVersion = mutableStateOf("1.0.0")
    val updateVersion = mutableStateOf("1.0.1")
}