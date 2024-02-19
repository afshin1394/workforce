package irancell.nwg.wfm

actual suspend fun checkConnectivity(isConnected : (boolean : Boolean) -> Unit) {
    val connectivityStatus = ConnectivityStatus()

    connectivityStatus.getStatus(success: { status in
    })
}

