package utils

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

fun JsonElement?.toStringList(): List<String> {

    return when (this) {
        is JsonArray -> {
            this.mapNotNull { it.jsonPrimitive.contentOrNull }
        }
        is JsonPrimitive -> {
            listOfNotNull(this.contentOrNull)
        }
        else -> emptyList()
    }
}