package utils

import com.benasher44.uuid.uuid4
import data.network.request.upload.UploadRequest
import irancell.nwg.wfm.File
import irancell.nwg.wfm.InternalStorage
import irancell.nwg.wfm.ZipFiles
import irancell.nwg.wfm.provideAppContext

fun convertToZip(list: List<String>,name:String,ticketNumber:String): UploadRequest {

    val uploadRequest: UploadRequest
    val fileList = convertToFileList(list)


    val zipFileData = ZipFiles(fileList, InternalStorage.getWFMRoute(provideAppContext()) + "files.zip")

    uploadRequest = UploadRequest(
        extraInfo = createJsonWithTicketNumber(ticketNumber),
        totalPart = 1,
        currentPart = 1,
        name =name,
        customId = uuid4().toString(),
        file = File(zipFileData!!.path).readBytes()
    )


    return uploadRequest
}