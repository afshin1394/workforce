package irancell.nwg.wfm

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import database.entity.GeneralLocationEntity
import domain.models.LiveLocationDomain
import domain.usecase.ResultStatus

import domain.usecase.usecase.location.SendLocationToServerUseCase
import domain.usecase.usecase.location.StoreLocationDataUseCase
import domain.usecase.usecase.steps.UpdateStepsUseCase
import domain.usecase.usecase.ticket.UpdateTaskUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import utils.AlarmAction
import utils.AsyncStatus
import utils.AvailabilityObjectId
import utils.ServiceState
import utils.TicketNumber
import utils.ViewStates
import utils.getCurrentDate
import java.util.concurrent.TimeUnit


internal actual class BackgroundServiceApp : Service(), KoinComponent {

    val storeLocationDataUseCase: StoreLocationDataUseCase by inject()
    val sendLocationToServerUseCase: SendLocationToServerUseCase by inject()
    val updateTaskUseCase: UpdateTaskUseCase by inject()
    val updateStepsUseCase: UpdateStepsUseCase by inject()

    private lateinit var pendingIntentUpdate: PendingIntent
    private lateinit var pendingIntentStoreLocation: PendingIntent
    private lateinit var pendingIntentSendLocation: PendingIntent
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private val REQUEST_CODE_1 = 1
    private val REQUEST_CODE_2 = 2
    private val REQUEST_CODE_3 = 3


    actual companion object {
        private val _serviceState: MutableStateFlow<ServiceState> =
            MutableStateFlow(ServiceState.Normal)

        actual fun updateServiceState(serviceState: ServiceState) {
            _serviceState.update { serviceState }
        }


        val gpsTrackingIntent: Intent by lazy {
            Intent((provideAppContext() as Context), BackgroundServiceApp::class.java)
        }

        const val Notification_ID = 123
        const val CHANNEL_ID = "GPS TRACKER"
        actual fun stopBackgroundService() {

            /*if (getSharedPref().getBool(isRunningGPS, false))*/
            (provideAppContext() as Context).stopService(gpsTrackingIntent)

        }

        fun isServiceRunning(): Boolean {
            val activityManager =
                (provideAppContext() as Context).getSystemService(ACTIVITY_SERVICE) as ActivityManager
            for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
                if (BackgroundServiceApp::class.java.name == service.service.className) {
                    return true
                }
            }
            return false
        }

        actual fun startBackgroundService() {
            if (!isServiceRunning()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    (provideAppContext() as Context).startForegroundService(gpsTrackingIntent)
                } else {
                    (provideAppContext() as Context).startService(gpsTrackingIntent)
                }
            }
        }


        actual val serviceState: MutableStateFlow<ServiceState>
            get() = _serviceState


    }


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        /*getSharedPref().put(isRunningGPS, true)*/
        val notification =
            createNotification(applicationContext, "Gps Tracking On", "retrieving gps data")
        val intent1 = Intent(this, BackgroundServiceApp::class.java)
        intent1.setAction(AlarmAction.UPDATE)

        val intent2 = Intent(this, BackgroundServiceApp::class.java)
        intent2.setAction(AlarmAction.STORE_LOCATION)


        val intent3 = Intent(this, BackgroundServiceApp::class.java)
        intent3.setAction(AlarmAction.SEND_LOCATION)


        startForeground(Notification_ID, notification);

        Location.start() {
            GlobalScope.launch {
                lat = it.latitude.toDouble()
                lon = it.longitude.toDouble()
            }

        }

        pendingIntentUpdate =
            PendingIntent.getService(this, REQUEST_CODE_1, intent1, PendingIntent.FLAG_IMMUTABLE)
        pendingIntentStoreLocation =
            PendingIntent.getService(this, REQUEST_CODE_2, intent2, PendingIntent.FLAG_IMMUTABLE)
        pendingIntentSendLocation =
            PendingIntent.getService(this, REQUEST_CODE_3, intent3, PendingIntent.FLAG_IMMUTABLE)
    }


    override fun onBind(intent: Intent?): IBinder? {

        return null
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var scope = CoroutineScope(Dispatchers.Main)

        println("checkGpsPer${isLocationEnabled()}")
        println("checkGpsPerAction${intent?.action}")

        val telephonyData = TelephonyDataImpl(this)
        val data = telephonyData.getTelephonyData()

        println("checkGpsPerAction$data")



        try {
            scope.launch(Dispatchers.Main) {
                println("checkGpsPerAction in${intent?.action}")

                when (intent?.action) {


                    AlarmAction.SEND_LOCATION -> {

//                        sendLocationToServer()

                    }

                    AlarmAction.STORE_LOCATION -> {

                        storeLocation(data)

                    }

                    AlarmAction.UPDATE -> {

                        if (_serviceState.value != ServiceState.Suspend) {
                            updateTask()
                        }
                    }
                }


                startAlarm()

            }

        } catch (e: Exception) {

        }


        return START_STICKY
    }

    private suspend fun BackgroundServiceApp.storeLocation(data : JsonArray) {
        storeLocationDataUseCase(
            GeneralLocationEntity(
                latitude = lat.toString(),
                longitude = lon.toString(),
                datetime = getCurrentDate(),
                isSent = 0,
                networkInfo = Json.encodeToString(JsonArray.serializer(),data)
            )
        ).collect {
            when (it.status) {
                AsyncStatus.ERROR -> {
                    val errorMessage = it.message!!
                    println("checkGpsPerAction storeLocationDataUseCase Error")

                }

                AsyncStatus.EMPTY -> {

                }

                AsyncStatus.LOADING -> {
                }

                AsyncStatus.SUCCESS -> {
                    println("checkGpsPerAction sustoreLocationDataUseCaseccess")

                }
            }
        }
    }

    private suspend fun BackgroundServiceApp.sendLocationToServer() {
        sendLocationToServerUseCase(
            Unit
        ).collect {
            when (it.status) {
                AsyncStatus.ERROR -> {
                    if (it.resultStatus is ResultStatus.CLIENT_EXCEPTION.UNATHORIZED || it.resultStatus is ResultStatus.CLIENT_EXCEPTION.FORBIDDEN)
                        _serviceState.update { ServiceState.Faulty }

                }

                AsyncStatus.LOADING -> {
                }

                AsyncStatus.EMPTY -> {
                }

                AsyncStatus.SUCCESS -> {

                }
            }
        }
    }

    private suspend fun updateTask() {
        updateTaskUseCase(Unit)
            .collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        if (it.resultStatus is ResultStatus.CLIENT_EXCEPTION.UNATHORIZED || it.resultStatus is ResultStatus.CLIENT_EXCEPTION.FORBIDDEN)
                            _serviceState.update { ServiceState.Faulty }
                        println("TaskCallApi${"ERROR"}")
                    }

                    AsyncStatus.LOADING -> {
                    }

                    AsyncStatus.EMPTY -> {

                    }

                    AsyncStatus.SUCCESS -> {
                        updateSteps()
                        println("TaskCallApi${"SUCCESS"}")
                        Log.i("getAllTask", "onStartCommand: CallApi" + it.data)
                    }
                }
            }
    }

    private suspend fun updateSteps() {
        updateStepsUseCase(Unit).collect {
            when (it.status) {
                AsyncStatus.ERROR -> {


                    Napier.log(
                        LogLevel.ASSERT,
                        "updateSteps",
                        message = "ERROR: " + it.message
                    )

                }

                AsyncStatus.EMPTY -> {

                }

                AsyncStatus.LOADING -> {
                    Napier.log(LogLevel.ASSERT, "updateSteps", message = "LOADING: ")

                }

                AsyncStatus.SUCCESS -> {
                    _serviceState.update { ServiceState.Normal }


                    Napier.log(
                        LogLevel.ASSERT,
                        "" +
                                "",
                        message = "SUCCESS: " + it.data
                    )


                }


            }
        }


    }


    @SuppressLint("ScheduleExactAlarm")
    private fun startAlarm() {

        val alarmManager1 = getSystemService(ALARM_SERVICE) as AlarmManager
        val alarmManager2 = getSystemService(ALARM_SERVICE) as AlarmManager
        val alarmManager3 = getSystemService(ALARM_SERVICE) as AlarmManager

        val currentTimeMillis = SystemClock.elapsedRealtime()
        val intervalMillis1 = TimeUnit.MILLISECONDS.toMillis(1000)
        val intervalMillis2 = TimeUnit.MILLISECONDS.toMillis(15000)
        val intervalMillis3 = TimeUnit.MILLISECONDS.toMillis(60000)


        alarmManager1.setExact(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            currentTimeMillis + intervalMillis1,
            pendingIntentUpdate,
        )
        alarmManager2.setExact(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            currentTimeMillis + intervalMillis2,
            pendingIntentStoreLocation,
        )
        alarmManager3.setExact(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            currentTimeMillis + intervalMillis3,
            pendingIntentSendLocation,
        )
    }


    private fun cancelAlarm() {

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntentUpdate)
        alarmManager.cancel(pendingIntentStoreLocation)
        alarmManager.cancel(pendingIntentSendLocation)
    }

    @SuppressLint("MissingPermission")
    fun createNotification(
        context: Context,
        title: String,
        content: String,
    ): Notification {

        createNotificationChannel(title, content)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_sdm)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notification = builder.build()
        with(NotificationManagerCompat.from(this)) {
            notify(Notification_ID, notification)
        }

        return notification
    }


    private fun createNotificationChannel(name: String, content: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = content
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelAlarm()
        stopSelf()
    }

}


