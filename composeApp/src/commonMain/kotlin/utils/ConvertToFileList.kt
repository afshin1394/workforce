package utils

import irancell.nwg.wfm.FileData
import irancell.nwg.wfm.UriToFile


fun convertToFileList(list:List<String>):List<FileData> {
    val fileDataArray = mutableListOf<FileData>()
    for (uri in list) {
        val fileData = UriToFile(uri)
        if (fileData != null) {
            fileDataArray.add(fileData)
        }
    }

    return fileDataArray
}