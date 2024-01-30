package presentation.screens.main.viewmodel

import dev.icerock.moko.mvvm.viewmodel.ViewModel


import androidx.compose.runtime.mutableStateOf
import irancell.nwg.wfm.getPlatform

class AboutScreenVM : ViewModel() {
    private val platform = getPlatform()
    val currentVersion = mutableStateOf("1.0.0")
    val updateVersion = mutableStateOf("1.0.1")
}