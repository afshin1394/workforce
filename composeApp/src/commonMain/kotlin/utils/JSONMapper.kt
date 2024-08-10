package utils
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject


fun Map<String, Any>.mutableToJson(): String {
    val jsonObject = buildJsonObject {
        this@mutableToJson.forEach { (key, value) ->
            var valuee = value
            if(valuee is JsonPrimitive)
                valuee  = valuee.content
            when (valuee) {

                is String -> put(key, JsonPrimitive(valuee))
                is Int -> put(key, JsonPrimitive(valuee))
                is Boolean -> put(key, JsonPrimitive(valuee))
                is Double -> put(key, JsonPrimitive(valuee))
                is Float -> put(key, JsonPrimitive(valuee.toDouble()))
                is Long -> put(key, JsonPrimitive(valuee))
                is List<*> -> {
                    val jsonArray = JsonArray(valuee.map { element ->
                        var elementt = element
                        if(elementt is JsonPrimitive)
                            elementt  = elementt.content
                        when (elementt) {
                            is String -> JsonPrimitive(elementt)
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