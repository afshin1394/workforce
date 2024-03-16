package irancell.nwg.wfm

actual class InternalStorage{
    actual companion object {
        actual fun initWFMImages(context: Any) : Any {
            return ""
        }
        actual fun initSuspendImages(context: Any): Any {
            return ""

        }
        actual fun initProcessImages(context: Any): Any {
            return ""

        }

        actual fun createWorkItemImages(
            context: Any,
            pathName: String,
            name: String
        ): Any {
            TODO("Not yet implemented")
        }

        actual fun getWFMRoute(context: Any): String {
            TODO("Not yet implemented")
        }

        actual fun getSuspendRoute(context: Any): String {
            TODO("Not yet implemented")
        }

        actual fun getProcessRoute(context: Any): String {
            TODO("Not yet implemented")
        }

    }
}