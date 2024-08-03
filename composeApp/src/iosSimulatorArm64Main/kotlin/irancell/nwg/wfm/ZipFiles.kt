package irancell.nwg.wfm

actual fun ZipFiles(fileDataList: List<FileData>, zipFilePath: String): FileData? {
    return try {
        val tempDir = NSTemporaryDirectory()
        val zipFilePath = tempDir + zipFileName
        val paths = fileDataList.map { it.path }

        if (SSZipArchive.createZipFileAtPath(zipFilePath, paths)) {
            FileData(zipFileName, zipFilePath, NSFileManager.defaultManager().attributesOfItemAtPath(zipFilePath, null)?.fileSize() ?: 0)
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}