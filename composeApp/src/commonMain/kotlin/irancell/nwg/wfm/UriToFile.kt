package irancell.nwg.wfm

data class FileData(val name: String, val path: String, val size: Long)

expect fun UriToFile(uri: String): String?