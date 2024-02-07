package irancell.nwg.wfm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import domain.usecase.usecase.location.SendLocationToServerUseCase
import domain.usecase.usecase.location.StoreLocationDataUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.db.GeneralLocationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.stopKoin
import utils.AsyncStatus
import utils.getCurrentDate
import java.util.concurrent.TimeUnit

actual class GpsTrackingService : Service() , KoinComponent {


    lateinit var pendingIntent: PendingIntent
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    val storeLocationDataUseCase : StoreLocationDataUseCase by inject()
    val sendLocationToServerUseCase : SendLocationToServerUseCase by inject()

   actual companion object {
        lateinit var gpsTrackingIntent : Intent

        const val Notification_ID = 123
        const val CHANNEL_ID = "GPS TRACKER"
        var isRunning: Boolean = false
        actual fun stopLocationTracker(){
            Napier.log(
                LogLevel.ASSERT,
                tag = "serviice",
                message = (provideAppContext() as Context).toString()
            )

            if (isRunning)
                (provideAppContext() as Context).stopService(gpsTrackingIntent)

            stopKoin()
        }


        actual fun startLocationTracker() {
            Napier.log(
                LogLevel.ASSERT,
                tag = "serviice",
                message = (provideAppContext() as Context).toString()
            )

            gpsTrackingIntent =
                Intent((provideAppContext() as Context), GpsTrackingService::class.java)
            if (!isRunning) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    (provideAppContext() as Context).startForegroundService(gpsTrackingIntent)
                } else {
                    (provideAppContext() as Context).startService(gpsTrackingIntent)
                }
            }
        }
    }



    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "init")
        super.onCreate()
        isRunning = true
        val notification =
            createNotification(applicationContext, "Gps Tracking On", "retrieving gps data")
        val intent = Intent(this, GpsTrackingService::class.java)
        startForeground(Notification_ID, notification);
        GlobalScope.launch {

        Location.start() {
                Log.i("locationServicess", "onCreate:  latitude:" + it.latitude)
                lat = it.latitude.toDouble()
                lon = it.longitude.toDouble()
            }

        }

        pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }


    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "scope")

        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            Napier.log(LogLevel.ASSERT, tag = "serviice", message = "Launch")

            storeLocationDataUseCase(
                GeneralLocationEntity(
                    lat.toString(),
                    lon.toString(),
                    getCurrentDate(),
                    0
                )
            ).collect{
                when(it.status){
                    AsyncStatus.ERROR -> {
                        val errorMessage = it.message!!
                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "serviice",
                            message = it.message
                        )
                    }
                    AsyncStatus.LOADING -> {
                    }
                    AsyncStatus.SUCCESS -> {
                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "serviice",
                            message = "success"
                        )
                    }
                }
            }
            Napier.log(
                LogLevel.ASSERT,
                tag = "serviice",
                message = sendLocationToServerUseCase.toString()
            )
            sendLocationToServerUseCase(
                GeneralLocationEntity(
                    lat.toString(),
                    lon.toString(),
                    getCurrentDate(),
                    0
                )
            ).collect{
                when(it.status){
                    AsyncStatus.ERROR -> {
                        val errorMessage = it.message!!
                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "serviice",
                            message = it.message
                        )
                    }
                    AsyncStatus.LOADING -> {
                    }
                    AsyncStatus.SUCCESS -> {
                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "serviice",
                            message = "success"
                        )
                    }
                }
            }



//            Log.i("locationServicess", "onStartCommand:  latitude:" + lat + "longitude" + lon)
//            isWifi(this@SendGpsService).let {
//                if (it) {
//            val response =
//                sendLocationService.sendLocation(SendLocationRequest(lon, lat)).execute()
//                }
//            }



            startAlarm()
            scope.cancel()
        }


        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        cancelAlarm()
        stopSelf()
        isRunning = false
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun startAlarm() {
        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "Alarm")

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val currentTimeMillis = SystemClock.elapsedRealtime()
        val intervalMillis = TimeUnit.SECONDS.toMillis(5)
        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "Alarm lat: $lat  + lon: $lon" )

        alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            currentTimeMillis + intervalMillis,
            pendingIntent
        )
    }

    private fun cancelAlarm() {
        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "CancelAlarm")

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    @SuppressLint("MissingPermission")
    fun createNotification(
        context: Context,
        title: String,
        content: String,
    ): Notification {
        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "CreateNotificcation")

        createNotificationChannel(title, content)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_sdm)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notification = builder.build()
        with(NotificationManagerCompat.from(this)) {

            Log.i("notification", "createNotification: ")
            notify(Notification_ID, notification)
            Log.i("notification", "createNotification  accepted: ")


        }
        Napier.log(LogLevel.ASSERT, tag = "serviice", message = Notification_ID.toString())

        return notification
    }


    private fun createNotificationChannel(name: String, content: String) {
        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "createNotificationChannel")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Napier.log(
                LogLevel.ASSERT,
                tag = "serviice",
                message = "Build.VERSION.SDK_INT >= Build.VERSION_CODES.O"
            )

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = content
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Napier.log(LogLevel.ASSERT, tag = "serviice", message = "notificationManager")

        }
    }


}