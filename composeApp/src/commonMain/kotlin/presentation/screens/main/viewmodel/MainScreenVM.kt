package presentation.screens.main.viewmodel

import androidx.compose.runtime.MutableState
import irancell.nwg.wfm.GpsTrackingService
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

import com.irancell.nwg.wfm.presentation.components.FilterSectionItem
import com.irancell.nwg.wfm.presentation.model.FilterType
import com.irancell.nwg.wfm.presentation.model.SelectableItem
import com.irancell.nwg.wfm.presentation.model.StateFilter
import com.irancell.nwg.wfm.presentation.model.Task
import presentation.screens.main.events.MainEvent
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import domain.usecase.usecase.availability.ChangeServerAvailabilityUseCase
import domain.usecase.usecase.availability.GetAvailabilityUseCase
import domain.usecase.usecase.availability.StoreAvailabilityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import utils.AsyncResult

import utils.AsyncStatus
import utils.BaseViewModel
import utils.ViewStates

class MainScreenVM(
    private val storeAvailabilityUseCase: StoreAvailabilityUseCase,
    private val getAvailabilityUseCase: GetAvailabilityUseCase,
    private val changeServerAvailabilityUseCase: ChangeServerAvailabilityUseCase,
) : BaseViewModel() {
    private val _availability = MutableStateFlow(false)
    val availability = _availability.asStateFlow()

    private val _openCamera = MutableStateFlow(false)
    val openCamera = _openCamera.asStateFlow()

    init {
        viewModelScope.launch {
            getAvailabilityUseCase(
                Unit
            ).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        state.update { ViewStates.Loading }
                        val errorMessage = it.message!!
                        error.update { errorMessage }
                    }

                    AsyncStatus.LOADING -> {

                    }

                    AsyncStatus.SUCCESS -> {
                        state.update { ViewStates.Success }
                        it.data?.let { available ->
                                _availability.update { available }
                        }
                    }
                }
            }
        }
    }


    fun changeAvailability() {

        viewModelScope.launch {


            changeServerAvailabilityUseCase(!_availability.value).collect{
               when(it){
                   is AsyncResult.Error -> {
                       it.message?.let{ string ->
                           error.update { string }
                       }
                   }
                   is AsyncResult.Loading -> {

                   }
                   is AsyncResult.Success -> {

                       storeAvailabilityUseCase(
                           _availability.value
                       ).collect{
                           when (it.status) {
                               AsyncStatus.ERROR -> {
                                   it.message?.let{ string ->
                                       error.update { string }
                                   }
                               }

                               AsyncStatus.LOADING -> {

                               }

                               AsyncStatus.SUCCESS -> {
                                   state.update { ViewStates.Success }
                                   _availability.update { !it }
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

    private val initialTasks = arrayListOf(
        Task(     1235,
    ),
        Task(
            1236,
            "Huawei External Alarm, T5713, Bater ... Huawei External Alarm, T5713, Bater ...",
            "HSE pre-check",
            "Level 3",
            "CR",
            "Tehran, Amanieh, Zarin stre...",
            "0h 43m",
        ),
        Task(
            1237,

            "Huawei External Alarm, T5722, Bater ... Huawei External Alarm, T5722, Bater ...",
            "HSE pre-check",
            "Level 3",
            "TT",
            "Tehran, Nelson mandela, Zarin stre...",
            "2h 43m"
        ),
        Task(
            1238,

            "Huawei External Alarm, T5742, Bater ... Huawei External Alarm, T5742, Bater ...",
            "HSE pre-check",
            "Level 2",
            "TT",
            "Tehran, Zafar, Zarin stre...",
            "4h 43m"
        ), Task(
            1239,

            "Huawei External Alarm, T5744, Bater ... Huawei External Alarm, T5744, Bater ...",
            "Departed",
            "Level 2",
            "CR",
            "Tehran, Takhti, Zarin stre...",
            "1h 43m"
        ), Task(
            1339,

            "Huawei External Alarm, T5754, Bater ... Huawei External Alarm, T5754, Bater ...",
            "Departed",
            "Level 1",
            "PT",
            "Tehran, Takhti, Zarin stre...",
            "1h 43m"
        ), Task(
            1439,

            "Huawei External Alarm, T5754, Bater ... Huawei External Alarm, T5754, Bater ...",
            "Departed",
            "Level 1",
            "TT",
            "Tehran, Mirdamad, Zarin stre...",
            "4h 43m"
        ), Task(
            1429,

            "Huawei External Alarm, T3754, Bater ... Huawei External Alarm, T3754, Bater ...",
            "Departed",
            "Level 1",
            "CR",
            "Tehran, Ghoba, Zarin stre...",
            "2h 44m"
        )
    )


    var tasks = ArrayList(initialTasks)
    var selectedTask : MutableState<Task?> = mutableStateOf(null)

    var events = mutableStateOf<MainEvent>(MainEvent.Default)



    var enableSuspendSubmit = mutableStateOf(false)
    var enableCancelSubmit = mutableStateOf(false)

    var suspendReason = mutableStateOf("")
    var cancelReason = mutableStateOf("")


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

        val filteredList = arrayListOf<Task>()

        filterSectionItems.forEach {
            it.filterStates.forEach {
                if (it.isActive)
                    filterMaps.add(FilterModel(it.type, it))
            }


        }
        val tasksList: ArrayList<Task> = arrayListOf()
        tasksList.addAll(initialTasks)
        for (key in filterMaps) {
            when (key.type) {
                FilterType.CURRENT_STEP -> {

                    val ix = tasksList.filter {
                        it.step.trim() == key.filter.title.trim()
                    }
                    tasksList.clear()
                    tasksList.addAll(ix)
                    filteredList.addAll(ix)

                }

                FilterType.DEFAULT -> {

                }

                FilterType.SEVERITY_LEVEL -> {
                    val ix = tasksList.filter { it.faultLevel.trim() == key.filter.title.trim() }
                    tasksList.clear()
                    tasksList.addAll(ix)
                    filteredList.addAll(ix)

                }

                FilterType.SLA_STATUS -> {

                }

                FilterType.TICKET_TYPE -> {
                    val ix = tasksList.filter { it.type.trim() == key.filter.title.trim() }
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
        tasks.addAll(initialTasks)
    }

    fun openCamera(permissionsController: PermissionsController) {
        viewModelScope.launch {
            val cameraPermission = Permission.CAMERA
            val isGranted = permissionsController.isPermissionGranted(cameraPermission)
            if (isGranted){
                _openCamera.update { true }
            }else{
                permissionsController.providePermission(cameraPermission)
            }
        }
    }

    fun updateCameraStatus(openCamera : Boolean) {
        _openCamera.update { openCamera }
    }

}