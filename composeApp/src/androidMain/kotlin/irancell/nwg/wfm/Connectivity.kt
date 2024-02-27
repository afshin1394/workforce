package irancell.nwg.wfm

import android.content.Context
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

actual suspend fun checkConnectivity(isConnected: (boolean: Boolean) -> Unit) {
//    val connectivityStatus = ConnectivityStatus(provideAppContext() as Context)
//    connectivityStatus.start()
//    connectivityStatus.getStatus{
//        isConnected(it)
//    }
//    connectivityStatus.stop()

}
