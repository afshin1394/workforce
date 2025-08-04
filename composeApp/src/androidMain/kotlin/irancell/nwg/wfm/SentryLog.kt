package irancell.nwg.wfm

// import io.sentry.Sentry

actual fun SentryLog(message:String): Any{
    // return   Sentry.captureException(Exception(message))
    println("SentryLog: $message")
    return Unit
}