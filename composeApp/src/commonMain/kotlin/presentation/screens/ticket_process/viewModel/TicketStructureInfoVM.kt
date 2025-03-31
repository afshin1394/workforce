package presentation.screens.ticket_process.viewModel

import androidx.compose.runtime.mutableStateListOf
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.InitComponentDomain
import domain.models.task.InstanceTicketsBasicInformationValuesDomain
import domain.usecase.usecase.initialForm.GetInitialFormByTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import presentation.model.ExtractLogicsModel
import utils.AsyncStatus
import utils.BaseViewModel
import utils.LogicCalculation
import utils.LogicTicketInfoCalculation
import utils.ViewStates
import utils.processInParallel

class TicketStructureInfoVM (private val getInitialFormByTask: GetInitialFormByTask,
) : BaseViewModel() {


    private val _ticketId = MutableStateFlow("0")
    val ticketId = _ticketId.asStateFlow()

    private val _ticketNumber = MutableStateFlow("0")
    val ticketNumber = _ticketNumber.asStateFlow()

    var tempInitComponentList = mutableStateListOf<InitComponentDomain>()
    val logicCalculation: LogicTicketInfoCalculation = LogicTicketInfoCalculation(viewModelScope, tempInitComponentList)
    fun updateTicketNumber(ticketNumber: String) {
        _ticketNumber.update { ticketNumber }
    }

    fun updateTicketId(ticketId: String) {
        _ticketId.update { ticketId }
    }
    fun getInitialStructureForm(ticketNumber: String) {
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
                            tempInitComponentList.clear()
                            it.initStructure.components?.toList()
                                ?.let { it1 ->
                                    tempInitComponentList.addAll(it1)
                                    println("khoda komak   ${it1[0].injected_value?:"kkk"}")
                                }

                            handleLogics {

                            }

                        }


                    }
                }
            }
        }
    }

    suspend fun handleLogics(onResult: (MutableList<ExtractLogicsModel>) -> Unit) {
        withContext(Dispatchers.Default) {
            val componentsCopy = tempInitComponentList.toList()

            // Perform logic checks in the background
            checkHideLogics(componentsCopy)

        }
    }



    private suspend fun checkHideLogics(components: List<InitComponentDomain>) {
        processInParallel(components, processBlock = { componentDomain, mutex ->
            logicCalculation.extractHideLogic(componentDomain).let { logics ->

            }
            componentDomain.components.value?.takeIf { it.isNotEmpty() }?.let { nestedComponents ->
                yield()  // Yield control if the workload is high
                checkHideLogics(nestedComponents)  // Recursive call on nested components
            }
        })

    }



}