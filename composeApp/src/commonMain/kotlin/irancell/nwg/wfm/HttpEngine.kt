package irancell.nwg.wfm

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine

expect fun httpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient
