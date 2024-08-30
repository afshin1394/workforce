package irancell.nwg.wfm

import androidx.compose.runtime.Composable

expect class Camera{

     companion object {
          @Composable
           fun onResult(onSuccess: (uri: Any,obj : Any?) -> Unit)


          @Composable
          fun launchCamera(obj: Any?=null,savePath: String,key:String)


     }

}