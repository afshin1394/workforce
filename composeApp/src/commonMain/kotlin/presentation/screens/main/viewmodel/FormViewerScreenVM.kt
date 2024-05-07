package presentation.screens.main.viewmodel

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import presentation.model.UploadFileModel
import presentation.screens.ticket_process.events.ImageEvent
import utils.BaseViewModel

class FormViewerScreenVM : BaseViewModel() {


    private val _imageEvent = MutableStateFlow<ImageEvent>(ImageEvent.Default)
    val imageEvent = _imageEvent.asStateFlow()

    val attachmentsUri = MutableStateFlow("")

    fun openCamera(){
        _imageEvent.update { ImageEvent.OpenCamera }
    }
    fun openPreview(index : Int){
        _imageEvent.update { ImageEvent.PhotoPreview }
    }

    fun openEdit(index : Int){
        _imageEvent.update { ImageEvent.EditPhoto }
    }

    fun updateDeletedPhoto(imgUri: String, position: Int) {

        val attachmentUriList: List<String> = attachmentsUri.value.split(",")
        val updatedList = attachmentUriList.filter { it != imgUri }
        val updatedListAsString = updatedList.joinToString(",")

        attachmentsUri.value =  updatedListAsString
    }



    //////////////////////////upload/////////////////

     val uploadDomain = MutableStateFlow<UploadFileModel>(
        UploadFileModel(
            path = Unit,
            fileName = ""

        )
    )
    var uploadDomainList = mutableStateListOf<UploadFileModel>()
}
