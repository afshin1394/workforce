package irancell.nwg.wfm

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

actual class TelephonyDataImpl : TelephonyData {
    
    override fun getTelephonyData(): JsonObject {
        // iOS implementation - return empty object as placeholder
        return buildJsonObject {
            put("platform", "iOS")
            put("available", false)
        }
    }
    
    override fun getCellID(): String {
        // iOS implementation - return empty string as placeholder
        return ""
    }
}