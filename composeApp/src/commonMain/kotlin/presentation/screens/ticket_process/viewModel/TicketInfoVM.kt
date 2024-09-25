package presentation.screens.ticket_process.viewModel

import androidx.compose.runtime.mutableStateListOf
import data.network.response.task.task.InitForm

import domain.models.PhotoDomain
import domain.models.task.InitFormDomain
import domain.models.task.TaskDomain

import domain.usecase.usecase.initialForm.GetInitialFormByTask

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import utils.AsyncStatus
import utils.BaseViewModel


import utils.ViewStates

class TicketInfoVM(
    private val getInitialFormByTask: GetInitialFormByTask,
) : BaseViewModel() {

    val initFormsState = mutableStateListOf<InitFormDomain>()
    private val _ticketNumber = MutableStateFlow("0")
    val ticketNumber = _ticketNumber.asStateFlow()

    fun updateTicketNumber(ticketNumber: String) {
        _ticketNumber.update { ticketNumber }
    }

    private val _positionSelected = MutableStateFlow(0)
    val positionSelected = _positionSelected.asStateFlow()

    fun getInitialForm(ticketNumber: String) {
        viewModelScope.launch {
            getInitialFormByTask(ticketNumber).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)
                    }

                    AsyncStatus.LOADING -> {
                        updateState(ViewStates.Loading)
                    }

                    AsyncStatus.EMPTY -> {
                        updateState(ViewStates.EMPTY)
                    }

                    AsyncStatus.SUCCESS -> {
                        updateState(ViewStates.Success())

                        it.data?.let {
                            initFormsState.addAll(it.initForms)
                        }
                    }
                }
            }
        }
    }
}






