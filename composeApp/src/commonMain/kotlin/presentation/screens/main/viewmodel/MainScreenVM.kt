package presentation.screens.main.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

import com.irancell.nwg.wfm.presentation.components.FilterSectionItem
import presentation.model.FilterType
import com.irancell.nwg.wfm.presentation.model.SelectableItem
import domain.models.PhotoDomain
import presentation.model.StateFilter
import presentation.screens.main.events.MainEvent
import domain.models.SuspendTaskDomain
import domain.models.task.TaskDomain
import domain.usecase.usecase.availability.ChangeServerAvailabilityUseCase
import domain.usecase.usecase.availability.GetAvailabilityUseCase
import domain.usecase.usecase.availability.StoreAvailabilityUseCase
import domain.usecase.usecase.steps.CheckForEditedTicketUseCase
import domain.usecase.usecase.photo.DeleteByComponentKeyUseCase
import domain.usecase.usecase.photo.GetPhotoByComponentKeyUseCase
import domain.usecase.usecase.photo.InsertPhotoUseCase
import domain.usecase.usecase.profile.GetProfileUseCase
import domain.usecase.usecase.suspendTask.DeleteByTaskIdUseCase
import domain.usecase.usecase.suspendTask.GetSuspendTaskByIdUseCase
import domain.usecase.usecase.suspendTask.StoreSuspendTaskUseCase
import domain.usecase.usecase.ticket.GetTasksUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.BackgroundServiceApp
import irancell.nwg.wfm.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import utils.AsyncResult

import utils.AsyncStatus
import utils.BaseViewModel
import utils.ServiceState
import utils.ViewStates
import utils.getCurrentDate

class MainScreenVM(
    private val storeAvailabilityUseCase: StoreAvailabilityUseCase,
    private val getAvailabilityUseCase: GetAvailabilityUseCase,
    private val changeServerAvailabilityUseCase: ChangeServerAvailabilityUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val storeSuspendTaskUseCase: StoreSuspendTaskUseCase,
    private val getSuspendTaskByIdUseCase: GetSuspendTaskByIdUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val deleteByTaskIdUseCase: DeleteByTaskIdUseCase,
    private val insertPhotoUseCase: InsertPhotoUseCase,
    private val getPhotoByComponentKeyUseCase: GetPhotoByComponentKeyUseCase,
    private val deleteByComponentKeyUseCase: DeleteByComponentKeyUseCase,
    private val checkForEditedTicketUseCase: CheckForEditedTicketUseCase
    ) : BaseViewModel() {
    private val _availability = MutableStateFlow(false)
    val availability = _availability.asStateFlow()

    private val _openCamera = MutableStateFlow(false)
    val openCamera = _openCamera.asStateFlow()
    val tasks = mutableStateListOf<TaskDomain>()

    private val _profileName = MutableStateFlow("")
    val profileName = _profileName.asStateFlow()

    private val _reload = MutableStateFlow(false)
    val reload = _reload.asStateFlow()


    private val _positionSelected = MutableStateFlow(0)
    val positionSelected = _positionSelected.asStateFlow()


    private val _ticketIsEdited = MutableStateFlow<Boolean?>(null)
    var ticketIsEdited = _ticketIsEdited.asStateFlow()


    var selectedTask: MutableState<TaskDomain?> = mutableStateOf(null)

    var events = mutableStateOf<MainEvent>(MainEvent.Default)


    var enableSuspendSubmit = mutableStateOf(false)
    var enableCancelSubmit = mutableStateOf(false)

    var suspendReason = mutableStateOf("")
    var cancelReason = mutableStateOf("")


    var suspendDescription = mutableStateOf("")
    var cancelDescription = mutableStateOf("")


    private val _showAcceptDialog = MutableStateFlow(false)
    var showAcceptDialog = _showAcceptDialog.asStateFlow()



    init {
        getCurrentAvailability()
        getProfileName()
        getTasks()
    }

    fun updateShowAcceptDialog(showDialog : Boolean){
        _showAcceptDialog.update { showDialog }
    }
    fun updatePositionSelected(position: Int) {
        _positionSelected.update { position }
    }

    private fun getProfileName() {
        viewModelScope.launch {
            getProfileUseCase(Unit)
                .collect {
                    when (it.status) {
                        AsyncStatus.ERROR -> {
                            handleError(it.resultStatus)
                        }

                        AsyncStatus.LOADING -> {

                        }

                        AsyncStatus.SUCCESS -> {
                            it.data?.let {
                                val name = it.firstName + " " + it.lastName
                                updateState(ViewStates.Success())
                                _profileName.update { name }
                            }

                        }
                    }
                }
        }
    }


    private fun getCurrentAvailability() {
        Napier.log(LogLevel.ASSERT, "getCurrentAvailability", message = "sdasddad")

        viewModelScope.launch(Dispatchers.Main) {
            getAvailabilityUseCase(
                Unit
            ).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)
                        Napier.log(LogLevel.ASSERT, "resrrrr", message = it.resultStatus.toString())
                    }

                    AsyncStatus.LOADING -> {
                        updateState(ViewStates.Loading)
                    }

                    AsyncStatus.SUCCESS -> {
                        updateState(ViewStates.Success())

                        println("testtttttttavaliblity ${it.data}")
                        it.data?.let { available ->
                            _availability.update { available }
                        }
                    }
                }
            }
        }
    }


    fun changeAvailability() {

        viewModelScope.launch(Dispatchers.IO) {


            changeServerAvailabilityUseCase(!_availability.value).collect {
                when (it) {
                    is AsyncResult.Error -> {
                        handleError(it.resultStatus)
                    }

                    is AsyncResult.Loading -> {
                        updateState(ViewStates.Loading)
                    }

                    is AsyncResult.Success -> {

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
                                    updateState(ViewStates.Success())
                                    events.value = MainEvent.Default
                                    Napier.log(
                                        LogLevel.ASSERT,
                                        "storeAvailabilityUseCase start back",
                                        message = _availability.value.toString()
                                    )
                                    if (_availability.value) {
                                        BackgroundServiceApp.startBackgroundService()
                                        BackgroundServiceApp.updateServiceState(ServiceState.Normal)
                                        getTasks()

                                    } else {
                                        Napier.log(
                                            LogLevel.ASSERT,
                                            "storeAvailabilityUseCase stop back",
                                            message = _availability.value.toString()
                                        )
                                        BackgroundServiceApp.stopBackgroundService()
                                    }

                                }
                            }
                        }
                    }
                }
            }


        }


    }


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


                }

                FilterType.DEFAULT -> {

                }

                FilterType.SEVERITY_LEVEL -> {

                }

                FilterType.SLA_STATUS -> {

                }

                FilterType.TICKET_TYPE -> {
                    val ix =
                        tasksList.filter { it.basic_info.ticket_state?.trim() == key.filter.title.trim() }
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
        }
    }

    fun updateCameraStatus(openCamera: Boolean) {
        _openCamera.update { openCamera }
    }

    fun getTasks() {

        viewModelScope.launch(Dispatchers.Main) {
            delay(1000)
            getTasksUseCase(Unit).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        //handleError(it.resultStatus)
                        handleError(it.resultStatus)
                        Napier.log(
                            LogLevel.ASSERT,
                            "getAllWorksUseCase",
                            message = "ERROR: " + it.message
                        )

                    }

                    AsyncStatus.LOADING -> {
                        Napier.log(LogLevel.ASSERT, "getAllWorksUseCase", message = "LOADING: ")
                        // updateState(ViewStates.Loading)

                    }

                    AsyncStatus.SUCCESS -> {
                        tasks.clear()


                        Napier.log(
                            LogLevel.ASSERT,
                            "getAllWorksUseCase",
                            message = "SUCCESS: " + it.data
                        )
                        it.data?.let { it1 ->
                            tasks.addAll(it1)
                            _reload.update { true }

                        }

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







    val suspendTaskDomain = MutableStateFlow<SuspendTaskDomain>(
        SuspendTaskDomain(
            selectedTask.value?.basic_info?.ticket_number ?: "0",
            "",
            "",
            "",
            0,
            getCurrentDate(),
            Location.getLastLocation().latitude,
            Location.getLastLocation().longitude
        )
    )


    fun loadSuspendTask() {
        viewModelScope.launch {
            selectedTask.value?.let {
                getSuspendTaskByIdUseCase(it.basic_info.ticket_number ?: "0").collect {
                    when (it.status) {
                        AsyncStatus.ERROR -> {
//                            handleError(it.resultStatus)
                        }

                        AsyncStatus.LOADING -> {
                            Napier.log(LogLevel.ASSERT, "getAllWorksUseCase", message = "LOADING: ")
//                            updateState(ViewStates.Loading)

                        }

                        AsyncStatus.SUCCESS -> {
                            updateState(ViewStates.Success())
                            val suspendTask = it.data
                            suspendTask?.let {
                                suspendTaskDomain.value = it
                                getPhotoByComponentKey()
                            }
                        }
                    }
                }
            }
        }
    }


    fun saveSuspendTask() {
        Location.start { }
        Napier.log(LogLevel.ASSERT, "atttac", message = suspendTaskDomain.value.attachmentsUri)


        viewModelScope.launch {
            selectedTask.value?.basic_info?.ticket_number?.let {
                deleteByTaskIdUseCase(it).collect {
                    when (it.status) {
                        AsyncStatus.ERROR -> {
                            handleError(it.resultStatus)
                            Location.stop()

                        }

                        AsyncStatus.LOADING -> {
                            Napier.log(LogLevel.ASSERT, "saveSuspendTask", message = "LOADING: ")
                            updateState(ViewStates.Loading)

                        }

                        AsyncStatus.SUCCESS -> {

                            storeSuspendTask()
                            saveAndDeletePhotoByComponentKey()


                        }

                    }
                }
            }


        }


    }

    private fun storeSuspendTask() {
        viewModelScope.launch {
            storeSuspendTaskUseCase(
                SuspendTaskDomain(
                    suspendTaskDomain.value.ticket_number,
                    suspendTaskDomain.value.reason,
                    suspendTaskDomain.value.description,
                    suspendTaskDomain.value.attachmentsUri,
                    0,
                    getCurrentDate(),
                    Location.getLastLocation().latitude,
                    Location.getLastLocation().longitude
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
                        updateState(ViewStates.Success())
                        Location.stop()

                    }

                }
            }
        }
    }

    fun updateSuspendTicketReason(reason: String) {

        suspendTaskDomain.value.reason = reason


    }

    fun updateSuspendTicketDescription(description: String) {
        suspendTaskDomain.value.description = description

    }

    fun updateSuspendTicketImageUri(imgUri: String) {

        val uriAttachment = if (suspendTaskDomain.value.attachmentsUri.isEmpty())
            suspendTaskDomain.value.attachmentsUri.plus(imgUri)
        else
            suspendTaskDomain.value.attachmentsUri.plus(",$imgUri")

        suspendTaskDomain.value =
            SuspendTaskDomain(
                suspendTaskDomain.value.ticket_number,
                suspendTaskDomain.value.reason,
                suspendTaskDomain.value.description,
                uriAttachment,
                0,
                suspendTaskDomain.value.datetime,
                suspendTaskDomain.value.latitude,
                suspendTaskDomain.value.longitude
            )



        updatePhotoDomain(imgUri)


    }


    fun resetSuspendTask() {
        suspendTaskDomain.value = SuspendTaskDomain(
            selectedTask.value?.basic_info?.ticket_number ?: "0",
            "",
            "",
            "",
            0,
            getCurrentDate(),
            Location.getLastLocation().latitude,
            Location.getLastLocation().longitude
        )
    }


/////////////////////////////photo//////////////////////////////////////////////////////////


    private val photoDomain = MutableStateFlow<PhotoDomain>(
        PhotoDomain(
            selectedTask.value?.basic_info?.ticket_number ?: "0",
            "0",
            0,
            "",
            "",
            "0"
        )
    )
    var photoDomainList = mutableStateListOf<PhotoDomain>()


    private fun getPhotoByComponentKey() {

        viewModelScope.launch {

            getPhotoByComponentKeyUseCase(
                selectedTask.value?.basic_info?.ticket_number ?: "0"
            ).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)
                    }

                    AsyncStatus.LOADING -> {
                        Napier.log(LogLevel.ASSERT, "getAllPhotoUseCase", message = "LOADING: ")
                        updateState(ViewStates.Loading)

                    }

                    AsyncStatus.SUCCESS -> {
                        photoDomainList.clear()
                        updateState(ViewStates.Success())


                        it.data?.let { it1 ->
                            for (i in it1.indices) {
                                photoDomain.value =
                                    PhotoDomain(
                                        selectedTask.value?.basic_info?.ticket_number ?: "0",
                                        "0",
                                        i.toLong(),
                                        it1[i].origin_uri,
                                        it1[i].edited_uri,
                                        it1[i].angle
                                    )

                                photoDomainList.add(photoDomain.value)
                            }
                        }


                    }

                }
            }
        }


    }

    private fun updatePhotoDomain(imgUri: String) {

        photoDomain.value =
            PhotoDomain(
                selectedTask.value?.basic_info?.ticket_number ?: "0",
                "0",
                0,
                imgUri,
                "",
                "0"
            )
        photoDomainList.add(photoDomain.value)

    }

    private fun insertNewPhoto() {

        viewModelScope.launch {

            for (i in photoDomainList.indices) {
                insertPhotoUseCase(
                    PhotoDomain(
                        selectedTask.value?.basic_info?.ticket_number ?: "0",
                        "0",
                        photoDomainList[i].index_row,
                        photoDomainList[i].origin_uri,
                        photoDomainList[i].edited_uri,
                        photoDomainList[i].angle
                    )

                ).collect {
                    when (it.status) {
                        AsyncStatus.ERROR -> {
                            handleError(it.resultStatus)

                        }

                        AsyncStatus.LOADING -> {
                            updateState(ViewStates.Loading)

                        }

                        AsyncStatus.SUCCESS -> {
                            updateState(ViewStates.Success())


                        }

                    }
                }

            }


        }

    }


    fun updateSuspendTicketImageUriForDeletePhoto(imgUri: String, po: Int) {

        val attachmentUriList: List<String> = suspendTaskDomain.value.attachmentsUri.split(",")
        val updatedList = attachmentUriList.filter { it != imgUri }
        val updatedListAsString = updatedList.joinToString(",")

        suspendTaskDomain.value =
            SuspendTaskDomain(
                suspendTaskDomain.value.ticket_number,
                suspendTaskDomain.value.reason,
                suspendTaskDomain.value.description,
                updatedListAsString,
                0,
                suspendTaskDomain.value.datetime,
                suspendTaskDomain.value.latitude,
                suspendTaskDomain.value.longitude
            )
        photoDomainList.removeAt(po)
        events.value = MainEvent.SuspendTicket

//        if (photoDomainList.size == 0) {
//            events.value = MainEvent.SuspendTicket
//        } else {
//            events.value = MainEvent.PhotoPreview
//
//        }
    }

     fun checkIfTicketIsEdited(){
        viewModelScope.launch {
            checkForEditedTicketUseCase(selectedTask.value?.basic_info?.ticket_number?:"").collect{
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)

                    }

                    AsyncStatus.LOADING -> {
                        Napier.log(LogLevel.ASSERT, "saveSuspendTask", message = "LOADING: ")
                        updateState(ViewStates.Loading)

                    }

                    AsyncStatus.SUCCESS -> {
                        it.data?.let { isEdited ->
                            _ticketIsEdited.update { isEdited }
                        }
                        updateState(ViewStates.Success())

                    }

                }

            }
        }
    }

    private fun saveAndDeletePhotoByComponentKey() {
        viewModelScope.launch {
            selectedTask.value?.basic_info?.ticket_number?.let {
                deleteByComponentKeyUseCase(it).collect {
                    when (it.status) {
                        AsyncStatus.ERROR -> {
                            handleError(it.resultStatus)
                            Location.stop()

                        }

                        AsyncStatus.LOADING -> {
                            Napier.log(LogLevel.ASSERT, "saveSuspendTask", message = "LOADING: ")
                            updateState(ViewStates.Loading)

                        }

                        AsyncStatus.SUCCESS -> {
                            updateState(ViewStates.Success())
                            insertNewPhoto()
                        }

                    }
                }
            }


        }

    }

}