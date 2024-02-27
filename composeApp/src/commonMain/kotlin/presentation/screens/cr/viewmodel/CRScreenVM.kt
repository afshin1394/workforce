package presentation.screens.cr.viewmodel

import androidx.compose.runtime.mutableStateListOf
import domain.models.CRDomain
import domain.models.TaskDomain
import domain.usecase.usecase.cr.GetAllCR
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import utils.AsyncStatus
import utils.BaseViewModel
import utils.ViewStates

class CRScreenVM (
    private val getAllCR: GetAllCR
) : BaseViewModel(){
    init {
        getAllChangeRequest()
    }
    val crDomainList  = mutableStateListOf<CRDomain>()

    fun getAllChangeRequest(){
        viewModelScope.launch(Dispatchers.IO) {
            getAllCR(
                Unit
            ).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)
                        Napier.log(LogLevel.ASSERT,"changeRequest", message = it.resultStatus.toString())
                    }
                    AsyncStatus.LOADING -> {
                        updateState(ViewStates.Loading)
                    }
                    AsyncStatus.SUCCESS -> {
                        updateState(ViewStates.Success)
                        it.data?.let { cr ->
                            crDomainList.clear()
                            crDomainList.addAll(cr)
                            Napier.log(LogLevel.ASSERT,"changeRequest", message = it.data.toString())
                        }
                    }

                }
            }
        }
    }
}