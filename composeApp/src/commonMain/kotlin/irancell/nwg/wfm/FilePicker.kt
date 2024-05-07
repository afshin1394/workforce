package irancell.nwg.wfm

import androidx.compose.runtime.Composable

expect class FilePicker {

    companion object {
        @Composable
         fun onResult(onSuccess:  (List<Pair<Any, Any>>) -> Unit)


        @Composable
        fun launchFilePicker()


        fun  openFile(filePath:Any,context: Any)


    }



}