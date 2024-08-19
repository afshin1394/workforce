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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import utils.AsyncStatus
import utils.AvailabilityObjectId
import utils.ServiceState
import utils.TicketNumber
import utils.ViewStates
import utils.getCurrentDate
import java.util.concurrent.TimeUnit


internal actual class BackgroundServiceApp : Service() , KoinComponent {

    val storeLocationDataUseCase : StoreLocationDataUseCase by inject()
    val sendLocationToServerUseCase : SendLocationToServerUseCase by inject()
    val updateTaskUseCase : UpdateTaskUseCase by inject()
    val updateStepsUseCase : UpdateStepsUseCase by inject()

    lateinit var pendingIntent: PendingIntent
    private var lat: Double = 0.0
    private var lon: Double = 0.0


    actual companion object {
        private val _serviceState : MutableStateFlow<ServiceState> = MutableStateFlow(ServiceState.Normal)
        actual fun updateServiceState(serviceState: ServiceState){
            _serviceState.update { serviceState }
        }


        val gpsTrackingIntent : Intent by lazy {
            Intent((provideAppContext() as Context), BackgroundServiceApp::class.java)
        }

        const val Notification_ID = 123
        const val CHANNEL_ID = "GPS TRACKER"
        actual fun stopBackgroundService(){

            /*if (getSharedPref().getBool(isRunningGPS, false))*/
            (provideAppContext() as Context).stopService(gpsTrackingIntent)

        }

        fun isServiceRunning(): Boolean {
            val activityManager = (provideAppContext() as  Context).getSystemService(ACTIVITY_SERVICE) as ActivityManager
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
        val intent = Intent(this, BackgroundServiceApp::class.java)
        startForeground(Notification_ID, notification);

        Location.start() {
            GlobalScope.launch {
                lat = it.latitude.toDouble()
                lon = it.longitude.toDouble()
            }

        }

        pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
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




        try {
            scope.launch(Dispatchers.Main) {

                storeLocationDataUseCase(
                    GeneralLocationEntity(
                        latitude = lat.toString(),
                       longitude =  lon.toString(),
                       datetime =  getCurrentDate(),
                       isSent =  0
                    )
                ).collect{
                    when(it.status){
                        AsyncStatus.ERROR -> {
                            val errorMessage = it.message!!

                        }
                        AsyncStatus.EMPTY->{

                        }
                        AsyncStatus.LOADING -> {
                        }
                        AsyncStatus.SUCCESS -> {

                        }
                    }
                }

                sendLocationToServerUseCase(
                    listOf(LiveLocationDomain(
                        lat,
                        lon,
                        getCurrentDate(),
                        0,
                        getSharedPref().getString(AvailabilityObjectId)?.toLong()?:0,
                        getSharedPref().getString(TicketNumber)?:""
                    ))
                ).collect{
                    when(it.status){
                        AsyncStatus.ERROR -> {
                            if( it.resultStatus is ResultStatus.CLIENT_EXCEPTION.UNATHORIZED || it.resultStatus is ResultStatus.CLIENT_EXCEPTION.FORBIDDEN)
                                _serviceState.update { ServiceState.Faulty }

                        }
                        AsyncStatus.LOADING -> {
                        }
                        AsyncStatus.EMPTY->{
                        }
                        AsyncStatus.SUCCESS -> {

                        }
                    }
                }
                println("TaskCallApi${_serviceState.value}")

                if(_serviceState.value!=ServiceState.Suspend) {
                    println("TaskCallApi${"ServiceState.Suspend"}")
                    updateTask()
                }

                startAlarm()
                scope.cancel()
            }

        }catch (e:Exception){

        }


        return START_STICKY
    }
    private suspend fun updateTask(){
        updateTaskUseCase(Unit)
            .collect{
                when(it.status){
                    AsyncStatus.ERROR -> {
                        if( it.resultStatus is ResultStatus.CLIENT_EXCEPTION.UNATHORIZED || it.resultStatus is ResultStatus.CLIENT_EXCEPTION.FORBIDDEN)
                            _serviceState.update { ServiceState.Faulty }
                        println("TaskCallApi${"ERROR"}")
                    }
                    AsyncStatus.LOADING -> {
                    }
                    AsyncStatus.EMPTY->{

                    }
                    AsyncStatus.SUCCESS -> {
                        updateSteps()
                        println("TaskCallApi${"SUCCESS"}")
                        Log.i("getAllTask", "onStartCommand: CallApi"+it.data)
                    }
                }
            }
    }
    private suspend fun updateSteps() {
        updateStepsUseCase(Unit).collect {
            when (it.status) {
                AsyncStatus.ERROR -> {
//                    if( it.resultStatus is ResultStatus.CLIENT_EXCEPTION.UNATHORIZED || it.resultStatus is ResultStatus.CLIENT_EXCEPTION.FORBIDDEN)
//                        _serviceState.update { ServiceState.Faulty }

                    Napier.log(
                        LogLevel.ASSERT,
                        "updateSteps",
                        message = "ERROR: " + it.message
                    )

                }
                AsyncStatus.EMPTY->{

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

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val currentTimeMillis = SystemClock.elapsedRealtime()
        val intervalMillis = TimeUnit.MILLISECONDS.toMillis(2000)

        alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            currentTimeMillis + intervalMillis,
            pendingIntent
        )
    }

    private fun cancelAlarm() {

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
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


