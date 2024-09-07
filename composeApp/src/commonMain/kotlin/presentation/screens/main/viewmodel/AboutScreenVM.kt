package presentation.screens.main.viewmodel

import androidx.compose.runtime.State
import dev.icerock.moko.mvvm.viewmodel.ViewModel


import androidx.compose.runtime.mutableStateOf
import domain.models.version.GetVersionDomain
import domain.usecase.usecase.profile.StoreProfileUseCase
import domain.usecase.usecase.version.GetVersionOfServerUseCase
import domain.usecase.usecase.version.SendVersionToServerUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.DeviceInfo
import irancell.nwg.wfm.getPlatform
import irancell.nwg.wfm.getSharedPref
import kotlinx.coroutines.launch
import presentation.screens.splash.events.CheckVersionEvent
import utils.AsyncStatus
import utils.BaseViewModel
import utils.FileApk

class AboutScreenVM(
    private val getVersionOfServerUseCase: GetVersionOfServerUseCase
) : BaseViewModel() {



    var eventsVersion = mutableStateOf<CheckVersionEvent>(CheckVersionEvent.Default)

    private val _versionData = mutableStateOf<GetVersionDomain?>(null)
    val versionData: State<GetVersionDomain?> = _versionData

    init {
        checkVersionOfServer()
    }




    private fun checkVersionOfServer() {
        viewModelScope.launch {
            getVersionOfServerUseCase(Unit).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        eventsVersion.value = CheckVersionEvent.InvalidToken


                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "get version of Server",
                            message = "ERROR" + it.message
                        )
                    }

                    AsyncStatus.LOADING -> {
                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "get version of Server",
                            message = "LOADING"
                        )
                    }

                    AsyncStatus.EMPTY -> {

                    }

                    AsyncStatus.SUCCESS -> {

                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "get version of Server",
                            message = "SUCCESS${it.data}"
                        )
                        it. data?.let { versionData ->
                            _versionData.value = versionData
                            when {
                                versionData.force_update && versionData.version_code > DeviceInfo.getAppVersionCode().toDouble() -> {
                                    eventsVersion.value = CheckVersionEvent.ForceUpdate
                                }
                                versionData.version_code > DeviceInfo.getAppVersionCode().toDouble() -> {
                                    eventsVersion.value = CheckVersionEvent.NormalUpdate
                                }
                                else -> {
                                    getSharedPref().put(FileApk,"")
                                    eventsVersion.value = CheckVersionEvent.OkVersion
                                }
                            }
                        } ?: run {
                            getSharedPref().put(FileApk,"")
                            eventsVersion.value = CheckVersionEvent.OkVersion
                        }
                    }
                }
            }

        }
    }


}