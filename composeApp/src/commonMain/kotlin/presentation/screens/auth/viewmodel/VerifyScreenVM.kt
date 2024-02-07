package presentation.screens.auth.viewmodel


import androidx.compose.runtime.mutableStateOf
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import domain.usecase.usecase.auth.VerifyUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import utils.AsyncStatus
import utils.BaseViewModel
import utils.ViewStates

class VerifyScreenVM(
   private val verifyUseCase: VerifyUseCase
) : BaseViewModel() {

    fun verify(otpCode : String,onProcess : () -> Unit){


        viewModelScope.launch {

            verifyUseCase(otpCode).collect{
                when(it.status){
                    AsyncStatus.ERROR -> {
                        state.update { ViewStates.Error }
                        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "ERROR")

                    }
                    AsyncStatus.LOADING -> {
                        state.update { ViewStates.Loading }

                        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "LOADING")

                    }
                    AsyncStatus.SUCCESS -> {
                        state.update { ViewStates.Success }

                        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "SUCCESS${it.data}")
                        onProcess()
                    }
                }
            }

        }
    }

}