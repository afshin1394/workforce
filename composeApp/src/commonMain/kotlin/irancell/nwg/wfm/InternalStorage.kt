package irancell.nwg.wfm

expect class InternalStorage {
    companion object {

         fun initWFMImages(context:Any) : Any
         fun initSuspendImages(context:Any) : Any
         fun initProcessImages(context:Any) : Any
         fun createWorkItemImages(context:Any,pathName : String,name : String) : Any

    }
}