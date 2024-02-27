package presentation.screens.main.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import irancell.nwg.wfm.GpsTrackingService
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

import com.irancell.nwg.wfm.presentation.components.FilterSectionItem
import presentation.model.FilterType
import com.irancell.nwg.wfm.presentation.model.SelectableItem
import presentation.model.StateFilter
import presentation.screens.main.events.MainEvent
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import domain.models.SuspendTaskDomain
import domain.models.TaskDomain
import domain.usecase.ResultStatus
import domain.usecase.usecase.availability.ChangeServerAvailabilityUseCase
import domain.usecase.usecase.availability.GetAvailabilityUseCase
import domain.usecase.usecase.availability.StoreAvailabilityUseCase
import domain.usecase.usecase.suspendTask.GetSuspendTaskById
import domain.usecase.usecase.suspendTask.StoreSuspendTask
import domain.usecase.usecase.ticket.UpdateTasksUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import utils.AsyncResult

import utils.AsyncStatus
import utils.BaseViewModel
import utils.ViewStates
import utils.getCurrentDate

class MainScreenVM(
    private val storeAvailabilityUseCase: StoreAvailabilityUseCase,
    private val getAvailabilityUseCase: GetAvailabilityUseCase,
    private val changeServerAvailabilityUseCase: ChangeServerAvailabilityUseCase,
    private val updateTasksUseCase: UpdateTasksUseCase,
    private val storeSuspendTask: StoreSuspendTask,
    private val getSuspendTaskById: GetSuspendTaskById,
) : BaseViewModel() {
    private val _availability = MutableStateFlow(false)
    val availability = _availability.asStateFlow()

    private val _openCamera = MutableStateFlow(false)
    val openCamera = _openCamera.asStateFlow()
    val tasks  = mutableStateListOf<TaskDomain>()




    init {

        getCurrentAvailability()
        getTasks()

    }


    private fun getCurrentAvailability() {
        viewModelScope.launch(Dispatchers.Main) {
            getAvailabilityUseCase(
                Unit
            ).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)
                        Napier.log(LogLevel.ASSERT,"resrrrr", message = it.resultStatus.toString())
                    }
                    AsyncStatus.LOADING -> {
                        updateState(ViewStates.Loading)
                    }
                    AsyncStatus.SUCCESS -> {
                        updateState(ViewStates.Success)
                        it.data?.let { available ->
                            _availability.update { available }
                        }
                    }
                }
            }
        }
    }


    fun changeAvailability() {

        viewModelScope.launch(Dispatchers.Main) {


            changeServerAvailabilityUseCase(!_availability.value).collect {
                when (it) {
                    is AsyncResult.Error -> {
                        handleError(it.resultStatus)
                        Napier.log(LogLevel.ASSERT,"resrrrr", message = it.resultStatus.toString())

                    }

                    is AsyncResult.Loading -> {
                        updateState(ViewStates.Loading)

                    }

                    is AsyncResult.Success -> {
                        Napier.log(LogLevel.ASSERT,"resrrrr", message = it.resultStatus.toString())

                        _availability.update { !it }
                        storeAvailabilityUseCase(
                            Pair(_availability.value, it.data.toString())
                        ).collect {
                            when (it.status) {
                                AsyncStatus.ERROR -> {
                                    handleError(it.resultStatus)

                                }

                                AsyncStatus.LOADING -> {

                                }

                                AsyncStatus.SUCCESS -> {
                                    updateState(ViewStates.Success)
                                    Napier.log(
                                        LogLevel.ASSERT,
                                        "storeAvailabilityUseCase",
                                        message = _availability.value.toString()
                                    )
                                    if (_availability.value) {
                                        GpsTrackingService.startLocationTracker()
                                    } else {
                                        GpsTrackingService.stopLocationTracker()
                                    }

                                }
                            }
                        }
                    }
                }
            }


        }


    }


//    private val initialTasks = arrayListOf(
//        TaskDomain(
//            1235,
//        ),
//        TaskDomain(
//            1236,
//            "Huawei External Alarm, T5713, Bater ... Huawei External Alarm, T5713, Bater ...",
//            "HSE pre-check",
//            "Level 3",
//            "CR",
//            "Tehran, Amanieh, Zarin stre...",
//            "0h 43m",
//            "Done",
//            3
//        ),
//        TaskDomain(
//            1237,
//
//            "Huawei External Alarm, T5722, Bater ... Huawei External Alarm, T5722, Bater ...",
//            "HSE pre-check",
//            "Level 3",
//            "TT",
//            "Tehran, Nelson mandela, Zarin stre...",
//            "2h 43m",
//            "Pending",
//            1
//        ),
//        TaskDomain(
//            1238,
//
//            "Huawei External Alarm, T5742, Bater ... Huawei External Alarm, T5742, Bater ...",
//            "HSE pre-check",
//            "Level 2",
//            "TT",
//            "Tehran, Zafar, Zarin stre...",
//            "4h 43m",
//            "Done",
//            3
//        ), TaskDomain(
//            1239,
//
//            "Huawei External Alarm, T5744, Bater ... Huawei External Alarm, T5744, Bater ...",
//            "Departed",
//            "Level 2",
//            "CR",
//            "Tehran, Takhti, Zarin stre...",
//            "1h 43m",
//            "Suspended",
//            4
//        ), TaskDomain(
//            1339,
//
//            "Huawei External Alarm, T5754, Bater ... Huawei External Alarm, T5754, Bater ...",
//            "Departed",
//            "Level 1",
//            "PT",
//            "Tehran, Takhti, Zarin stre...",
//            "1h 43m",
//            "Completed",
//            5
//
//        ), TaskDomain(
//            1439,
//
//            "Huawei External Alarm, T5754, Bater ... Huawei External Alarm, T5754, Bater ...",
//            "Departed",
//            "Level 1",
//            "TT",
//            "Tehran, Mirdamad, Zarin stre...",
//            "4h 43m",
//            "Doing",
//            2
//
//        ), TaskDomain(
//            1429,
//
//            "Huawei External Alarm, T3754, Bater ... Huawei External Alarm, T3754, Bater ...",
//            "Departed",
//            "Level 1",
//            "CR",
//            "Tehran, Ghoba, Zarin stre...",
//            "2h 44m",
//            "Pending",
//            1
//
//
//        )
//    )


    var selectedTask: MutableState<TaskDomain?> = mutableStateOf(null)

    var events = mutableStateOf<MainEvent>(MainEvent.Default)


    var enableSuspendSubmit = mutableStateOf(false)
    var enableCancelSubmit = mutableStateOf(false)

    var suspendReason = mutableStateOf("")
    var cancelReason = mutableStateOf("")


    var suspendDescription = mutableStateOf("")
    var cancelDescription = mutableStateOf("")

    var suspendImageUri = mutableStateOf("")


    val suspendItems = mutableStateListOf(
        SelectableItem(1, "Equipment failure", false),
        SelectableItem(2, "Weather condition", false),
        SelectableItem(3, "Travel restrictions", false),
        SelectableItem(4, "Location Constraints", false),
        SelectableItem(5, "Blocked road", false),
        SelectableItem(6, "Other", false)

    )


    val cancelItems = mutableStateListOf(
        SelectableItem(1, "Lack of expertise", false),
        SelectableItem(2, "Unavailable resources", false),
        SelectableItem(3, "Equipment failure", false),
        SelectableItem(4, "Weather condition", false),
        SelectableItem(5, "Travel restrictions", false),
        SelectableItem(6, "Location Constraints", false),
        SelectableItem(7, "Blocked road", false),
        SelectableItem(8, "Car crash", false),
        SelectableItem(9, "Other", false),
    )


    val items = FilterSectionItem(
        "Severity Level",

        arrayListOf(
            StateFilter(1, "Level 1", false, FilterType.SEVERITY_LEVEL),
            StateFilter(2, "Level 2", false, FilterType.SEVERITY_LEVEL),
            StateFilter(3, "Level 3", false, FilterType.SEVERITY_LEVEL),
            StateFilter(4, "Level 4", false, FilterType.SEVERITY_LEVEL),
            StateFilter(5, "Level 5", false, FilterType.SEVERITY_LEVEL)
        )
    )

    val items2 = FilterSectionItem(
        "Current step",

        arrayListOf(
            StateFilter(1, "HSE pre-checked", false, FilterType.CURRENT_STEP),
            StateFilter(2, "Departed", false, FilterType.CURRENT_STEP),
            StateFilter(3, "HSE checked", false, FilterType.CURRENT_STEP),
            StateFilter(4, "Job done", false, FilterType.CURRENT_STEP),
            StateFilter(5, "Waiting for approval", false, FilterType.CURRENT_STEP),
            StateFilter(5, "Approved", false, FilterType.CURRENT_STEP)
        )
    )

    val items3 = FilterSectionItem(
        "SLA status",

        arrayListOf(
            StateFilter(1, "Overdue", false, FilterType.SLA_STATUS),
            StateFilter(2, "Uptime", false, FilterType.SLA_STATUS),
            StateFilter(3, "On track", false, FilterType.SLA_STATUS),

            )
    )

    val items4 = FilterSectionItem(
        "Ticket type",

        arrayListOf(
            StateFilter(1, "TT", false, FilterType.TICKET_TYPE),
            StateFilter(2, "CR", false, FilterType.TICKET_TYPE),
            StateFilter(3, "DG", false, FilterType.TICKET_TYPE),
            StateFilter(3, "PM", false, FilterType.TICKET_TYPE),

            )
    )


    val filterSectionItems: MutableList<FilterSectionItem> =
        mutableListOf(items, items2, items3, items4)

    data class FilterModel(val type: FilterType, val filter: StateFilter)

    fun getActiveFilterItems() {
        val filterMaps: ArrayList<FilterModel> = arrayListOf()

        val filteredList = arrayListOf<TaskDomain>()

        filterSectionItems.forEach {
            it.filterStates.forEach {
                if (it.isActive)
                    filterMaps.add(FilterModel(it.type, it))
            }


        }
        val tasksList: ArrayList<TaskDomain> = arrayListOf()
        tasksList.addAll(tasks)
        for (key in filterMaps) {
            when (key.type) {
                FilterType.CURRENT_STEP -> {
//
//                    val ix = tasksList.filter {
//                        it.step.trim() == key.filter.title.trim()
//                    }
//                    tasksList.clear()
//                    tasksList.addAll(ix)
//                    filteredList.addAll(ix)

                }

                FilterType.DEFAULT -> {

                }

                FilterType.SEVERITY_LEVEL -> {
//                    val ix = tasksList.filter { it.faultLevel.trim() == key.filter.title.trim() }
//                    tasksList.clear()
//                    tasksList.addAll(ix)
//                    filteredList.addAll(ix)

                }

                FilterType.SLA_STATUS -> {

                }

                FilterType.TICKET_TYPE -> {
                    val ix = tasksList.filter { it.status.trim() == key.filter.title.trim() }
                    tasksList.clear()
                    tasksList.addAll(ix)
                    filteredList.addAll(ix)
                }
            }

            tasks.clear()
            tasks.addAll(tasksList)
        }
    }


    fun removeAllFilters() {

        filterSectionItems.forEach {
            it.filterStates.forEach { stateFilter ->
                run {
                    stateFilter.isActiveState = false
                    stateFilter.isActive = false
                }
            }
        }
        tasks.clear()
        tasks.addAll(tasks)
    }

    fun openCamera() {
        viewModelScope.launch {
            _openCamera.update { true }

//            val cameraPermission = Permission.CAMERA
//            val isGranted = permissionsController.isPermissionGranted(cameraPermission)
//            if (isGranted) {
//            } else {
//                permissionsController.providePermission(cameraPermission)
//            }
        }
    }

    fun updateCameraStatus(openCamera: Boolean) {
        _openCamera.update { openCamera }
    }

    fun getTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            updateTasksUseCase(Unit).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)


                        Napier.log(
                            LogLevel.ASSERT,
                            "getAllWorksUseCase",
                            message = "ERROR: " + it.message
                        )

                    }

                    AsyncStatus.LOADING -> {
                        Napier.log(LogLevel.ASSERT, "getAllWorksUseCase", message = "LOADING: ")
                        updateState(ViewStates.Loading)

                    }

                    AsyncStatus.SUCCESS -> {
                        updateState(ViewStates.Success)

                        Napier.log(
                            LogLevel.ASSERT,
                            "getAllWorksUseCase",
                            message = "SUCCESS: " + it.data
                        )
                        it.data?.let { it1 -> tasks.addAll(it1) }

                        Napier.log(
                            LogLevel.ASSERT,
                            "getAllWorksUseCase",
                            message = "initialTasks: $tasks"
                        )

                    }

                }
            }
        }

    }
    private val _suspendTaskDomain = MutableStateFlow<SuspendTaskDomain?>(null)
    val suspendTaskDomain = _suspendTaskDomain.asStateFlow()

    fun loadSuspendTask(){
        viewModelScope.launch {
            selectedTask.value?.let {
                getSuspendTaskById(it.workId).collect{
                    when (it.status) {
                        AsyncStatus.ERROR -> {
//                            handleError(it.resultStatus)
                        }

                        AsyncStatus.LOADING -> {
                            Napier.log(LogLevel.ASSERT, "getAllWorksUseCase", message = "LOADING: ")
                            updateState(ViewStates.Loading)

                        }

                        AsyncStatus.SUCCESS -> {

                            updateState(ViewStates.Success)
                            val suspendTaskDomain = it.data
                            _suspendTaskDomain.update { suspendTaskDomain }

                            }
                        }

                    }
                }



            }

        }




    fun saveSuspendTask() {

                viewModelScope.launch {
                    storeSuspendTask(
                        SuspendTaskDomain(
                            selectedTask.value?.workId ?: 0,
                            suspendReason.value,
                            suspendDescription.value,
                            suspendImageUri.value,
                            0,
                            getCurrentDate(),
                            "",
                            ""
                        )
                    ).collect {
                        when (it.status) {
                            AsyncStatus.ERROR -> {
                                handleError(it.resultStatus)
                                Location.stop()

                            }

                            AsyncStatus.LOADING -> {
                                Napier.log(LogLevel.ASSERT, "getAllWorksUseCase", message = "LOADING: ")
                                updateState(ViewStates.Loading)

                            }

                            AsyncStatus.SUCCESS -> {
                                updateState(ViewStates.Success)
                                Location.stop()

                            }

                        }
                    }
//                }
            }


    }

}