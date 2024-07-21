package irancell.nwg.wfm

actual class File actual constructor(private val path: String) {

    actual fun sizeInMB(): Int {

        return 0
    }

    actual fun extension(): String {

        return ""
    }
}