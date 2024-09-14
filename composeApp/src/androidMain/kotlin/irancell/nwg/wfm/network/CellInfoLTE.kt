package irancell.nwg.wfm.network

import kotlinx.serialization.Serializable


@Serializable
data class CellInfoLTE(val ci: Int, val pci: Int, val tac: Int, val mcc: String?, val mnc: String?, val erfcn : Int,val bandwidth : Int?,val mobileNetworkOperator: String,val cellSignalStrengthLTE: CellSignalStrengthLTE ) : CellInfo()
@Serializable
data class CellSignalStrengthLTE(val dbm : Int?,val rssi : Int?,val level : Int?,val asuLevel : Int?,val cqi : Int?,val cqiTableIndex : Int?,val rsrq : Int?,val rssnr : Int?,val timingAdvance : Int?)

