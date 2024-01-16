import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.Android.App
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
actual class Location actual constructor(update : (GeneralLocation) -> Unit){
    var isGPSEnabled = false
    var isNetworkEnabled = false
    var canGetLocation = false
    val context = App.INSTANCE
    var _locationManager: LocationManager? = null
    val locationManager get() = _locationManager


    init {

        val locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                location.also {
                    if (it.latitude.toString().length > 12)
                        it.latitude = it.latitude.toString().substring(0, 12).toDouble()
                    if (it.longitude.toString().length > 12)
                        it.longitude = it.longitude.toString().substring(0, 12).toDouble()

                    update(GeneralLocation(it.latitude,it.longitude))

                }
            }


            override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
            override fun onProviderEnabled(s: String) {}
            override fun onProviderDisabled(s: String) {}
        }
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)


            fusedLocationClient.getCurrentLocation(
                com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                        CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                })
                .addOnSuccessListener { location: android.location.Location? ->
                    location?.let {
                        Napier.log(LogLevel.ASSERT,"locationCheck", message = "\n"+it.latitude.toString()+"-"+it.longitude.toString()+"\n")
                        location.also {
                            if (it.latitude.toString().length > 12)
                                it.latitude = it.latitude.toString().substring(0, 12).toDouble()
                            if (it.longitude.toString().length > 12)
                                it.longitude = it.longitude.toString().substring(0, 12).toDouble()

                            update(GeneralLocation(it.latitude,it.longitude))

                        }

//                        _location?.let { _location ->
//                            latitude = _location.getLatitude()
//                            longitude = _location.getLongitude()
//                            altitude = _location.getAltitude()
//                            accurancy = _location.getAccuracy()
//                            bearing = _location.getBearing()
//                            speed = _location.getSpeed()
//                            time = _location.time
//                        }
                    }


                }

            _locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            isGPSEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
            isNetworkEnabled =
                locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
            if (isGPSEnabled || isNetworkEnabled) {
                canGetLocation = true
                if (isGPSEnabled) {

                    if (context.checkSelfPermission(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                            2000
                        )
                    }

                    locationManager?.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        3000,
                        0f,
                        locationListener
                    )


                    val lastLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.also {
                        Napier.log(LogLevel.ASSERT,"locationCheck", message = "\n"+it.latitude.toString()+"-"+it.longitude.toString()+"\n")

                        if (it.latitude.toString().length > 12)
                            it.latitude = it.latitude.toString().substring(0, 12).toDouble()
                        if (it.longitude.toString().length > 12)
                            it.longitude = it.longitude.toString().substring(0, 12).toDouble()
                    }
                    lastLocation?.let {
                        it.also {
                            if (it.latitude.toString().length > 12)
                                it.latitude = it.latitude.toString().substring(0, 12).toDouble()
                            if (it.longitude.toString().length > 12)
                                it.longitude = it.longitude.toString().substring(0, 12).toDouble()

                            update(GeneralLocation(it.latitude,it.longitude))

                        }

                    }
//                    _location?.let { _location ->
//                        latitude = _location.getLatitude()
//                        longitude = _location.getLongitude()
//                        altitude = _location.getAltitude()
//                        accurancy = _location.getAccuracy()
//                        bearing = _location.getBearing()
//                        speed = _location.getSpeed()
//                        time = _location.time
//                    }


                }
                if (isNetworkEnabled) {
                    if (context.checkSelfPermission(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                            2000
                        )
                    }
                    locationManager?.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        60000,
                        3f,
                        locationListener
                    )


                    val lastLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.also {
                        if (it.latitude.toString().length > 12)
                            it.latitude = it.latitude.toString().substring(0, 12).toDouble()
                        if (it.longitude.toString().length > 12)
                            it.longitude = it.longitude.toString().substring(0, 12).toDouble()
                    }
                    lastLocation?.let {
                        lastLocation.also {
                            if (it.latitude.toString().length > 12)
                                it.latitude = it.latitude.toString().substring(0, 12).toDouble()
                            if (it.longitude.toString().length > 12)
                                it.longitude = it.longitude.toString().substring(0, 12).toDouble()

                            update(GeneralLocation(it.latitude,it.longitude))

                        }

                    }

//                    _location?.let { _location ->
//                        latitude = _location.getLatitude()
//                        longitude = _location.getLongitude()
//                        altitude = _location.getAltitude()
//                        accurancy = _location.getAccuracy()
//                        bearing = _location.getBearing()
//                        speed = _location.getSpeed()
//                        time = _location.time
//                    }


                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}