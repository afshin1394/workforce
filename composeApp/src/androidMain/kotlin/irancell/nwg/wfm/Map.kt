package irancell.nwg.wfm

import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import irancell.nwg.wfm.Android.App
import irancell.nwg.wfm.db.GeneralLocation


@Composable
actual fun mapView(generalLocations : List<GeneralLocation>){
   val context = LocalContext.current


    val intent = Intent(context,MapActivity::class.java)


    context.startActivity(intent)


//    val mapbox = MapboxMap(
//        Modifier.fillMaxSize(),
//
//        mapViewportState = MapViewportState().apply {
//
//            setCameraOptions {
//                zoom(4.0)
//                center(Point.fromLngLat(51.463504, 35.776380))
//                pitch(0.0)
//                bearing(0.0)
//
//
//            }
//
//
//        },
//    )







}




