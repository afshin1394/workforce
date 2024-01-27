package irancell.nwg.wfm

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.CompoundButton
import android.widget.Switch
import androidx.fragment.app.FragmentActivity
import com.google.android.material.materialswitch.MaterialSwitch
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline
import presentation.screens.main.compose.GpsTrackingReportScreen
import presentation.screens.main.compose.GpsTrackingReportScreen.Companion.gneralLocs


class MapActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        Napier.log(
            LogLevel.ASSERT,
            tag = "sdaddadadad",
            message = GpsTrackingReportScreen.gneralLocs.size.toString()
        )
        setContentView(R.layout.activity_map)
        val mapView = findViewById<org.osmdroid.views.MapView>(R.id.mapView)

        mapView.setMultiTouchControls(true);
        mapView.getTileProvider().clearTileCache();
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        val mapController: IMapController = MapController(mapView)
        val geoPoint = GeoPoint(
            GpsTrackingReportScreen.gneralLocs[gneralLocs.size/2].longitude.toDouble(),
            GpsTrackingReportScreen.gneralLocs[gneralLocs.size/2].latitude.toDouble()
        )
        mapController.setZoom(16.0);
        mapController.setCenter(geoPoint)
        mapController.animateTo(geoPoint)
        val switch = findViewById<Switch>(R.id.switchRoute)
        showPins(this@MapActivity, mapView)

        switch.setOnCheckedChangeListener { p0, p1 ->
            if (p1) {

                drawLine(mapView)
            } else {
                showPins(this@MapActivity, mapView)
            }
        }


    }


}

fun showPins(context: Context, mapView: MapView) {
    mapView.getOverlays().clear();
    mapView.invalidate();
    for (mapObjectModel in GpsTrackingReportScreen.gneralLocs) {
        val marker = Marker(mapView)
        if (mapObjectModel.longitude.toDouble() != 0.0 && mapObjectModel.latitude.toDouble() != 0.0)
            marker.setPosition(
                GeoPoint(
                    mapObjectModel.longitude.toDouble(),
                    mapObjectModel.latitude.toDouble(),
                )
            )
        marker.setIcon(context.resources.getDrawable(org.osmdroid.library.R.drawable.marker_default))
        marker.setTitle("")
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.setInfoWindow(null)

        Log.i("adding", "showDestinationPositions: " + marker.getPosition())
        if (mapView.getOverlays().contains(marker)) mapView.getOverlays().remove(marker)
        mapView.getOverlays().add(marker)
    }
    mapView.invalidate()
}

fun drawLine(mapView: MapView) {
    mapView.getOverlays().clear();
    mapView.invalidate();
    val points: ArrayList<GeoPoint> = arrayListOf()
    for (gneralLoc in gneralLocs) {
        if (gneralLoc.longitude.toDouble() != 0.0 && gneralLoc.latitude.toDouble() != 0.0) {
            points.add(GeoPoint(gneralLoc.longitude.toDouble(), gneralLoc.latitude.toDouble()))
        }
    }
    val polyline = Polyline()
    polyline.setPoints(points)
    polyline.color = com.google.android.material.R.color.mtrl_error
    mapView.getOverlayManager().add(polyline)
    polyline.infoWindow = null


    val overlays = ArrayList<Overlay>()
    overlays.add(polyline)
    mapView.invalidate()
}


