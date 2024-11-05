package irancell.nwg.wfm

import kotlinx.coroutines.sync.Semaphore
expect fun hardwareInfo(): Semaphore
