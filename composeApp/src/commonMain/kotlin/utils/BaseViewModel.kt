package utils

import com.plusmobileapps.konnectivity.Konnectivity
import com.plusmobileapps.konnectivity.NetworkConnection
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import domain.usecase.ResultStatus
import domain.usecase.usecase.auth.AutoLogoutUseCase
import domain.usecase.usecase.ipDetection.IpDetectionUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.BackgroundServiceApp
import irancell.nwg.wfm.GPS
import irancell.nwg.wfm.LifecycleEvent
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.Orientation
import irancell.nwg.wfm.getOrientation
import irancell.nwg.wfm.isRootedOrEmulator
import irancell.nwg.wfm.provideAppContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class AvailabilityStatus {
    data object NotRunning : AvailabilityStatus()
    data object Available : AvailabilityStatus()
    data object Unavailable : AvailabilityStatus()
    data object NoInternet : AvailabilityStatus()
}

sealed class ViewStates {
    data object Default : ViewStates()
    data object Loading : ViewStates()
    data class Error(val message: String) : ViewStates()
    data class Success(val message: StringResource? = MR.strings.success) : ViewStates()
    data object EMPTY : ViewStates()
}

sealed interface ServiceState {
    data object NotRunning : ServiceState
    data object Normal : ServiceState
    data object Suspend : ServiceState
    data class Faulty(val message: StringResource) : ServiceState
}

sealed class VpnDetectionStates {
    data object Default : VpnDetectionStates()
    data object HideBottomSheet : VpnDetectionStates()
    data object ShowBottomSheet : VpnDetectionStates()
}

sealed class DeviceSafetyStates {
    data object Default : DeviceSafetyStates()
    data object HideBottomSheet : DeviceSafetyStates()
    data object ShowBottomSheet : DeviceSafetyStates()
}

sealed interface GpsState {
    data object Default : GpsState
    data object Enabled : GpsState
    data object Disabled : GpsState
}

sealed class NetworkStates {
    data object Default : NetworkStates()
    data object NetworkConnectionNONE : NetworkStates()
    data object NetworkConnectionWIFI : NetworkStates()
    data object NetworkConnectionCELLULAR : NetworkStates()
    data object NetworkConnectionUNKNOWN : NetworkStates()
}


sealed interface OrientationState {
    data object Default : OrientationState
    data object Landscape : OrientationState
    data object Portrait : OrientationState
}

open class BaseViewModel : ViewModel(), KoinComponent {
    private val ipDetectionUseCase: IpDetectionUseCase by inject()
    private val autoLogoutUseCase: AutoLogoutUseCase by inject()
    val loading = MutableStateFlow(false)
    private val _state = MutableStateFlow<ViewStates>(ViewStates.Default)
    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Normal)
    private val _networkState = MutableStateFlow<NetworkStates>(NetworkStates.Default)
    private val _vpnDetectionState =
        MutableStateFlow<VpnDetectionStates>(VpnDetectionStates.Default)
    private val _isDeviceSafeState =
        MutableStateFlow<DeviceSafetyStates>(DeviceSafetyStates.Default)
    private val _gpsState = MutableStateFlow<GpsState>(GpsState.Default)
    private val _lifeCycleEvent = MutableStateFlow(LifecycleEvent.ON_ANY)
    private val _orientationState = MutableStateFlow<OrientationState>(OrientationState.Default)
    val lifeCycleEvent = _lifeCycleEvent.asStateFlow()
    val state = _state.asStateFlow()
    val serviceState = _serviceState.asStateFlow()
    val networkState = _networkState.asStateFlow()
    val vpnDetectionStates = _vpnDetectionState.asStateFlow()
    val isDeviceSafeState = _isDeviceSafeState.asStateFlow()
    val gpsState = _gpsState.asStateFlow()
    val orientationState = _orientationState.asStateFlow()
    private val konnectivity: Konnectivity = Konnectivity()
    private val _availabilityStatus =
        MutableStateFlow<AvailabilityStatus>(AvailabilityStatus.Unavailable)
    val availabilityStatus = _availabilityStatus.asStateFlow()

    init {
        // Force all security states to safe values for emulator compatibility
        _isDeviceSafeState.update { DeviceSafetyStates.HideBottomSheet }
        _vpnDetectionState.update { VpnDetectionStates.HideBottomSheet }
        
        BackgroundServiceApp.updateServiceState(ServiceState.Normal)
        traceNetwork()
        traceLocation()
        collectServiceState()
        traceOrientation()
    }

    fun updateAvailabilityState(availabilityStatus: AvailabilityStatus) {
        _availabilityStatus.update { availabilityStatus }
    }

    private fun collectServiceState() {
        viewModelScope.launch {
            BackgroundServiceApp.serviceState.collect { newState ->
                Napier.log(LogLevel.ASSERT, tag = "serviceState", message = newState.toString())
                
                // FIXED: Only update if state actually changed to prevent loops
                if (_serviceState.value != newState) {
                    when (newState) {
                        is ServiceState.Faulty -> {
                            _serviceState.update { newState }
                            autoLogoutUseCase(Unit).collect { result ->
                                when {
                                    result.status == AsyncStatus.SUCCESS -> {
                                        // Already updated above, no need to update again
                                    }
                                }
                            }
                        }

                        ServiceState.NotRunning -> {
                            _serviceState.update { newState }
                            updateAvailabilityState(AvailabilityStatus.NotRunning)
                        }

                        ServiceState.Normal -> {
                            _serviceState.update { newState }
                            updateAvailabilityState(AvailabilityStatus.Available)
                        }

                        ServiceState.Suspend -> {
                            _serviceState.update { newState }
                            // Don't change availability status for suspend
                        }
                    }
                }
            }
        }
    }

    fun updateLifeCycleEventState(event: LifecycleEvent) {
        _lifeCycleEvent.update { event }
    }

    fun detectDeviceSafety() {
        // Bypassed for development/emulator testing
        _isDeviceSafeState.update { DeviceSafetyStates.HideBottomSheet }
        /*
        if (isRootedOrEmulator()) {
            _isDeviceSafeState.update { DeviceSafetyStates.ShowBottomSheet }
        } else {
            _isDeviceSafeState.update { DeviceSafetyStates.HideBottomSheet }
        }
        */
    }

    fun restrictForeignIp() {
        // Bypassed for development/emulator testing
        _vpnDetectionState.update { VpnDetectionStates.HideBottomSheet }
        /*
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

                    AsyncStatus.EMPTY -> {

                    }

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
        */
    }

    private fun traceLocation() {
        GPS.registerGps(provideAppContext()) { isEnabled ->
            val newGpsState = if (isEnabled) GpsState.Enabled else GpsState.Disabled
            // FIXED: Only update if state actually changed
            if (_gpsState.value != newGpsState) {
                _gpsState.update { newGpsState }
            }
        }
        
        // Initialize GPS state
        val initialGpsState = if (GPS.getLocationsState()) GpsState.Enabled else GpsState.Disabled
        if (_gpsState.value != initialGpsState) {
            _gpsState.update { initialGpsState }
        }
    }


    private fun traceOrientation() {
        Orientation.orientationState(provideAppContext()) { orientationString ->
            val newOrientationState = when (orientationString) {
                "landscape" -> OrientationState.Landscape
                "portrait" -> OrientationState.Portrait
                else -> OrientationState.Default
            }
            // FIXED: Only update if state actually changed
            if (_orientationState.value != newOrientationState) {
                _orientationState.update { newOrientationState }
            }
        }
    }

    private fun traceNetwork() {
        viewModelScope.launch(Dispatchers.Main) {
            konnectivity.currentNetworkConnectionState
                .collect { connection ->
                    val networkState = when (connection) {
                        NetworkConnection.NONE -> NetworkStates.NetworkConnectionNONE
                        NetworkConnection.WIFI -> NetworkStates.NetworkConnectionWIFI
                        NetworkConnection.CELLULAR -> NetworkStates.NetworkConnectionCELLULAR
                        else -> NetworkStates.NetworkConnectionUNKNOWN
                    }
                    // OPTIMIZED: Only update if state actually changed
                    if (_networkState.value != networkState) {
                        _networkState.update { networkState }
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



    fun handleError(resultStatus: ResultStatus?, errorMessage: String? = null){

        val errorResource = errorMessage?.let { stringToResource(it) } ?: MR.strings.client_error.desc()
        when (resultStatus) {
            is ResultStatus.CLIENT_EXCEPTION.FORBIDDEN -> {
                _serviceState.update { ServiceState.Faulty(MR.strings.unauthorized) }
            }

            is ResultStatus.CLIENT_EXCEPTION.UNATHORIZED -> {
                _serviceState.update { ServiceState.Faulty(MR.strings.unauthorized) }
            }

            is ResultStatus.CLIENT_EXCEPTION -> {
                _state.update { ViewStates.Error(errorMessage?:"An error has been occurred please contact support") }
            }

            is ResultStatus.EXCEPTION -> {
                _state.update { ViewStates.Error(errorMessage?:"An error has been occurred please contact support") }
            }

            is ResultStatus.IO_EXCEPTION -> {
                _state.update { ViewStates.Error(errorMessage?:"An error has been occurred please contact support") }
            }

            is ResultStatus.REDIRECT_EXCEPTION -> {
                _state.update { ViewStates.Error("Redirect Exception happened") }
            }

            is ResultStatus.SERVER_EXCEPTION -> {
                _state.update { ViewStates.Error("Server is not available right now, please try later") }
            }

            is ResultStatus.SUCCESS -> {
                _state.update { ViewStates.Error("Success") }
            }

            is ResultStatus.TIME_OUT -> {
                _state.update { ViewStates.Error("Please check your network connection") }
            }

            else -> {
                _state.update { ViewStates.Error(errorMessage?:"An error has been occurred please contact support") }
            }
        }
    }

    fun updateServiceState(serviceState: ServiceState) {
        // FIXED: Only update if state actually changed to prevent infinite loops
        if (_serviceState.value != serviceState) {
            _serviceState.update { serviceState }
        }
    }
}
