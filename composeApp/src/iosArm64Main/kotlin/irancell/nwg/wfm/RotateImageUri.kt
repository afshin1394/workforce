package irancell.nwg.wfm

actual fun RotateImageUri(originalUri: String,key:String,angle: Float): String {

    val uri = NSURL.fileURLWithPath(originalUri)
    val rotatedUri = rotateImageAndSave(uri, angle)
    return rotatedUri.absoluteString ?: originalUri
}

private fun rotateImageAndSave(uri: NSURL, angle: Int): NSURL {

    val image = UIImage.imageWithContentsOfFile(uri.path!!)
        ?: return uri


    val rotatedImage = rotateUIImage(image, angle)


    val savePath = NSSearchPathForDirectoriesInDomains(
        NSDocumentDirectory,
        NSUserDomainMask,
        true
    ).firstOrNull() ?: NSTemporaryDirectory()
    val key = "rotatedImage_${NSUUID().UUIDString}.jpg"
    val saveUrl = NSURL.fileURLWithPath("$savePath/$key")

    val rotatedUri = saveUIImageToFile(rotatedImage, saveUrl)
    return rotatedUri
}

private fun rotateUIImage(image: UIImage, angle: Int): UIImage {
    val radians = (angle.toDouble() * kotlin.math.PI) / 180.0
    val rotatedSize = CGSizeMake(image.size.height, image.size.width)
    UIGraphicsBeginImageContext(rotatedSize)
    val context = UIGraphicsGetCurrentContext()!!

    context.translateBy(x = rotatedSize.width / 2, y = rotatedSize.height / 2)
    context.rotateBy(radians)
    image.drawInRect(CGRectMake(-image.size.width / 2, -image.size.height / 2, image.size.width, image.size.height))

    val rotatedImage = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()

    return rotatedImage ?: image
}

private fun saveUIImageToFile(image: UIImage, url: NSURL): NSURL {
    val imageData = image.jpegData(1.0) ?: return url
    imageData.writeToURL(url, atomically = true)

    return url
}