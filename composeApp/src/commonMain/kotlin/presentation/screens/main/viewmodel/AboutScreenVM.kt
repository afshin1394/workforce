package presentation.screens.main.viewmodel

import Platform
import dev.icerock.moko.mvvm.viewmodel.ViewModel


import androidx.compose.runtime.mutableStateOf
import com.irancell.nwg.wfm.presentation.screens.main.events.MainEvent
import getPlatform

class AboutScreenVM : ViewModel() {
    private val platform = getPlatform()

    val currentVersion = mutableStateOf("1.0.0")
    val updateVersion = mutableStateOf("1.0.1")
}