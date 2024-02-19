package irancell.nwg.wfm

import com.github.ln_12.library.ConnectivityStatus

    actual suspend fun checkConnectivity(isConnected : (boolean : Boolean) -> Unit) {
        val connectivityStatus = ConnectivityStatus()

        connectivityStatus.getStatus(success: { status in
        })
    }
