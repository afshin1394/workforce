package presentation.screens.ticket_process.viewModel

import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.collectAsState
import domain.models.initialForm.InitialFormDomain
import domain.models.initialForm.InitialFormStructureDomain
import domain.usecase.usecase.initialForm.GetInitialFormByTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import utils.AsyncStatus
import utils.BaseViewModel
import utils.ViewStates

class TicketInfoVM(
   private val getInitialFormByTask: GetInitialFormByTask
) : BaseViewModel() {
    var taskId = MutableStateFlow(0L)

    private val _initialFormStructureDomain = MutableStateFlow<InitialFormStructureDomain?>(null)
    val initialFormDomain = _initialFormStructureDomain.asStateFlow()


    fun getInitialForm(taskId: Long) {
        viewModelScope.launch {
        getInitialFormByTask(taskId).collect{
            when(it.status){
                AsyncStatus.ERROR -> {
                    handleError(it.resultStatus)
                }
                AsyncStatus.LOADING -> {
                    updateState(ViewStates.Loading)
                }
                AsyncStatus.SUCCESS -> {
                    val initialFormDomain = it.data
                    _initialFormStructureDomain.update { initialFormDomain?.structure }
                    updateState(ViewStates.Success)

                }
            }
        }

        }

    }
}