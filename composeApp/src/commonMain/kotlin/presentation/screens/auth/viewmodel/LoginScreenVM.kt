package presentation.screens.auth.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import domain.models.LoginRequestDomain
import domain.usecase.usecase.auth.LoginUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.getSharedPref
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import presentation.screens.auth.AuthValidation
import presentation.screens.splash.events.PermissionEvent
import utils.AsyncStatus
import utils.BaseViewModel
import utils.Password
import utils.UserName
import utils.ViewStates

class LoginScreenVM(
    private val loginUseCase: LoginUseCase
) : BaseViewModel() {

    private val _authValidationState =
        MutableStateFlow<AuthValidation>(AuthValidation.Init)
    val authValidationState = _authValidationState.asStateFlow()

    private val _email = MutableStateFlow(getSharedPref().getString(UserName).orEmpty())
    var email = _email.asStateFlow()

    private val _password = MutableStateFlow(getSharedPref().getString(Password).orEmpty())
    var password = _password.asStateFlow()




    fun performLogin(email: String, pass: String): Boolean {
        return when {
            email.isEmpty() -> {
                _authValidationState.update { AuthValidation.NoEmail }
                false
            }
            pass.isEmpty() -> {
                _authValidationState.update { AuthValidation.NoPassword }
                false
            }
            pass.length < 8 -> {
                _authValidationState.update { AuthValidation.NotEnoughChar }
                false
            }
            else -> true
        }
    }




    fun login(userName: String, password: String) {


        viewModelScope.launch {

            loginUseCase(LoginRequestDomain(userName, password)).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)

                        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "ERROR"+it.message)

                    }

                    AsyncStatus.LOADING -> {
                        updateState(ViewStates.Loading)

                        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "LOADING")

                    }

                    AsyncStatus.SUCCESS -> {
                        updateState(ViewStates.Success)
                        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "SUCCESS${it.data}")
                    }
                }
            }

        }
    }

    fun updatePassword(text: String) {
        _password.update { text }

    }

    fun updateEmail(text: String) {
        _email.update { text }
    }

}