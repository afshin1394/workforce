package irancell.nwg.wfm

import domain.models.PhotoDomain

expect class InternalStorage {
    companion object {

        fun initWFMImages(context: Any)
        fun initSuspendImages(context: Any)
        fun initProcessImages(context: Any)

        fun getWFMRoute(context: Any): String

        fun getSuspendRouteOriginal(context: Any): String
        fun getUploadFileRouteOriginal(context: Any): String
        fun getSuspendRouteEdited(context: Any): String


        fun getProcessRouteOriginal(context: Any): String
        fun getProcessRouteEdited(context: Any): String


        fun createWorkItemImages(context: Any, pathName: String, name: String): Any

        fun removeFiles(paths: List<Any>?)


    }
}