package irancell.nwg.wfm

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttpConfig
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

internal actual fun HttpClientConfig<*>.configureForPlatform(){
    engine {
        this as OkHttpConfig
        config {

                val trustAllCert = AllCertsTrustManager()
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, arrayOf(trustAllCert), SecureRandom())
                sslSocketFactory(sslContext.socketFactory, trustAllCert)

        }
    }
}
internal class AllCertsTrustManager : X509TrustManager {

    @Suppress("TrustAllX509TrustManager")
    override fun checkServerTrusted(
        chain: Array<X509Certificate>,
        authType: String
    ) {
        // no-op
    }

    @Suppress("TrustAllX509TrustManager")
    override fun checkClientTrusted(
        chain: Array<X509Certificate>,
        authType: String
    ) {
        // no-op
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
}
