package presentation.screens.auth.viewmodel

import domain.models.LoginRequestDomain
import domain.usecase.usecase.auth.LoginUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import utils.AsyncStatus
import utils.BaseViewModel
import utils.ViewStates

class LoginScreenVM(
    private val loginUseCase: LoginUseCase
) : BaseViewModel() {

    fun login(userName : String , password : String,onProcess : () -> Unit){


        viewModelScope.launch {

            loginUseCase(LoginRequestDomain(userName, password)).collect{
                when(it.status){
                    AsyncStatus.ERROR -> {
                        state.update { ViewStates.Error }

                        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "ERROR"+it.message)

                    }
                    AsyncStatus.LOADING -> {
                        state.update { ViewStates.Loading }

                        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "LOADING")

                    }
                    AsyncStatus.SUCCESS -> {
                        state.update { ViewStates.Loading }

                        Napier.log(LogLevel.ASSERT, tag = "serviice", message = "SUCCESS${it.data}")
                        onProcess()
                    }
                }
            }

        }
    }

}