package utils

import domain.models.UploadDomain
import irancell.nwg.wfm.File
import kotlin.random.Random

fun extractKeyFromValue(value: String): String? {
    val atIndex = value.indexOf('@')
    val starIndex = value.indexOf('.', atIndex)
    return if (atIndex != -1 && starIndex != -1) {
        value.substring(atIndex + 1, starIndex)
    } else {
        null
    }
}

fun extractFileSizeFromKey(key: String): Long? {
    val parts = key.split("*")
    return if (parts.size >= 2) {
        parts[1].toLongOrNull()
    } else {
        null
    }
}

fun generateRandom6DigitNumber(): Int {
    return Random.nextInt(100000, 999999)
}

fun getFileExtension(filePath: String): String {
    val nameWithoutParams = filePath.substringBeforeLast('*')
    return nameWithoutParams.substringAfterLast('.', "")
}

fun getMimeType(filePath: String): String {
    val extension = getFileExtension(filePath).toLowerCase()
    return when (extension) {
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        "gif" -> "image/gif"
        "pdf" -> "application/pdf"
        "txt" -> "text/plain"
        else -> "application/octet-stream"
    }
}

fun formatUploadDomainList(uploadList: List<UploadDomain>): List<UploadDomain> {
    val formattedList = mutableListOf<UploadDomain>()

    for (upload in uploadList) {
        val keyFromValue = extractKeyFromValue(upload.value?:"")
        if (keyFromValue != null) {
            val random6Digit = generateRandom6DigitNumber()
            val fileExtension = getFileExtension(upload.value?:"")
            val fileMimeType = getMimeType(upload.value?:"")
            val fileSize = extractFileSizeFromKey(upload.key?:"") ?: 0L
            val newValue = "${upload.value!!.substringBeforeLast('*')}${"*"}${random6Digit}${"*"}${fileMimeType}${"*"}${upload.key+"."+fileExtension}${"*"}${upload.value!!.substringAfterLast('*')}"
            val newUploadDomain = UploadDomain(key = keyFromValue, value = newValue)
            formattedList.add(newUploadDomain)
        }
    }

    return formattedList
}