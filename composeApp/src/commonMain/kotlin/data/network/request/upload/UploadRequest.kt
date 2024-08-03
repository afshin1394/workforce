package data.network.request.upload

import irancell.nwg.wfm.FileData

data class UploadRequest (

    val extraInfo: String,
    val totalPart: Int,
    val currentPart: Int,
    val name: String,
    val customId: String,
   val file: FileData
)