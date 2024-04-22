package irancell.nwg.wfm

actual fun ParseUri(uriString: String): Any {
    return NSURL(string = uriString)
}