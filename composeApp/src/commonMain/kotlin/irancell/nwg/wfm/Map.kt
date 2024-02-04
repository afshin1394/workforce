package irancell.nwg.wfm

import androidx.compose.runtime.Composable
import irancell.nwg.wfm.db.GeneralLocationEntity

@Composable

expect fun mapView(generalLocations : List<GeneralLocationEntity>)