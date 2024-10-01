package utils

import com.plusmobileapps.konnectivity.Konnectivity
import com.plusmobileapps.konnectivity.NetworkConnection
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.StringResource
import domain.usecase.ResultStatus
import domain.usecase.usecase.ipDetection.IpDetectionUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.BackgroundServiceApp
import irancell.nwg.wfm.GPS
import irancell.nwg.wfm.LifecycleEvent
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.provideAppContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class ViewStates() {
    data object Default : ViewStates()
    data object Loading : ViewStates()
    data class Error(val message: StringResource) : ViewStates()
    data class Success(val message: StringResource? = MR.strings.success) : ViewStates()
    data object EMPTY : ViewStates()
    data class UnAuthorized(val message: StringResource) : ViewStates()
}

sealed class VpnDetectionStates() {
    data object Default : VpnDetectionStates()
    data object HideBottomSheet : VpnDetectionStates()
    data object ShowBottomSheet : VpnDetectionStates()
}

sealed interface GpsState {
    data object Default : GpsState
    data object Enabled : GpsState
    data object Disabled : GpsState
}

sealed class NetworkStates() {
    data object Default : NetworkStates()
    data object NetworkConnectionNONE : NetworkStates()
    data object NetworkConnectionWIFI : NetworkStates()
    data object NetworkConnectionCELLULAR : NetworkStates()
}


open class BaseViewModel : ViewModel(), KoinComponent {
    private val ipDetectionUseCase: IpDetectionUseCase by inject()

    val loading = MutableStateFlow(false)

    private val _state = MutableStateFlow<ViewStates>(ViewStates.Default)
    private val _networkState = MutableStateFlow<NetworkStates>(NetworkStates.Default)
    private val _vpnDetectionState =
        MutableStateFlow<VpnDetectionStates>(VpnDetectionStates.Default)
    private val _gpsState = MutableStateFlow<GpsState>(GpsState.Default)
    private val _lifeCycleEvent = MutableStateFlow(LifecycleEvent.ON_ANY)
    val lifeCycleEvent = _lifeCycleEvent.asStateFlow()

    val state = _state.asStateFlow()
    val networkState = _networkState.asStateFlow()
    val vpnDetectionStates = _vpnDetectionState.asStateFlow()
    val gpsState = _gpsState.asStateFlow()
    val konnectivity: Konnectivity = Konnectivity()

    init {
        traceNetwork()
        traceLocation()
        collectServiceState()
    }

    private fun collectServiceState() {
        viewModelScope.launch {
            BackgroundServiceApp.serviceState.collect {
                if (it is ServiceState.Faulty) {
                    _state.update {
                        ViewStates.UnAuthorized(MR.strings.unauthorized)
                    }

                }
            }
        }
    }

    fun updateLifeCycleEventState(event: LifecycleEvent) {
        _lifeCycleEvent.update { event }
    }

    fun restrictForeignIp() {
        viewModelScope.launch {
            ipDetectionUseCase(Unit).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
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
                            _vpnDetectionState.update { VpnDetectionStates.ShowBottomSheet }
                        } else {
                            _vpnDetectionState.update { VpnDetectionStates.HideBottomSheet }
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

    private fun traceLocation() {
        GPS.registerGps(provideAppContext()) {
            if (it)
                _gpsState.update { GpsState.Enabled }
            else
                _gpsState.update { GpsState.Disabled }
        }
        when (GPS.getLocationsState()) {
            true -> {
                _gpsState.update { GpsState.Enabled }
            }

            false -> {
                _gpsState.update { GpsState.Disabled }
            }
        }
    }


    private fun traceNetwork() {
        viewModelScope.launch(Dispatchers.Main) {
            konnectivity.currentNetworkConnectionState.collect { connection ->
                when (connection) {
                    NetworkConnection.NONE -> {
                        _networkState.update { NetworkStates.NetworkConnectionNONE }
                    }

                    NetworkConnection.WIFI -> {
                        _networkState.update { NetworkStates.NetworkConnectionWIFI }
                    }

                    NetworkConnection.CELLULAR -> {
                        _networkState.update { NetworkStates.NetworkConnectionCELLULAR }
                    }
                }
            }
        }
    }

    fun updateState(viewStates: ViewStates) {
        _state.update { viewStates }
    }

    fun updateVpnDetectionState(vpnDetectionStates: VpnDetectionStates) {
        _vpnDetectionState.update { vpnDetectionStates }
    }

    fun handleError(resultStatus: ResultStatus?) {

        when (resultStatus) {
            is ResultStatus.CLIENT_EXCEPTION.FORBIDDEN -> {
                _state.update { ViewStates.UnAuthorized(MR.strings.unauthorized) }
            }

            is ResultStatus.CLIENT_EXCEPTION.UNATHORIZED -> {
                _state.update { ViewStates.UnAuthorized(MR.strings.unauthorized) }
            }

            is ResultStatus.CLIENT_EXCEPTION -> {
                _state.update { ViewStates.Error(MR.strings.client_error) }
            }

            is ResultStatus.EXCEPTION -> {
                _state.update { ViewStates.Error(MR.strings.general_error) }
            }

            is ResultStatus.IO_EXCEPTION -> {
                _state.update { ViewStates.Error(MR.strings.general_error) }
            }

            is ResultStatus.REDIRECT_EXCEPTION -> {
                _state.update { ViewStates.Error(MR.strings.redirect_error) }
            }

            is ResultStatus.SERVER_EXCEPTION -> {
                _state.update { ViewStates.Error(MR.strings.server_error) }
            }

            is ResultStatus.SUCCESS -> {
                _state.update { ViewStates.Error(MR.strings.success) }
            }

            is ResultStatus.TIME_OUT -> {
                _state.update { ViewStates.Error(MR.strings.timeout_error) }
            }

            else -> {
                _state.update { ViewStates.Error(MR.strings.general_error) }
            }
        }
    }
}
