package irancell.nwg.wfm

import kotlinx.coroutines.flow.MutableStateFlow
import utils.ServiceState
import utils.TicketListStatus

internal expect class BackgroundServiceApp {

    companion object {
        val serviceState : MutableStateFlow<ServiceState>
        val ticketListState : MutableStateFlow<TicketListStatus>
        fun updateServiceState(serviceState: ServiceState)
        fun startBackgroundService()
        fun stopBackgroundService()
        fun isServiceRunning(): Boolean
    }
}