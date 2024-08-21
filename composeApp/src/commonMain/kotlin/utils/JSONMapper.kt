package utils
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

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


object AnySerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Any")

    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("This class can be saved only by Json")
        val element = when (value) {
            is Int -> JsonPrimitive(value)
            is String -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is List<*> -> JsonArray(value.map { JsonPrimitive(it.toString()) })
            is Map<*, *> -> JsonObject(value.mapKeys { it.key.toString() }
                .mapValues { JsonPrimitive(it.value.toString()) })
            else -> throw SerializationException("Unsupported type: ${value::class}")
        }
        jsonEncoder.encodeJsonElement(element)
    }

    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("This class can be loaded only by Json")
        val element = jsonDecoder.decodeJsonElement()
        return when (element) {
            is JsonPrimitive -> {
                when {
                    element.isString -> element.content
                    element.booleanOrNull != null -> element.boolean
                    element.intOrNull != null -> element.int
                    else -> throw SerializationException("Unsupported primitive type")
                }
            }
            is JsonArray -> element.map { it.toString() }
            is JsonObject -> element.mapValues { it.value.toString() }
            else -> throw SerializationException("Unsupported JSON element")
        }
    }
}