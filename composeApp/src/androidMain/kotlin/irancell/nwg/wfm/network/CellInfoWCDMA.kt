package irancell.nwg.wfm.network

import kotlinx.serialization.Serializable


@Serializable
data class CellInfoWCDMA(val cid: Int, val lac: Int, val mcc: String?, val mnc: String?, val psc: Int, val urfcn : Int,val cellSignalStrengthWCDMA : CellSignalStrengthWCDMA) : CellInfo()
@Serializable
data class CellSignalStrengthWCDMA(val dbm : Int,val level : Int,val asulevel : Int,val ecNo : Int? )

