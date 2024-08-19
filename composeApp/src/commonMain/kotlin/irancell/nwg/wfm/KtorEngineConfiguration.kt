package irancell.nwg.wfm

import io.ktor.client.HttpClientConfig

internal expect fun HttpClientConfig<*>.configureForPlatform()