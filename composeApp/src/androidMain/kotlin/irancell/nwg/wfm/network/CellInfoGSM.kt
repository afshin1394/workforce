package irancell.nwg.wfm.network

import kotlinx.serialization.Serializable
import java.io.Serial

@Serializable
data class CellInfoGSM(val cellId: Int, val lac: Int, val mcc: Int, val mnc: Int, val psc: Int, val signalStrength: Int,val bsic : Int,val arfcn: Int,val cellSignalStrengthGSM: CellSignalStrengthGSM) : CellInfo()

@Serializable
data class CellSignalStrengthGSM(val rssi : Int?,val level : Int?,val dbm : Int?,val asuLevel : Int?,val bitErrorRate : Int?)