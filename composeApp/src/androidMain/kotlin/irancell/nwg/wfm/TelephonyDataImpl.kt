package irancell.nwg.wfm

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import irancell.nwg.wfm.network.toJson
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

actual class TelephonyDataImpl(context: Context) : TelephonyData {
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    @SuppressLint("MissingPermission")
    override fun getTelephonyData(): JsonArray {
      return  telephonyManager.allCellInfo.toJson()
    }

    override fun getCellID() : String {
        return ""
    }







}

