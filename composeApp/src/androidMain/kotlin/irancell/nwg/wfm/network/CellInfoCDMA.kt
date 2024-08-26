package irancell.nwg.wfm.network

import kotlinx.serialization.Serializable

@Serializable
data class CellInfoCDMA(val baseStationId: Int, val latitude: Int, val longitude: Int, val networkId: Int, val systemId: Int, val cellSignalStrengthCDMA: CellSignalStrengthCDMA) : CellInfo()
@Serializable
data class CellSignalStrengthCDMA(val dbm : Int,val level : Int,val asuLevel : Int,val evdoDbm : Int,val cdmaDBM : Int,val cdmaEcio : Int,val cdmaLevel : Int,val evdoEcio : Int,val evdoLevel : Int,val evdoSnr: Int)