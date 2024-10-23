package irancell.nwg.wfm

import App
import android.content.Context
import android.content.pm.PackageInfo
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import io.sentry.Sentry
import org.koin.core.context.stopKoin
import utils.Language
import utils.ModeApp
import utils.ORIENTATION
import java.util.Locale

class MainActivity : FragmentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var rotationSensor: Sensor? = null
    private var isPortrait = true  // این متغیر وضعیت Portrait را نگه می‌دارد
    private var canOpenDrawer = true  //
    override fun attachBaseContext(newBase: Context?) {

        if(getSharedPref().getString(ModeApp)==""||getSharedPref().getString(ModeApp)==null){
            getSharedPref().put(ModeApp, "on")
        }
        if(getSharedPref().getString(Language)==""||getSharedPref().getString(Language)==null){
            getSharedPref().put(Language, Locale.getDefault().language)
        }
        updateConfig(this)
        super.attachBaseContext(newBase)
    }



    /*    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if ( getSharedPref().getString(Language)=="fa"){
            getSharedPref().put(Language, "en")
        }else{
            getSharedPref().put(Language, "fa")
        }

    }*/



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val packageInfo: PackageInfo =
            (provideAppContext() as Context).packageManager.getPackageInfo((provideAppContext() as Context).packageName, 0)

        Sentry.init { options ->
            options.dsn = "https://c38def8cf951033c69c3c1cdfe6ec3e7@o4505880661065728.ingest.us.sentry.io/4507055751036928"
            options.environment = "production"
            options.release = packageInfo.versionName // Set the release version
        }
        setContent {
          App()
        }
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // تغییر جهت صفحه را تشخیص دهید
        when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                isPortrait = false
                canOpenDrawer = false  // در لحظه تغییر، باز شدن دراور را غیر فعال کنید

                getSharedPref().put(ORIENTATION, canOpenDrawer)
                resetDrawerFlagWithDelay()  // بعد از تاخیر، مجدداً باز شدن دراور را فعال کنید
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                isPortrait = true
                canOpenDrawer = false  // در لحظه تغییر، باز شدن دراور را غیر فعال کنید

                getSharedPref().put(ORIENTATION, canOpenDrawer)
                resetDrawerFlagWithDelay()  // بعد از تاخیر، مجدداً باز شدن دراور را فعال کنید
            }
        }
    }


    private fun resetDrawerFlagWithDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            canOpenDrawer = true  // بعد از مدتی کوتاه، دوباره اجازه باز شدن دراور را بدهید
            getSharedPref().put(ORIENTATION, canOpenDrawer)
        }, 500)  // 500 میلی‌ثانیه یا هر زمانی که بخواهید
    }
    override fun onDestroy() {
        super.onDestroy()
        stopKoin()
    }


    override fun onResume() {
        super.onResume()
        rotationSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            val orientationAngles = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            // محاسبه pitch برای تشخیص افقی یا عمودی بودن دستگاه
            val pitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()

            // وقتی در حالت عمودی هستیم و در حال چرخش به افقی هستیم
            if (isPortrait && (pitch > -10 && pitch < 10)) {
                // تغییر از portrait به landscape
                isPortrait = false // به‌روزرسانی وضعیت
            } else if (!isPortrait && (pitch < -80 || pitch > 80)) {
                // تغییر از landscape به portrait
                isPortrait = true // به‌روزرسانی وضعیت
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

}


@Preview
@Composable
fun AppAndroidPreview() {
    App()
}