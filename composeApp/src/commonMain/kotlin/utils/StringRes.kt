package utils


import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc

fun stringToResource(text: String): StringDesc {
    return StringDesc.Raw(text)
}