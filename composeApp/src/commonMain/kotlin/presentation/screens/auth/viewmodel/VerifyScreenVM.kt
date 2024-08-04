package presentation.screens.auth.viewmodel



import domain.usecase.usecase.auth.ResendUseCase
import domain.usecase.usecase.auth.VerifyUseCase
import domain.usecase.usecase.profile.StoreProfileUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.BackgroundServiceApp
import irancell.nwg.wfm.CountdownTimer
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.SMSListener
import irancell.nwg.wfm.TimerListener
import irancell.nwg.wfm.getSharedPref
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import utils.AsyncStatus
import utils.BaseViewModel
import utils.Token
import utils.ViewStates
import utils.isRunningGPS

class VerifyScreenVM(
   private val verifyUseCase: VerifyUseCase,
   private val resendUseCase: ResendUseCase,
   private val storeProfileUseCase: StoreProfileUseCase
) : BaseViewModel() {
    private val _remainTime = MutableStateFlow(59)
    var remainTime = _remainTime.asStateFlow()

    private val _finishTimer = MutableStateFlow(false)
    var finishTimer =_finishTimer.asStateFlow()


     val otpCode = MutableStateFlow("")


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
                    AsyncStatus.SUCCESS -> {
                        getSharedPref().put(Token, authToken)
                        getProfile()


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

}