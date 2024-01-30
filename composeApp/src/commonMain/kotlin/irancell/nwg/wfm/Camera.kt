package irancell.nwg.wfm

import androidx.compose.runtime.Composable

expect class Camera{

     companion object {
          @Composable
          fun ImagePicker()
     }

}