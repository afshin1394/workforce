package irancell.nwg.wfm

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.CellInfoLte
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import irancell.nwg.wfm.network.CellInfoLTE
import irancell.nwg.wfm.network.toJson
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

actual class TelephonyDataImpl(private val context: Context) : TelephonyData {

    @SuppressLint("MissingPermission")
    override fun getTelephonyData(): JsonObject {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        return  telephonyManager.allCellInfo.toJson()
    }

    @SuppressLint("MissingPermission")
    override fun getCellID() : String {

        return ""
    }







}

