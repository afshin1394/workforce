package irancell.nwg.wfm.network

import android.os.Build
import android.telephony.CellInfoCdma
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoWcdma
import androidx.annotation.RequiresApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.modules.*
import kotlinx.serialization.serializer


fun List<android.telephony.CellInfo>.toJson() : JsonArray{
    val cellInfoModule = SerializersModule {
        polymorphic(CellInfo::class) {
            subclass(CellInfoCDMA::class, CellInfoCDMA.serializer())
            subclass(CellInfoLTE::class, CellInfoLTE.serializer())
            subclass(CellInfoWCDMA::class, CellInfoWCDMA.serializer())
            subclass(CellInfoGSM::class, CellInfoGSM.serializer())
        }
    }

// Step 4: Configure your JSON serializer with the module
    val json = Json {
        serializersModule = cellInfoModule
        classDiscriminator = "type" // Optional: Use a custom discriminator field
    }

    val cellInfos = this.map { cellInfo ->
        when (cellInfo) {

            is CellInfoGsm -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                CellInfoGSM(cellInfo.cellIdentity.cid,cellInfo.cellIdentity.arfcn, cellInfo.cellIdentity.mcc, cellInfo.cellIdentity.mnc, cellInfo.cellIdentity.psc, cellInfo.cellSignalStrength.dbm,cellInfo.cellIdentity.bsic ,cellInfo.cellIdentity.arfcn, cellSignalStrengthGSM = CellSignalStrengthGSM(cellInfo.cellSignalStrength.rssi,cellInfo.cellSignalStrength.level,cellInfo.cellSignalStrength.dbm,cellInfo.cellSignalStrength.asuLevel,cellInfo.cellSignalStrength.bitErrorRate))
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    CellInfoGSM(cellInfo.cellIdentity.cid,cellInfo.cellIdentity.arfcn, cellInfo.cellIdentity.mcc, cellInfo.cellIdentity.mnc, cellInfo.cellIdentity.psc, cellInfo.cellSignalStrength.dbm,cellInfo.cellIdentity.bsic ,cellInfo.cellIdentity.arfcn, cellSignalStrengthGSM = CellSignalStrengthGSM(null,cellInfo.cellSignalStrength.level,cellInfo.cellSignalStrength.dbm,cellInfo.cellSignalStrength.asuLevel,cellInfo.cellSignalStrength.bitErrorRate))
                } else {
                    CellInfoGSM(cellInfo.cellIdentity.cid,cellInfo.cellIdentity.arfcn, cellInfo.cellIdentity.mcc, cellInfo.cellIdentity.mnc, cellInfo.cellIdentity.psc, cellInfo.cellSignalStrength.dbm,cellInfo.cellIdentity.bsic ,cellInfo.cellIdentity.arfcn, cellSignalStrengthGSM = CellSignalStrengthGSM(null,cellInfo.cellSignalStrength.level,cellInfo.cellSignalStrength.dbm,cellInfo.cellSignalStrength.asuLevel,null))                }

            }

            is CellInfoCdma -> CellInfoCDMA(cellInfo.cellIdentity.basestationId, cellInfo.cellIdentity.latitude, cellInfo.cellIdentity.longitude, cellInfo.cellIdentity.networkId, cellInfo.cellIdentity.systemId, cellInfo.cellSignalStrength.dbm, cellSignalStrengthCDMA = CellSignalStrengthCDMA(cellInfo.cellSignalStrength.dbm,cellInfo.cellSignalStrength.level,cellInfo.cellSignalStrength.asuLevel,cellInfo.cellSignalStrength.evdoLevel,cellInfo.cellSignalStrength.cdmaDbm,cellInfo.cellSignalStrength.cdmaEcio,cellInfo.cellSignalStrength.cdmaLevel,cellInfo.cellSignalStrength.evdoEcio,cellInfo.cellSignalStrength.evdoLevel,cellInfo.cellSignalStrength.evdoSnr))
            is CellInfoLte -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    CellInfoLTE(cellInfo.cellIdentity.ci, cellInfo.cellIdentity.pci, cellInfo.cellIdentity.tac, cellInfo.cellIdentity.mcc, cellInfo.cellIdentity.mnc, cellInfo.cellSignalStrength.dbm,cellInfo.cellIdentity.earfcn,cellInfo.cellIdentity.bandwidth,cellInfo.cellIdentity.mobileNetworkOperator?:"", cellSignalStrengthLTE = CellSignalStrengthLTE(cellInfo.cellSignalStrength.dbm,cellInfo.cellSignalStrength.rssi,cellInfo.cellSignalStrength.level,cellInfo.cellSignalStrength.asuLevel,cellInfo.cellSignalStrength.cqi,cellInfo.cellSignalStrength.cqiTableIndex,cellInfo.cellSignalStrength.rsrq,cellInfo.cellSignalStrength.rssnr,cellInfo.cellSignalStrength.timingAdvance))
                } else {
                    CellInfoLTE(cellInfo.cellIdentity.ci, cellInfo.cellIdentity.pci, cellInfo.cellIdentity.tac, cellInfo.cellIdentity.mcc, cellInfo.cellIdentity.mnc, cellInfo.cellSignalStrength.dbm,cellInfo.cellIdentity.earfcn,cellInfo.cellIdentity.bandwidth,cellInfo.cellIdentity.mobileNetworkOperator?:"", cellSignalStrengthLTE = CellSignalStrengthLTE(cellInfo.cellSignalStrength.dbm,null,cellInfo.cellSignalStrength.level,cellInfo.cellSignalStrength.asuLevel,cellInfo.cellSignalStrength.cqi,null,cellInfo.cellSignalStrength.rsrq,cellInfo.cellSignalStrength.rssnr,cellInfo.cellSignalStrength.timingAdvance))
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    CellInfoLTE(cellInfo.cellIdentity.ci, cellInfo.cellIdentity.pci, cellInfo.cellIdentity.tac, cellInfo.cellIdentity.mcc, cellInfo.cellIdentity.mnc, cellInfo.cellSignalStrength.dbm,cellInfo.cellIdentity.earfcn,cellInfo.cellIdentity.bandwidth,cellInfo.cellIdentity.mobileNetworkOperator?:"", cellSignalStrengthLTE = CellSignalStrengthLTE(cellInfo.cellSignalStrength.dbm,cellInfo.cellSignalStrength.rssi,cellInfo.cellSignalStrength.level,cellInfo.cellSignalStrength.asuLevel,cellInfo.cellSignalStrength.cqi,null,cellInfo.cellSignalStrength.rsrq,cellInfo.cellSignalStrength.rssnr,cellInfo.cellSignalStrength.timingAdvance))

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        CellInfoLTE(cellInfo.cellIdentity.ci, cellInfo.cellIdentity.pci, cellInfo.cellIdentity.tac, cellInfo.cellIdentity.mcc, cellInfo.cellIdentity.mnc, cellInfo.cellSignalStrength.dbm,cellInfo.cellIdentity.earfcn,cellInfo.cellIdentity.bandwidth,cellInfo.cellIdentity.mobileNetworkOperator?:"", cellSignalStrengthLTE = CellSignalStrengthLTE(cellInfo.cellSignalStrength.dbm,null,cellInfo.cellSignalStrength.level,cellInfo.cellSignalStrength.asuLevel,cellInfo.cellSignalStrength.cqi,null,cellInfo.cellSignalStrength.rsrq,cellInfo.cellSignalStrength.rssnr,cellInfo.cellSignalStrength.timingAdvance))
                    } else {
                        CellInfoLTE(cellInfo.cellIdentity.ci, cellInfo.cellIdentity.pci, cellInfo.cellIdentity.tac, cellInfo.cellIdentity.mcc, cellInfo.cellIdentity.mnc, cellInfo.cellSignalStrength.dbm,cellInfo.cellIdentity.earfcn,null,"", cellSignalStrengthLTE = CellSignalStrengthLTE(cellInfo.cellSignalStrength.dbm,null,cellInfo.cellSignalStrength.level,cellInfo.cellSignalStrength.asuLevel,null,null,null,null,cellInfo.cellSignalStrength.timingAdvance))
                    }
                }
            }

            is CellInfoWcdma -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                CellInfoWCDMA(cellInfo.cellIdentity.cid, cellInfo.cellIdentity.lac, cellInfo.cellIdentity.mcc, cellInfo.cellIdentity.mnc, cellInfo.cellIdentity.psc, cellInfo.cellSignalStrength.dbm,cellInfo.cellIdentity.uarfcn, cellSignalStrengthWCDMA = CellSignalStrengthWCDMA(cellInfo.cellSignalStrength.dbm,cellInfo.cellSignalStrength.level,cellInfo.cellSignalStrength.asuLevel,cellInfo.cellSignalStrength.ecNo))
            } else {
                CellInfoWCDMA(cellInfo.cellIdentity.cid, cellInfo.cellIdentity.lac, cellInfo.cellIdentity.mcc, cellInfo.cellIdentity.mnc, cellInfo.cellIdentity.psc, cellInfo.cellSignalStrength.dbm,cellInfo.cellIdentity.uarfcn, cellSignalStrengthWCDMA = CellSignalStrengthWCDMA(cellInfo.cellSignalStrength.dbm,cellInfo.cellSignalStrength.level,cellInfo.cellSignalStrength.asuLevel,null))
            }

            else -> throw UnsupportedOperationException("Unsupported cell info type")
        }
    }
    val jsonString = json.encodeToString(cellInfoModule.serializer(),cellInfos)
    println( "jsonSTRING :  $jsonString")
    return  json.parseToJsonElement(jsonString).jsonArray


}
