package irancell.nwg.wfm

import androidx.compose.runtime.Composable

actual class Camera {

    actual companion object {
        @Composable
        actual fun onResult(onSuccess: (uri: Any) -> Unit) {

        }
        @Composable
        actual fun launchCamera(savePath: String,key:String){

        }
    }


}