package presentation.screens.auth.viewmodel



import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import data.network.request.version.VersionRequest
import domain.models.version.GetVersionDomain
import domain.usecase.usecase.auth.ResendUseCase
import domain.usecase.usecase.auth.VerifyUseCase
import domain.usecase.usecase.profile.StoreProfileUseCase
import domain.usecase.usecase.version.GetVersionOfServerUseCase
import domain.usecase.usecase.version.SendVersionToServerUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.BackgroundServiceApp
import irancell.nwg.wfm.CountdownTimer
import irancell.nwg.wfm.DeviceInfo
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.SMSListener
import irancell.nwg.wfm.TimerListener
import irancell.nwg.wfm.getSharedPref
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import presentation.screens.splash.events.CheckVersionEvent
import utils.AsyncStatus
import utils.BaseViewModel
import utils.FileApk
import utils.Token
import utils.ViewStates
import utils.isRunningGPS

class VerifyScreenVM(
   private val verifyUseCase: VerifyUseCase,
   private val resendUseCase: ResendUseCase,
   private val versionToServerUseCase: SendVersionToServerUseCase,
   private val storeProfileUseCase: StoreProfileUseCase,
   private val getVersionOfServerUseCase: GetVersionOfServerUseCase
) : BaseViewModel() {
    private val _remainTime = MutableStateFlow(59)
    var remainTime = _remainTime.asStateFlow()

    private val _finishTimer = MutableStateFlow(false)
    var finishTimer =_finishTimer.asStateFlow()


     val otpCode = MutableStateFlow("")
    var eventsVersion = mutableStateOf<CheckVersionEvent>(CheckVersionEvent.Default)

    private val _versionData = mutableStateOf<GetVersionDomain?>(null)
    val versionData: State<GetVersionDomain?> = _versionData


    fun updateOtp(smsCode : String){
        otpCode.update { smsCode }
    }


    private val countdownTimer = CountdownTimer(_remainTime.value, object : TimerListener {
        override fun onTick(secondsLeft: Int) {
            _remainTime.update { secondsLeft }
            // Update UI with the remaining seconds
        }

        override fun onFinish() {
            _finishTimer.update { true }
        }
    })
    init {
      countdownTimer.start()
      SMSListener.enableSMSListener({
            otpCode.value = it
          Napier.log(LogLevel.ASSERT,tag="otpCodess",message= otpCode.value)
          if (otpCode.value.length == 6) {
              verify(otpCode.value)
          }
      },{
            updateState(ViewStates.Error(MR.strings.form))
      })
    }

    fun disableSMSListener(){
        SMSListener.disableSMSListener()
    }

    fun verify(otpCode : String){


        viewModelScope.launch {

            verifyUseCase(otpCode).collect{
                val authToken = it.data ?:""
                Napier.log(LogLevel.ASSERT, tag = "gettoken", message = authToken)

                when(it.status){
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)
                        Napier.log(LogLevel.ASSERT, tag = "gettoken", message = "ERROR"+it.message)
                    }
                    AsyncStatus.LOADING -> {
                        updateState(ViewStates.Loading)
                        Napier.log(LogLevel.ASSERT, tag = "gettoken", message = "LOADING")
                    }
                    AsyncStatus.EMPTY->{

                    }
                    AsyncStatus.SUCCESS -> {
                        getSharedPref().put(Token, authToken)
                        getProfile()
                        sendVersionToServer()


                    }
                }
            }

        }
    }
    private fun getProfile(){
        viewModelScope.launch {
            storeProfileUseCase(Unit).collect {
                when(it.status){
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)
                        Napier.log(LogLevel.ASSERT, tag = "getProfile", message = "ERROR")

                    }
                    AsyncStatus.LOADING -> {

                        Napier.log(LogLevel.ASSERT, tag = "getProfile", message = "LOADING")

                    }
                    AsyncStatus.EMPTY->{

                    }
                    AsyncStatus.SUCCESS -> {
                        updateState(ViewStates.Success())
                        countdownTimer.stop()
                        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "SUCCESS${it.data}")

                    }
                }
            }
        }
    }

    fun resendCode(onError: () -> Unit) {

        viewModelScope.launch {
            resendUseCase(Unit).collect {
                when(it.status){
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)
                        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "ERROR")
                        onError()

                    }
                    AsyncStatus.LOADING -> {

                        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "LOADING")

                    }

                    AsyncStatus.EMPTY->{

                    }
                    AsyncStatus.SUCCESS -> {
                        _finishTimer.update { false }
                        _remainTime.update { 59 }
                        countdownTimer.start()
                        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "SUCCESS${it.data}")
                    }
                }
            }
        }
    }

    private fun sendVersionToServer() {
        viewModelScope.launch {
            versionToServerUseCase(
                VersionRequest(
                current_version_code = DeviceInfo.getAppVersionCode(),
                current_version_name = DeviceInfo.getAppVersionName(),
                device_model = DeviceInfo.getDeviceModel(),
                os = DeviceInfo.getPlatformName(),
                os_version = DeviceInfo.getOSVersion().toDouble()
            )
            ).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)
                        Napier.log(LogLevel.ASSERT, tag = "versionToServer", message = "ERROR"+it.message)
                    }

                    AsyncStatus.LOADING -> {
                        updateState(ViewStates.Loading)
                        Napier.log(LogLevel.ASSERT, tag = "versionToServer", message = "LOADING")
                    }
                    AsyncStatus.EMPTY->{
                        updateState(ViewStates.EMPTY)
                    }

                    AsyncStatus.SUCCESS -> {
                        //updateState(ViewStates.Success())
                        checkVersionOfServer()
                        Napier.log(LogLevel.ASSERT, tag = "versionToServer", message = "SUCCESS${it.data}")
                    }
                }
            }

        }
    }


    private fun checkVersionOfServer() {
        viewModelScope.launch {
            getVersionOfServerUseCase(Unit).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        eventsVersion.value = CheckVersionEvent.InvalidToken


                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "get version of Server",
                            message = "ERROR" + it.message
                        )
                    }

                    AsyncStatus.LOADING -> {
                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "get version of Server",
                            message = "LOADING"
                        )
                    }

                    AsyncStatus.EMPTY -> {

                    }

                    AsyncStatus.SUCCESS -> {

                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "get version of Server",
                            message = "SUCCESS${it.data}"
                        )
                        it. data?.let { versionData ->
                            _versionData.value = versionData
                            when {
                                versionData.force_update && versionData.version_code > DeviceInfo.getAppVersionCode().toDouble()-> {
                                    eventsVersion.value = CheckVersionEvent.ForceUpdate
                                }
                                versionData.version_code > DeviceInfo.getAppVersionCode().toDouble() -> {
                                    eventsVersion.value = CheckVersionEvent.NormalUpdate
                                }
                                else -> {
                                    getSharedPref().put(FileApk,"")
                                    eventsVersion.value = CheckVersionEvent.OkVersion
                                }
                            }
                        } ?: run {
                            getSharedPref().put(FileApk,"")
                            eventsVersion.value = CheckVersionEvent.OkVersion
                        }
                    }
                }
            }

        }
    }



}