package utils


import io.github.aakira.napier.Napier
import irancell.nwg.wfm.InstallApk
import irancell.nwg.wfm.PerformDownload
import irancell.nwg.wfm.Unzip
import irancell.nwg.wfm.getSharedPref


suspend fun startDownloadFileApk(url: String): Boolean {
    val url = DevelopmentBASEURLForVersionFile + url


    if (getSharedPref().getString(FileApk).isNullOrEmpty()) {
        val downloadResult = PerformDownload(url)


        val result = downloadResult!!.getOrNull()
        if (result != null) {
            Napier.i(tag = "targetDire", message =  "startDownloadFileApk: result.targetDirectoryPath "+result.targetDirectoryPath)
            val apkFilePath = Unzip(result.zipFilePath, result.targetDirectoryPath)
            getSharedPref().put(FileApk, apkFilePath ?: "")


            return apkFilePath?.let { path ->
                InstallApk(path)
                true
            } ?: run {
                false
            }
        } else {

            getSharedPref().put(FileApk, "")
            return false
        }
    } else {

        val apkFilePath = getSharedPref().getString(FileApk)
        return apkFilePath?.let { path ->
            InstallApk(path)
            true
        } ?: run {
            false
        }
    }
}