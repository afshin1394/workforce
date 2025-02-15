package presentation.screens.ticket_process.viewModel

import androidx.compose.runtime.mutableStateListOf

import domain.models.task.InstanceTicketsBasicInformationValuesDomain

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


    val initFormsState = mutableStateListOf<InstanceTicketsBasicInformationValuesDomain>()




    private val _ticketId = MutableStateFlow("0")
    val ticketId = _ticketId.asStateFlow()

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
                        handleError(it.resultStatus,it.message)
                    }

                    AsyncStatus.LOADING -> {
                        updateState(ViewStates.Loading)
                    }

                    AsyncStatus.EMPTY->{
                        updateState(ViewStates.EMPTY)
                    }

                    AsyncStatus.SUCCESS -> {
                        updateState(ViewStates.Success())

                        it.data?.let {
                            initFormsState.add(InstanceTicketsBasicInformationValuesDomain("ticket_number", ticketNumber))
                            initFormsState .addAll(it.initForms)
                        }
                    }
                }
            }
        }
    }

    fun updateTicketId(ticketId: String) {
      _ticketId.update { ticketId }
    }

}






