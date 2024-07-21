package irancell.nwg.wfm

import androidx.compose.runtime.Composable
import database.entity.GeneralLocationEntity


@Composable

expect fun mapView(generalLocations : List<GeneralLocationEntity>)