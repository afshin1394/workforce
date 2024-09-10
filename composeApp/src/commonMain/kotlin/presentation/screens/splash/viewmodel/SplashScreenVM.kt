package presentation.screens.splash.viewmodel

import androidx.compose.runtime.mutableStateOf
import data.network.request.version.VersionRequest
import domain.usecase.usecase.version.GetVersionOfServerUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import io.ktor.http.HttpStatusCode
import irancell.nwg.wfm.DeviceInfo
import irancell.nwg.wfm.InternalStorage
import irancell.nwg.wfm.LifecycleEvent
import irancell.nwg.wfm.provideAppContext

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import presentation.screens.main.events.MainEvent
import presentation.screens.splash.events.CheckVersionEvent
import presentation.screens.splash.events.PermissionEvent
import utils.AsyncStatus
import utils.BaseViewModel
import utils.ViewStates
import androidx.compose.runtime.State
import domain.models.version.GetVersionDomain
import irancell.nwg.wfm.InstallApk
import irancell.nwg.wfm.PerformDownload
import irancell.nwg.wfm.Unzip
import irancell.nwg.wfm.getSharedPref

import utils.FileApk


class SplashScreenVM(
    private val getVersionOfServerUseCase: GetVersionOfServerUseCase

) : BaseViewModel() {

    private val _permissionState =
        MutableStateFlow<PermissionEvent>(PermissionEvent.RequestPermission)
    val permissionState = _permissionState.asStateFlow()



    private val _lifeCycleEvent = MutableStateFlow(LifecycleEvent.ON_ANY)
    val lifeCycleEvent = _lifeCycleEvent.asStateFlow()

    var eventsVersion = mutableStateOf<CheckVersionEvent>(CheckVersionEvent.Default)



    private val _versionData = mutableStateOf<GetVersionDomain?>(null)
    val versionData: State<GetVersionDomain?> = _versionData


    init {
        checkVersionOfServer()
        InternalStorage.initWFMImages(provideAppContext())
        InternalStorage.initProcessImages(provideAppContext())
        InternalStorage.initSuspendImages(provideAppContext())


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


    fun updateLifeCycleEventState(event: LifecycleEvent) {
        _lifeCycleEvent.update { event }
    }

    fun updatePermissionState(permissionEvent: PermissionEvent) {
        _permissionState.update { permissionEvent }
    }


    fun changeStateDenied() {
        _permissionState.update { PermissionEvent.CheckPermission }
    }


}


