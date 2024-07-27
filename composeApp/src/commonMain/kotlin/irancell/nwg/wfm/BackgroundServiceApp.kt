package irancell.nwg.wfm

import kotlinx.coroutines.flow.MutableStateFlow
import utils.ServiceState

internal expect class BackgroundServiceApp {

    companion object {
        val serviceState : MutableStateFlow<ServiceState>
        fun updateServiceState(serviceState: ServiceState)
        fun startBackgroundService()
        fun stopBackgroundService()
    }
}