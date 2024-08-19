package irancell.nwg.wfm

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.DarwinClientEngineConfig

internal actual fun HttpClientConfig<*>.configureForPlatform(){
    engine {
        this as DarwinClientEngineConfig
            handleChallenge(TrustAllChallengeHandler())

    }
}

internal class TrustAllChallengeHandler() : ChallengeHandler {

    override fun invoke(
        session: NSURLSession,
        task: NSURLSessionTask,
        challenge: NSURLAuthenticationChallenge,
        completionHandler: (NSURLSessionAuthChallengeDisposition, NSURLCredential?) -> Unit,
    ) {
        // Check that we want to handle this kind of challenge
        val protectionSpace = challenge.protectionSpace
        if (protectionSpace.authenticationMethod != NSURLAuthenticationMethodServerTrust) {
            // Not a 'NSURLAuthenticationMethodServerTrust', default handling...
            completionHandler(NSURLSessionAuthChallengePerformDefaultHandling, null)
            return
        }

        val serverTrust = challenge.protectionSpace.serverTrust
        if (serverTrust == null) {
            // Server trust is null, default handling...
            completionHandler(NSURLSessionAuthChallengePerformDefaultHandling, null)
            return
        }

        // Get the servers certs
        val certChain = SecTrustCopyCertificateChain(serverTrust)
        // Set those certs as trusted anchors
        SecTrustSetAnchorCertificates(serverTrust, certChain)

        if (serverTrust.trustIsValid()) {
            // ✔ Server trust is valid, continue...
            val credential = NSURLCredential.credentialForTrust(serverTrust)
            completionHandler(NSURLSessionAuthChallengeUseCredential, credential)
        } else {
            // ✖ Server trust not valid, cancel challenge...
            completionHandler(NSURLSessionAuthChallengeCancelAuthenticationChallenge, null)
        }
    }
}

/**
 * Evaluates trust for the specified certificate and policies.
 */
private fun SecTrustRef.trustIsValid(): Boolean {
    var isValid = false

    val version = cValue<NSOperatingSystemVersion> {
        majorVersion = 12
        minorVersion = 0
        patchVersion = 0
    }
    if (NSProcessInfo().isOperatingSystemAtLeastVersion(version)) {
        memScoped {
            val result = alloc<CFErrorRefVar>()
            // https://developer.apple.com/documentation/security/2980705-sectrustevaluatewitherror
            isValid = SecTrustEvaluateWithError(this@trustIsValid, result.ptr)
        }
    } else {
        // https://developer.apple.com/documentation/security/1394363-sectrustevaluate
        memScoped {
            val result = alloc<SecTrustResultTypeVar>()
            result.value = kSecTrustResultInvalid
            val status = SecTrustEvaluate(this@trustIsValid, result.ptr)
            if (status == errSecSuccess) {
                isValid = result.value == kSecTrustResultUnspecified ||
                        result.value == kSecTrustResultProceed
            }
        }
    }

    return isValid
}