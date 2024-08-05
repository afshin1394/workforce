package utils

import irancell.nwg.wfm.FileData
import irancell.nwg.wfm.UriToFile


fun convertToFileList(list:List<String>):List<String> {
    val fileDataArray = mutableListOf<String>()
    for (uri in list) {
        val fileData = UriToFile(uri)

        if (uri.startsWith("content:")){
            if (fileData != null) {
                fileDataArray.add(fileData)
            }

        }else{
            fileDataArray.add(uri)
        }

    }

    return fileDataArray
}