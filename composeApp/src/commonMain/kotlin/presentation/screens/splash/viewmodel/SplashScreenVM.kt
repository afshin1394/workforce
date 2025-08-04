package presentation.screens.splash.viewmodel

import androidx.compose.runtime.mutableStateOf
import domain.usecase.usecase.version.GetVersionOfServerUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.DeviceInfo
import irancell.nwg.wfm.InternalStorage
import irancell.nwg.wfm.LifecycleEvent
import irancell.nwg.wfm.provideAppContext

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import presentation.screens.splash.events.CheckVersionEvent
import presentation.screens.splash.events.PermissionEvent
import utils.AsyncStatus
import utils.BaseViewModel
import androidx.compose.runtime.State
import com.plusmobileapps.konnectivity.Konnectivity
import domain.models.version.GetVersionDomain
import domain.usecase.usecase.profile.StoreProfileUseCase
import domain.usecase.usecase.database.CheckDatabaseDataUseCase
import domain.usecase.usecase.database.ClearDatabaseUseCase
import irancell.nwg.wfm.getSharedPref

import utils.FileApk

class SplashScreenVM(
    private val getVersionOfServerUseCase: GetVersionOfServerUseCase,
    private val storeProfileUseCase: StoreProfileUseCase,
    private val checkDatabaseDataUseCase: CheckDatabaseDataUseCase,
    private val clearDatabaseUseCase: ClearDatabaseUseCase
) : BaseViewModel() {

    private val _permissionState =
        MutableStateFlow<PermissionEvent>(PermissionEvent.RequestPermission)
    val permissionState = _permissionState.asStateFlow()

    var eventsVersion = mutableStateOf<CheckVersionEvent>(CheckVersionEvent.Default)

    private val _versionData = mutableStateOf<GetVersionDomain?>(null)
    val versionData: State<GetVersionDomain?> = _versionData
     val konnectivity: Konnectivity = Konnectivity()
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

                    AsyncStatus.EMPTY -> {}

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
                                    checkDatabaseAndNavigate()
                                }
                            }
                        } ?: run {
                            getSharedPref().put(FileApk, "")
                            checkDatabaseAndNavigate()
                        }
                    }

                    else -> {}
                }
            }

        }
    }

    private fun getProfile() {
        viewModelScope.launch {
            storeProfileUseCase(Unit).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {

                        handleError(it.resultStatus,it.message)
                        Napier.log(LogLevel.ASSERT, tag = "getProfile", message = "ERROR")
                    }

                    AsyncStatus.LOADING -> {
                        Napier.log(LogLevel.ASSERT, tag = "getProfile", message = "LOADING")
                    }

                    AsyncStatus.EMPTY -> {}

                    AsyncStatus.SUCCESS -> {
                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "getProfile",
                            message = "SUCCESS${it.data}"
                        )

                    }

                    else -> {}
                }
            }
        }
    }

    fun updatePermissionState(permissionEvent: PermissionEvent) {
        _permissionState.update { permissionEvent }
    }


    fun changeStateDenied() {
        _permissionState.update { PermissionEvent.CheckPermission }
    }
    
    private fun checkDatabaseAndNavigate() {
        viewModelScope.launch {
            checkDatabaseDataUseCase(Unit).collect {
                when (it.status) {
                    AsyncStatus.SUCCESS -> {
                        if (it.data == true) {
                            // Database has data, navigate to main screen
                            eventsVersion.value = CheckVersionEvent.NavigateToMain
                        } else {
                            // Database is empty, navigate to download screen
                            eventsVersion.value = CheckVersionEvent.NavigateToDownload
                        }
                    }
                    AsyncStatus.ERROR -> {
                        // On error, assume database is empty and go to download
                        eventsVersion.value = CheckVersionEvent.NavigateToDownload
                    }
                    else -> {
                        // Loading or other states, do nothing for now
                    }
                }
            }
        }
    }
    
    // Testing method to clear database and force download flow
    fun clearDatabaseForTesting() {
        viewModelScope.launch {
            clearDatabaseUseCase(Unit).collect { result ->
                when (result.status) {
                    AsyncStatus.SUCCESS -> {
                        Napier.log(LogLevel.INFO, tag = "SplashScreenVM", message = "Database cleared successfully")
                        // After clearing, force navigation to download
                        eventsVersion.value = CheckVersionEvent.NavigateToDownload
                    }
                    AsyncStatus.ERROR -> {
                        Napier.log(LogLevel.ERROR, tag = "SplashScreenVM", message = "Failed to clear database: ${result.message}")
                    }
                    else -> {
                        // Loading state
                    }
                }
            }
        }
    }

}


