package utils
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

fun Map<String, Any>.toJson(): String {
    val jsonObject = buildJsonObject {
        this@toJson.forEach { (key, value) ->
            when (value) {
                is String -> put(key, JsonPrimitive(value))
                is Int -> put(key, JsonPrimitive(value))
                is Boolean -> put(key, JsonPrimitive(value))
                is Double -> put(key, JsonPrimitive(value))
                is Float -> put(key, JsonPrimitive(value.toDouble()))
                is Long -> put(key, JsonPrimitive(value))
                is List<*> -> {
                    val jsonArray = JsonArray(value.map { element ->
                        when (element) {
                            is String -> JsonPrimitive(element)
                            else -> throw IllegalArgumentException("Unsupported list element type")
                        }
                    })
                    put(key, jsonArray)
                }
                else -> throw IllegalArgumentException("Unsupported type")
            }
        }
    }
    return Json.encodeToString(jsonObject)
}
