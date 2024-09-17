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
import domain.usecase.usecase.ipDetection.IpDetectionUseCase
import domain.usecase.usecase.profile.StoreProfileUseCase
import irancell.nwg.wfm.getSharedPref
import kotlinx.coroutines.flow.StateFlow

import utils.FileApk

class SplashScreenVM(
    private val getVersionOfServerUseCase: GetVersionOfServerUseCase,
    private val storeProfileUseCase: StoreProfileUseCase,
    private val ipDetectionUseCase: IpDetectionUseCase,
) : BaseViewModel() {

    private val _permissionState =
        MutableStateFlow<PermissionEvent>(PermissionEvent.RequestPermission)
    val permissionState = _permissionState.asStateFlow()


    private val _lifeCycleEvent = MutableStateFlow(LifecycleEvent.ON_ANY)
    val lifeCycleEvent = _lifeCycleEvent.asStateFlow()

    var eventsVersion = mutableStateOf<CheckVersionEvent>(CheckVersionEvent.Default)


    private val _versionData = mutableStateOf<GetVersionDomain?>(null)
    val versionData: State<GetVersionDomain?> = _versionData

    private val _showVpnBottomSheet = MutableStateFlow(false)
    val showVpnBottomSheet: StateFlow<Boolean> = _showVpnBottomSheet.asStateFlow()


    init {

        InternalStorage.initWFMImages(provideAppContext())
        InternalStorage.initProcessImages(provideAppContext())
        InternalStorage.initSuspendImages(provideAppContext())
    }

    fun restrictForeignIp() {
        viewModelScope.launch {
            ipDetectionUseCase(Unit).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        checkVersionOfServer()
                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "get country",
                            message = "ERROR" + it.message
                        )
                    }

                    AsyncStatus.LOADING -> {
                        Napier.log(LogLevel.ASSERT, tag = "get country", message = "LOADING")
                    }

                    AsyncStatus.EMPTY -> {}
                    AsyncStatus.SUCCESS -> {
                        if (it.data.toString() != "IR") {
                            _showVpnBottomSheet.update { true }
                        } else {
                            checkVersionOfServer()
                        }
                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "get country",
                            message = it.data.toString()
                        )
                    }
                }
            }
        }
    }

    fun updateBottomSheetState(show: Boolean) {
        _showVpnBottomSheet.update { show }
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
                        getProfile()
                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "get version of Server",
                            message = "SUCCESS${it.data}"
                        )
                        it.data?.let { versionData ->
                            _versionData.value = versionData
                            when {
                                versionData.force_update && versionData.version_code.toDouble() > DeviceInfo.getAppVersionCode()
                                    .toDouble() -> {
                                    eventsVersion.value = CheckVersionEvent.ForceUpdate
                                }

                                versionData.version_code.toDouble() > DeviceInfo.getAppVersionCode()
                                    .toDouble() -> {
                                    eventsVersion.value = CheckVersionEvent.NormalUpdate
                                }

                                else -> {
                                    getSharedPref().put(FileApk, "")
                                    eventsVersion.value = CheckVersionEvent.OkVersion
                                }
                            }
                        } ?: run {
                            getSharedPref().put(FileApk, "")
                            eventsVersion.value = CheckVersionEvent.OkVersion
                        }
                    }
                }
            }

        }
    }

    private fun getProfile() {
        viewModelScope.launch {
            storeProfileUseCase(Unit).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)
                        Napier.log(LogLevel.ASSERT, tag = "getProfile", message = "ERROR")

                    }

                    AsyncStatus.LOADING -> {

                        Napier.log(LogLevel.ASSERT, tag = "getProfile", message = "LOADING")

                    }

                    AsyncStatus.EMPTY -> {

                    }

                    AsyncStatus.SUCCESS -> {
                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "getProfile",
                            message = "SUCCESS${it.data}"
                        )

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


