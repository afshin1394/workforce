package utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import domain.usecase.ResultStatus
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import io.ktor.http.HttpMessage
import irancell.nwg.wfm.GPS
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.provideAppContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed class ViewStates() {
    data object Default : ViewStates()
    data object Loading : ViewStates()
    data object NoGps : ViewStates()
    data class Error(val message : StringResource) : ViewStates()
    data object Success : ViewStates()
}


open class BaseViewModel : ViewModel() {
    val loading = MutableStateFlow(false)

     private val _state = MutableStateFlow<ViewStates>(ViewStates.Default)
     val state = _state.asStateFlow()


       init {

           GPS.registerGps(provideAppContext()){
               Napier.log(LogLevel.ASSERT,"BaseViewModel", message = "enableGPS")

               _state.update { ViewStates.NoGps }
            }
       }


        fun updateState(viewStates: ViewStates){
        _state.update { viewStates }
    }
    fun handleError(resultStatus: ResultStatus?){
        when(resultStatus) {
            is ResultStatus.CLIENT_EXCEPTION -> {
                _state.update { ViewStates.Error(MR.strings.client_error) }
            }
            is ResultStatus.EXCEPTION -> {
                _state.update { ViewStates.Error(MR.strings.general_error) }


            }
            is ResultStatus.IO_EXCEPTION -> {
                _state.update { ViewStates.Error(MR.strings.general_error) }


            }
            is ResultStatus.REDIRECT_EXCEPTION -> {

            }
            is ResultStatus.SERVER_EXCEPTION -> {
                _state.update { ViewStates.Error(MR.strings.server_error) }


            }
            is ResultStatus.SUCCESS -> {
                _state.update { ViewStates.Error(MR.strings.success) }


            }
            is ResultStatus.TIME_OUT -> {
                _state.update { ViewStates.Error(MR.strings.timeout_error) }

            }

            else -> {
                _state.update { ViewStates.Error(MR.strings.general_error) }

            }
        }


    }
}
