package irancell.nwg.wfm

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

interface TelephonyData {
    fun getTelephonyData() : JsonObject
    fun getCellID() : String
}
expect class TelephonyDataImpl : TelephonyData
