package presentation.screens.main.viewmodel

import androidx.compose.runtime.mutableStateListOf
import data.network.request.upload.UploadRequest
import irancell.nwg.wfm.FileData
import irancell.nwg.wfm.InternalStorage
import irancell.nwg.wfm.UriToFile
import irancell.nwg.wfm.ZipFiles
import irancell.nwg.wfm.provideAppContext
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

    fun saveFileFromUri(uri: String): FileData? {
        return UriToFile(uri)
    }
    fun getFileList():List<FileData> {
        val uris = listOf(
            "content://irancell.nwg.wfm.provider/wfmImages/Suspend/Original/--workorder_11-20240730-00003/38f217eb-4818-4118-aa87-af98da3c634f.jpg",
            "content://irancell.nwg.wfm.provider/wfmImages/Suspend/Original/--workorder_11-20240730-00003/e1fef636-047c-4335-b000-45ce51f69dcf.jpg"

            )

        val fileDataArray = mutableListOf<FileData>()

        for (uri in uris) {
            val fileData = saveFileFromUri(uri)
            if (fileData != null) {
                fileDataArray.add(fileData)
            }
        }



        return fileDataArray
    }

    fun convertToZip() {

        val fileList = getFileList()

        val zipFileData = ZipFiles(fileList,InternalStorage.getProcessRouteOriginal(provideAppContext()) + "files.zip")



        if (zipFileData != null) {
            println("Zip file created: ${zipFileData.path}")

            val uploadRequest = UploadRequest(
                extraInfo = "Some extra info",
                totalPart = 1,
                currentPart = 1,
                name = "Sample Name",
                customId = "12345",
                file = zipFileData
            )




        } else {
            println(" Zip file created:     ${"Failed to create zip file"}")
        }
    }
}
