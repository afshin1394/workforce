package irancell.nwg.wfm.network

import android.os.Build
import android.telephony.CellIdentityGsm
import android.telephony.CellIdentityLte
import android.telephony.CellIdentityWcdma
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


fun List<android.telephony.CellInfo>.toJson(): JsonObject {
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
                CellInfoGSM(
                    cellId = cellInfo.cellIdentity.cid,
                    arfcn = cellInfo.cellIdentity.arfcn,
                    mcc = (cellInfo.cellIdentity as? CellIdentityGsm)?.mccString.toString(),
                    mnc = (cellInfo.cellIdentity as? CellIdentityGsm)?.mncString.toString(),
                    psc = null,
                    bsic = cellInfo.cellIdentity.bsic,
                    cellSignalStrengthGSM = CellSignalStrengthGSM(
                        rssi = cellInfo.cellSignalStrength.rssi,
                        level = cellInfo.cellSignalStrength.level,
                        dbm = cellInfo.cellSignalStrength.dbm,
                        asuLevel = cellInfo.cellSignalStrength.asuLevel,
                        bitErrorRate = cellInfo.cellSignalStrength.bitErrorRate
                    ),
                    lac = cellInfo.cellIdentity.lac
                )
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    CellInfoGSM(
                        cellId = cellInfo.cellIdentity.cid,
                        arfcn = cellInfo.cellIdentity.arfcn,
                        mcc = (cellInfo.cellIdentity as? CellIdentityGsm)?.mccString.toString(),
                        mnc = (cellInfo.cellIdentity as? CellIdentityGsm)?.mncString.toString(),
                        psc = null,
                        bsic = cellInfo.cellIdentity.bsic,
                        cellSignalStrengthGSM = CellSignalStrengthGSM(
                            null,
                            level = cellInfo.cellSignalStrength.level,
                            dbm = cellInfo.cellSignalStrength.dbm,
                            asuLevel = cellInfo.cellSignalStrength.asuLevel,
                            bitErrorRate = cellInfo.cellSignalStrength.bitErrorRate
                        ),
                        lac = cellInfo.cellIdentity.lac
                    )
                } else {
                    CellInfoGSM(
                        cellId = cellInfo.cellIdentity.cid,
                        arfcn = cellInfo.cellIdentity.arfcn,
                        mcc = null,
                        mnc = null,
                        psc = null,
                        bsic = cellInfo.cellIdentity.bsic,
                        cellSignalStrengthGSM = CellSignalStrengthGSM(
                            rssi = null,
                            level = cellInfo.cellSignalStrength.level,
                            dbm = cellInfo.cellSignalStrength.dbm,
                            asuLevel = cellInfo.cellSignalStrength.asuLevel,
                            bitErrorRate = null
                        ),
                        lac = cellInfo.cellIdentity.lac
                    )
                }

            }

            is CellInfoCdma -> CellInfoCDMA(
                baseStationId = cellInfo.cellIdentity.basestationId,
                latitude = cellInfo.cellIdentity.latitude,
                longitude = cellInfo.cellIdentity.longitude,
                networkId = cellInfo.cellIdentity.networkId,
                systemId = cellInfo.cellIdentity.systemId,
                cellSignalStrengthCDMA = CellSignalStrengthCDMA(
                    dbm = cellInfo.cellSignalStrength.dbm,
                    level = cellInfo.cellSignalStrength.level,
                    asuLevel = cellInfo.cellSignalStrength.asuLevel,
                    cdmaDBM = cellInfo.cellSignalStrength.cdmaDbm,
                    cdmaEcio = cellInfo.cellSignalStrength.cdmaEcio,
                    cdmaLevel = cellInfo.cellSignalStrength.cdmaLevel,
                    evdoEcio = cellInfo.cellSignalStrength.evdoEcio,
                    evdoLevel = cellInfo.cellSignalStrength.evdoLevel,
                    evdoSnr = cellInfo.cellSignalStrength.evdoSnr,
                    evdoDbm = cellInfo.cellSignalStrength.evdoDbm
                )
            )

            is CellInfoLte -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    CellInfoLTE(
                        ci = cellInfo.cellIdentity.ci,
                        pci = cellInfo.cellIdentity.pci,
                        tac = cellInfo.cellIdentity.tac,
                        mcc = (cellInfo.cellIdentity as? CellIdentityLte)?.mccString.toString(),
                        mnc = (cellInfo.cellIdentity as? CellIdentityLte)?.mncString.toString(),
                        erfcn = cellInfo.cellIdentity.earfcn,
                        bandwidth = cellInfo.cellIdentity.bandwidth,
                        mobileNetworkOperator = cellInfo.cellIdentity.mobileNetworkOperator ?: "",
                        cellSignalStrengthLTE = CellSignalStrengthLTE(
                            dbm = cellInfo.cellSignalStrength.dbm,
                            rssi = cellInfo.cellSignalStrength.rssi,
                            level = cellInfo.cellSignalStrength.level,
                            asuLevel = cellInfo.cellSignalStrength.asuLevel,
                            cqi = cellInfo.cellSignalStrength.cqi,
                            cqiTableIndex = cellInfo.cellSignalStrength.cqiTableIndex,
                            rsrq = cellInfo.cellSignalStrength.rsrq,
                            rssnr = cellInfo.cellSignalStrength.rssnr,
                            timingAdvance = cellInfo.cellSignalStrength.timingAdvance
                        )
                    )
                } else {
                    CellInfoLTE(
                        ci = cellInfo.cellIdentity.ci,
                        pci = cellInfo.cellIdentity.pci,
                        tac = cellInfo.cellIdentity.tac,
                        mcc = (cellInfo.cellIdentity as? CellIdentityLte)?.mccString.toString(),
                        mnc = (cellInfo.cellIdentity as? CellIdentityLte)?.mncString.toString(),
                        erfcn = cellInfo.cellIdentity.earfcn,
                        bandwidth = cellInfo.cellIdentity.bandwidth,
                        mobileNetworkOperator = cellInfo.cellIdentity.mobileNetworkOperator ?: "",
                        cellSignalStrengthLTE = CellSignalStrengthLTE(
                            dbm = cellInfo.cellSignalStrength.dbm,
                            rssi = cellInfo.cellSignalStrength.rssi,
                            level = cellInfo.cellSignalStrength.level,
                            asuLevel = cellInfo.cellSignalStrength.asuLevel,
                            cqi = cellInfo.cellSignalStrength.cqi,
                            cqiTableIndex = cellInfo.cellSignalStrength.cqiTableIndex,
                            rsrq = cellInfo.cellSignalStrength.rsrq,
                            rssnr = cellInfo.cellSignalStrength.rssnr,
                            timingAdvance = cellInfo.cellSignalStrength.timingAdvance
                        )
                    )
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    CellInfoLTE(
                        ci = cellInfo.cellIdentity.ci,
                        pci = cellInfo.cellIdentity.pci,
                        tac = cellInfo.cellIdentity.tac,
                        mcc = (cellInfo.cellIdentity as? CellIdentityLte)?.mccString.toString(),
                        mnc = (cellInfo.cellIdentity as? CellIdentityLte)?.mncString.toString(),
                        erfcn = cellInfo.cellIdentity.earfcn,
                        bandwidth = cellInfo.cellIdentity.bandwidth,
                        mobileNetworkOperator = cellInfo.cellIdentity.mobileNetworkOperator ?: "",
                        cellSignalStrengthLTE = CellSignalStrengthLTE(
                            dbm = cellInfo.cellSignalStrength.dbm,
                            rssi = cellInfo.cellSignalStrength.rssi,
                            level = cellInfo.cellSignalStrength.level,
                            asuLevel = cellInfo.cellSignalStrength.asuLevel,
                            cqi = cellInfo.cellSignalStrength.cqi,
                            cqiTableIndex = null,
                            rsrq = cellInfo.cellSignalStrength.rsrq,
                            rssnr = cellInfo.cellSignalStrength.rssnr,
                            timingAdvance = cellInfo.cellSignalStrength.timingAdvance
                        )
                    )

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        CellInfoLTE(
                            ci = cellInfo.cellIdentity.ci,
                            pci = cellInfo.cellIdentity.pci,
                            tac = cellInfo.cellIdentity.tac,
                            mcc = (cellInfo.cellIdentity as? CellIdentityLte)?.mccString.toString(),
                            mnc = (cellInfo.cellIdentity as? CellIdentityLte)?.mncString.toString(),
                            erfcn = cellInfo.cellIdentity.earfcn,
                            bandwidth = cellInfo.cellIdentity.bandwidth,
                            mobileNetworkOperator = cellInfo.cellIdentity.mobileNetworkOperator
                                ?: "",
                            cellSignalStrengthLTE = CellSignalStrengthLTE(
                                dbm = cellInfo.cellSignalStrength.dbm,
                                rssi = null,
                                level = cellInfo.cellSignalStrength.level,
                                asuLevel = cellInfo.cellSignalStrength.asuLevel,
                                cqi = cellInfo.cellSignalStrength.cqi,
                                cqiTableIndex = null,
                                rsrq = cellInfo.cellSignalStrength.rsrq,
                                rssnr = cellInfo.cellSignalStrength.rssnr,
                                timingAdvance = cellInfo.cellSignalStrength.timingAdvance
                            )
                        )
                    } else {
                        CellInfoLTE(
                            ci = cellInfo.cellIdentity.ci,
                            pci = cellInfo.cellIdentity.pci,
                            tac = cellInfo.cellIdentity.tac,
                            mcc = null,
                            mnc = null,
                            erfcn = cellInfo.cellIdentity.earfcn,
                            bandwidth = null,
                            mobileNetworkOperator = "",
                            cellSignalStrengthLTE = CellSignalStrengthLTE(
                                dbm = cellInfo.cellSignalStrength.dbm,
                                rssi = null,
                                level = cellInfo.cellSignalStrength.level,
                                asuLevel = cellInfo.cellSignalStrength.asuLevel,
                                cqi = null,
                                cqiTableIndex = null,
                                rsrq = null,
                                rssnr = null,
                                timingAdvance = cellInfo.cellSignalStrength.timingAdvance
                            )
                        )
                    }
                }
            }

            is CellInfoWcdma -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                CellInfoWCDMA(
                    cid = cellInfo.cellIdentity.cid,
                    lac = cellInfo.cellIdentity.lac,
                    mcc = (cellInfo.cellIdentity as? CellIdentityWcdma)?.mccString.toString(),
                    mnc = (cellInfo.cellIdentity as? CellIdentityWcdma)?.mncString.toString(),
                    psc = cellInfo.cellIdentity.psc,
                    urfcn = cellInfo.cellIdentity.uarfcn,
                    cellSignalStrengthWCDMA = CellSignalStrengthWCDMA(
                        dbm = cellInfo.cellSignalStrength.dbm,
                        level = cellInfo.cellSignalStrength.level,
                        asulevel = cellInfo.cellSignalStrength.asuLevel,
                        ecNo = cellInfo.cellSignalStrength.ecNo
                    )
                )
            } else {
                CellInfoWCDMA(
                    cid = cellInfo.cellIdentity.cid,
                    lac = cellInfo.cellIdentity.lac,
                    mcc = null,
                    mnc = null,
                    psc = cellInfo.cellIdentity.psc,
                    urfcn = cellInfo.cellIdentity.uarfcn,
                    cellSignalStrengthWCDMA = CellSignalStrengthWCDMA(
                        dbm = cellInfo.cellSignalStrength.dbm,
                        level = cellInfo.cellSignalStrength.level,
                        asulevel = cellInfo.cellSignalStrength.asuLevel,
                        ecNo = null
                    )
                )
            }

            else -> throw UnsupportedOperationException("Unsupported cell info type")
        }
    }
    val jsonString = json.encodeToString(cellInfoModule.serializer(), cellInfos)
    val jsonArray = Json.decodeFromString<JsonArray>(jsonString)
    val jsonObject = JsonObject(mapOf("info" to jsonArray))
    val wrappedJsonString = Json.encodeToString(JsonObject.serializer(), jsonObject)

    return json.parseToJsonElement(wrappedJsonString).jsonObject


}
