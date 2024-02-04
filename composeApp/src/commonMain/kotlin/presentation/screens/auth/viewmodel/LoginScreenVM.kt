package presentation.screens.auth.viewmodel

import domain.models.LoginRequestDomain
import domain.usecase.usecase.auth.LoginUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import utils.AsyncStatus
import utils.BaseViewModel

class LoginScreenVM(
    private val loginUseCase: LoginUseCase
) : BaseViewModel() {

    fun login(userName : String , password : String,onProcess : () -> Unit){
        viewModelScope.launch {

            loginUseCase(LoginRequestDomain(userName, password)).collect{
                when(it.status){
                    AsyncStatus.ERROR -> {
                        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "ERROR")

                    }
                    AsyncStatus.LOADING -> {
                        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "LOADING")

                    }
                    AsyncStatus.SUCCESS -> {
                        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "SUCCESS${it.data}")
                        onProcess()
                    }
                }
            }

        }
    }

}