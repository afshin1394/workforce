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
import com.plusmobileapps.konnectivity.Konnectivity
import com.plusmobileapps.konnectivity.NetworkConnection
import database.entity.GeneralLocationEntity
import domain.usecase.ResultStatus
import domain.usecase.usecase.location.SendLocationToServerUseCase
import domain.usecase.usecase.location.StoreLocationDataUseCase
import domain.usecase.usecase.ticket.UpdateTaskUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import presentation.screens.main.viewmodel.TicketListStatus
import utils.AlarmAction
import utils.AsyncStatus
import utils.ServiceState
import utils.getCurrentDate
import java.util.concurrent.TimeUnit
import kotlin.math.log

internal actual class BackgroundServiceApp : Service(), KoinComponent {

    val storeLocationDataUseCase: StoreLocationDataUseCase by inject()
    val sendLocationToServerUseCase: SendLocationToServerUseCase by inject()
    val updateTaskUseCase: UpdateTaskUseCase by inject()

    private lateinit var pendingIntentUpdate: PendingIntent
    private lateinit var pendingIntentStoreLocation: PendingIntent
    private lateinit var pendingIntentSendLocation: PendingIntent
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private val REQUEST_CODE_1 = 1
    private val REQUEST_CODE_2 = 2
    private val REQUEST_CODE_3 = 3

    val konnectivity: Konnectivity = Konnectivity()
    private val _networkState = MutableStateFlow(false)
    var scope = CoroutineScope(Dispatchers.Main)
    val telephonyData = TelephonyDataImpl(this)


    actual companion object {
        private val _serviceState: MutableStateFlow<ServiceState> =
            MutableStateFlow(ServiceState.Normal)

        private val _ticketListState: MutableStateFlow<TicketListStatus> =
            MutableStateFlow(TicketListStatus.UnRecognized)

        actual fun updateServiceState(serviceState: ServiceState) {
            Log.d("HeyServiceGimmeTheLog",serviceState.toString())
            _serviceState.update { serviceState }
        }


        val gpsTrackingIntent: Intent by lazy {
            Intent((provideAppContext() as Context), BackgroundServiceApp::class.java)
        }

        const val Notification_ID = 123
        const val CHANNEL_ID = "GPS TRACKER"
        actual fun stopBackgroundService() {
            updateServiceState(ServiceState.Normal)
            /*if (getSharedPref().getBool(isRunningGPS, false))*/
            (provideAppContext() as Context).stopService(gpsTrackingIntent)

        }

        actual fun isServiceRunning(): Boolean {
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

        actual val ticketListState: MutableStateFlow<TicketListStatus>
            get() = _ticketListState

    }

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            traceNetwork()
        }

        /*getSharedPref().put(isRunningGPS, true)*/
        val notification =
            createNotification(
                applicationContext,
                "Ready to work",
                "iTicket is running on you device"
            )
        val intent1 = Intent(this, BackgroundServiceApp::class.java)
        intent1.setAction(AlarmAction.UPDATE.title)

        val intent2 = Intent(this, BackgroundServiceApp::class.java)
        intent2.setAction(AlarmAction.STORE_LOCATION.title)


        val intent3 = Intent(this, BackgroundServiceApp::class.java)
        intent3.setAction(AlarmAction.SEND_LOCATION.title)


        startForeground(Notification_ID, notification);

        Location.start() {
            scope.launch {
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
        startUpdateAlarm()
        startStoreLocationAlarm()
        startSendLocationAlarm()
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            scope.launch(Dispatchers.IO) {
                if (_serviceState.value != ServiceState.Suspend && _serviceState.value !is ServiceState.Faulty) {
                    val newState =
                        if (isServiceRunning()) ServiceState.Normal else ServiceState.NotRunning
                    updateServiceState(newState)
                }
                val data = telephonyData.getTelephonyData()
                when (intent?.action) {
                    AlarmAction.SEND_LOCATION.title -> {
                        startSendLocationAlarm()
                        sendLocationToServer()
                    }

                    AlarmAction.STORE_LOCATION.title -> {
                        startStoreLocationAlarm()
                        storeLocation(data)
                    }

                    AlarmAction.UPDATE.title -> {
                        startUpdateAlarm()

                        if (_serviceState.value != ServiceState.Suspend) {
                          updateTask()
                        }
                    }
                }
            }

        } catch (e: Exception) {

        }
        return START_STICKY
    }

    private suspend fun BackgroundServiceApp.storeLocation(data: JsonObject) {

        Log.i("networkState", "storeLocation: " + _networkState.value)

        val networkInfo: String = when (_networkState.value) {
            true -> {
                Json.encodeToString(JsonObject.serializer(), data)
            }

            false -> {
                Json.encodeToString(JsonObject.serializer(), createEmptyTelephonyData())
            }
        }

        storeLocationDataUseCase(
            GeneralLocationEntity(
                latitude = lat.toString(),
                longitude = lon.toString(),
                datetime = getCurrentDate(),
                isSent = 0,
                networkInfo = networkInfo
            )
        ).collect {
            when (it.status) {
                else -> {}
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
                        _serviceState.update { ServiceState.Faulty(MR.strings.unauthorized) }
                }

                else -> {}
            }
        }
    }

    private suspend fun traceNetwork() {
        konnectivity.currentNetworkConnectionState.collect { connection ->
            when (connection) {
                NetworkConnection.NONE -> {
                    _networkState.update { false }
                }

                NetworkConnection.WIFI -> {
                    _networkState.update { true }
                }

                NetworkConnection.CELLULAR -> {
                    _networkState.update { true }
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
                            _serviceState.update { ServiceState.Faulty(MR.strings.unauthorized) }
                    }

                    AsyncStatus.SUCCESS -> {
                        Log.i("getAllTask", "onStartCommand: CallApi" + it.data)
                        _ticketListState.update { TicketListStatus.Filled }
                    }

                    AsyncStatus.EMPTY -> {
                        _ticketListState.update { TicketListStatus.Empty }
                    }

                    AsyncStatus.LOADING -> {
                        _ticketListState.update { TicketListStatus.Loading }
                    }

                    else -> {}
                }
            }
    }

//    private suspend fun updateSteps() {
//        updateStepsUseCase(Unit).collect {
//            when (it.status) {
//                AsyncStatus.ERROR -> {
//                    Napier.log(
//                        LogLevel.ASSERT,
//                        "updateSteps",
//                        message = "ERROR: " + it.message
//                    )
//                }
//
//                AsyncStatus.EMPTY -> {
//                    Napier.log(LogLevel.ASSERT, "updateSteps", message = "EMPTY : ")
//                }
//
//                AsyncStatus.LOADING -> {
//                    Napier.log(LogLevel.ASSERT, "updateSteps", message = "LOADING: ")
//                }
//
//                AsyncStatus.SUCCESS -> {
//                    Napier.log(
//                        LogLevel.ASSERT,
//                        "updateSteps",
//                        message = "SUCCESS: " + it.data
//                    )
//                }
//            }
//        }
//    }

    private fun startUpdateAlarm() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val currentTimeMillis = SystemClock.elapsedRealtime()
        val intervalMillis = TimeUnit.MILLISECONDS.toMillis(AlarmAction.UPDATE.interval)
        alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            currentTimeMillis + intervalMillis,
            pendingIntentUpdate,
        )
    }

    private fun startStoreLocationAlarm() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val currentTimeMillis = SystemClock.elapsedRealtime()
        val intervalMillis = TimeUnit.MILLISECONDS.toMillis(AlarmAction.STORE_LOCATION.interval)
        alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            currentTimeMillis + intervalMillis,
            pendingIntentStoreLocation,
        )
    }

    private fun startSendLocationAlarm() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val currentTimeMillis = SystemClock.elapsedRealtime()
        val intervalMillis = TimeUnit.MILLISECONDS.toMillis(AlarmAction.SEND_LOCATION.interval)
        alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            currentTimeMillis + intervalMillis,
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
            .setSmallIcon(R.drawable.ic_i_ticket)
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

    private fun createEmptyTelephonyData(): JsonObject {
        val jsonArray = Json.decodeFromString<JsonArray>("[]")
        val jsonObject = JsonObject(mapOf("info" to jsonArray))
        val wrappedJsonString = Json.encodeToString(JsonObject.serializer(), jsonObject)

        return Json.parseToJsonElement(wrappedJsonString).jsonObject
    }

}


