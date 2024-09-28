package presentation.screens.main.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

import com.irancell.nwg.wfm.presentation.components.FilterSectionItem
import presentation.model.FilterType
import com.irancell.nwg.wfm.presentation.model.SelectableItem
import database.entity.GeneralLocationEntity
import domain.models.LiveLocationDomain
import domain.models.PhotoDomain
import presentation.model.StateFilter
import presentation.screens.main.events.MainEvent
import domain.models.SuspendTaskDomain
import domain.models.task.InitFormDomain
import domain.models.task.TaskDomain
import domain.usecase.usecase.auth.LogoutUseCase
import domain.usecase.usecase.availability.ChangeServerAvailabilityUseCase
import domain.usecase.usecase.availability.GetAvailabilityUseCase
import domain.usecase.usecase.initialForm.GetInitialFormByTask
import domain.usecase.usecase.location.DeleteSendLocationUseCase
import domain.usecase.usecase.location.GetGeneralLocationListUseCase
import domain.usecase.usecase.location.SendLocationToServerUseCase
import domain.usecase.usecase.location.UpdateUnSendLocationUseCase
import domain.usecase.usecase.steps.CheckForEditedTicketUseCase
import domain.usecase.usecase.photo.DeleteByComponentKeyUseCase
import domain.usecase.usecase.photo.GetPhotoByComponentKeyUseCase
import domain.usecase.usecase.photo.InsertPhotoUseCase
import domain.usecase.usecase.profile.GetProfileUseCase
import domain.usecase.usecase.steps.UpdateIsEditedTicketUseCase
import domain.usecase.usecase.steps.UpdateStepsUseCase
import domain.usecase.usecase.suspendTask.DeleteByTaskIdUseCase
import domain.usecase.usecase.suspendTask.GetSuspendTaskByIdUseCase
import domain.usecase.usecase.suspendTask.StoreSuspendTaskUseCase
import domain.usecase.usecase.ticket.GetTasksUseCase
import domain.usecase.usecase.ticket.UpdateTaskUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.BackgroundServiceApp
import irancell.nwg.wfm.BackgroundWorker
import irancell.nwg.wfm.Location
import irancell.nwg.wfm.getSharedPref
import irancell.nwg.wfm.openInMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import utils.AsyncResult

import utils.AsyncStatus
import utils.AvailabilityObjectId
import utils.BaseViewModel
import utils.ServiceState
import utils.TicketNumber
import utils.ViewStates
import utils.getCurrentDate

class MainScreenVM(
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
    private val checkForEditedTicketUseCase: CheckForEditedTicketUseCase,
    private val updateIsEditedTicketUseCase: UpdateIsEditedTicketUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val sendLocationToServerUseCase: SendLocationToServerUseCase,
    private val generalLocationListUseCase: GetGeneralLocationListUseCase,
    private val updateUnSendLocationUseCase: UpdateUnSendLocationUseCase,
    private val deleteSendLocationUseCase: DeleteSendLocationUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val updateStepsUseCase: UpdateStepsUseCase,
    private val getInitialFormByTask: GetInitialFormByTask,
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


    private val _ticketIsEdited = MutableStateFlow<Boolean>(false)
    var ticketIsEdited = _ticketIsEdited.asStateFlow()


    private val _ticketNumber =
        MutableStateFlow<String>(getSharedPref().getString(TicketNumber).orEmpty())
    var ticketNumber = _ticketNumber.asStateFlow()
    var selectedTask: MutableState<TaskDomain?> = mutableStateOf(null)

    private val _events = MutableStateFlow<MainEvent>(MainEvent.Default)
    var events = _events.asStateFlow()


    var enableSuspendSubmit = mutableStateOf(false)
    var enableCancelSubmit = mutableStateOf(false)

    var suspendReason = mutableStateOf("")
    var cancelReason = mutableStateOf("")


    var suspendDescription = mutableStateOf("")
    var cancelDescription = mutableStateOf("")


    private val _showAcceptDialog = MutableStateFlow(false)
    var showAcceptDialog = _showAcceptDialog.asStateFlow()


    val generalLocationList = mutableStateListOf<GeneralLocationEntity>()
    val initFormsState = mutableStateListOf<InitFormDomain>()

    init {
        getCurrentAvailability()
        getProfileName()
        getTasks()
        updateTicketNumber("")
    }

    fun updateState(eventState: MainEvent) {
        _events.value = eventState
    }


    private fun startWorkerManager() {
        BackgroundWorker.start(900000L) {
            println("Work executed!      ${" is okeyyyyyyyyy"}")
            sendLocationForServer()
        }
    }

    fun updateReloadState(isLoading: Boolean) {
        _reload.update { true }
    }


    private fun getGeneralUnSendLocationList(): List<LiveLocationDomain> {
        viewModelScope.launch {
            generalLocationListUseCase(
                Unit,
            ).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        updateState(ViewStates.Loading)
                    }

                    AsyncStatus.LOADING -> {
                        updateState(ViewStates.Loading)
                    }

                    AsyncStatus.SUCCESS -> {
                        it.data?.let { locations -> generalLocationList.addAll(locations) }
                        withContext(Dispatchers.Main) {
                            updateState(ViewStates.Success())
                        }
                    }

                    else -> {}
                }
            }
        }

        println("AvailabilityObjectId  ${getSharedPref().getString(AvailabilityObjectId)}")
        return generalLocationList.map { location ->
            LiveLocationDomain(
                latitude = location.latitude.toDouble(),
                longitude = location.longitude.toDouble(),
                recorded_date = location.datetime,
                site = 0,
                attendance = getSharedPref().getString(AvailabilityObjectId)?.toLong() ?: 0,
                ticket_num = getSharedPref().getString(TicketNumber) ?: "",
                network_info = Json.decodeFromString(JsonObject.serializer(), location.networkInfo)
            )
        }
    }

    private fun sendLocationForServer() {
        viewModelScope.launch {
            sendLocationToServerUseCase(Unit).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        println("Work executed!      ${" is ERROR"}")
                    }

                    AsyncStatus.SUCCESS -> {
                        println("Work executed!      ${" is SUCCESS"}")
                        updateSendLocationInDB()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun updateSendLocationInDB() {
        viewModelScope.launch {
            updateUnSendLocationUseCase(Unit).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        println("Work executed!      ${" is ERROR delete"}")
                    }

                    AsyncStatus.SUCCESS -> {
                        println("Work executed!      ${" is SUCCESS delete"}")
                        deleteSendLocationInDB()
                    }

                    else -> {}
                }
            }
        }
    }


    private fun deleteSendLocationInDB() {
        viewModelScope.launch {
            deleteSendLocationUseCase(Unit).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {}
                    AsyncStatus.LOADING -> {}
                    AsyncStatus.EMPTY -> {}
                    AsyncStatus.SUCCESS -> {}
                }
            }
        }
    }

    fun updateIsEditedTicket(isEdited: Boolean) {
        _ticketIsEdited.update { isEdited }
    }

    fun updateShowAcceptDialog(showDialog: Boolean) {
        _showAcceptDialog.update { showDialog }
    }

    fun updatePositionSelected(position: Int) {
        _positionSelected.update { position }
    }

    private fun getProfileName() {
        viewModelScope.launch {
            getProfileUseCase(Unit).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)
                    }

                    AsyncStatus.SUCCESS -> {
                        it.data?.let {
                            val name = it.firstName + " " + it.lastName
                            updateState(ViewStates.Success())
                            _profileName.update { name }
                        }
                    }

                    else -> {}
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
                            if (_availability.value) {
                                BackgroundServiceApp.startBackgroundService()
                                BackgroundServiceApp.updateServiceState(ServiceState.Normal)
//                                delay(1000)
//                                startWorkerManager()
                            } else BackgroundServiceApp.stopBackgroundService()
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun changeAvailability() {
        viewModelScope.launch(Dispatchers.IO) {
            changeServerAvailabilityUseCase(Unit).collect { result ->
                when (result) {
                    is AsyncResult.Error -> {
                        handleError(result.resultStatus)
                    }

                    is AsyncResult.Loading -> {
                        updateState(ViewStates.Loading)
                    }

                    is AsyncResult.Success -> {
                        _availability.update { result.data ?: false }
                        updateState(ViewStates.Success())
                        updateState(MainEvent.Default)
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

                    else -> {}
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
        "Severity Level", arrayListOf(
            StateFilter(1, "1", false, FilterType.SEVERITY_LEVEL),
            StateFilter(2, "2", false, FilterType.SEVERITY_LEVEL),
            StateFilter(3, "3", false, FilterType.SEVERITY_LEVEL),
            StateFilter(4, "4", false, FilterType.SEVERITY_LEVEL),
            StateFilter(5, "5", false, FilterType.SEVERITY_LEVEL)
        )
    )

    val items2 = FilterSectionItem(
        "Region", arrayListOf(
            StateFilter(1, "R1", false, FilterType.REGION),
            StateFilter(2, "R2", false, FilterType.REGION),
            StateFilter(3, "R3", false, FilterType.REGION),
            StateFilter(4, "R4", false, FilterType.REGION),
            StateFilter(5, "R5", false, FilterType.REGION),
            StateFilter(6, "R6", false, FilterType.REGION),
            StateFilter(7, "R7", false, FilterType.REGION),
            StateFilter(8, "R8", false, FilterType.REGION),
            StateFilter(9, "R9", false, FilterType.REGION),
            StateFilter(10, "R10", false, FilterType.REGION)
        )
    )

    val items3 = FilterSectionItem(
        "State", arrayListOf(
            StateFilter(1, "Running", false, FilterType.STATE),
            StateFilter(2, "Draft", false, FilterType.STATE),
            StateFilter(3, "Cancel", false, FilterType.STATE),
            StateFilter(4, "Suspended", false, FilterType.STATE),
            StateFilter(5, "Completed", false, FilterType.STATE)
        )
    )

    val filterSectionItems: MutableList<FilterSectionItem> = mutableListOf(items, items2, items3)

    data class FilterModel(val type: FilterType, val filter: StateFilter)

    fun getActiveFilterItems() {
        val filterMaps: ArrayList<FilterModel> = arrayListOf()

        filterSectionItems.forEach { section ->
            section.filterStates.forEach { filterState ->
                if (filterState.isActive) {
                    filterMaps.add(FilterModel(filterState.type, filterState))
                }
            }
        }

        println("Active filters: $filterMaps")
        println("Original tasks: $tasks")

        var tasksList: List<TaskDomain> = tasks.toList()

        for (key in filterMaps) {
            tasksList = when (key.type) {
                FilterType.REGION -> {
                    val filteredByRegion =
                        tasksList.filter { it.basic_info.region == key.filter.title }
                    println("Filtering by region (${key.filter.title}): $filteredByRegion")
                    filteredByRegion
                }

                FilterType.STATE -> {
                    val filteredByState =
                        tasksList.filter { it.basic_info.ticket_state == key.filter.title }
                    println("Filtering by state (${key.filter.title}): $filteredByState")
                    filteredByState
                }

                FilterType.SEVERITY_LEVEL -> {
                    val filteredBySeverity =
                        tasksList.filter { it.basic_info.level == key.filter.title }
                    println("Filtering by severity (${key.filter.title}): $filteredBySeverity")
                    filteredBySeverity
                }

                else -> tasksList
            }
            println("TasksList after applying ${key.type}: $tasksList")
        }

        tasks.clear()
        tasks.addAll(tasksList)

        println("Final filtered tasks: $tasks")
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

    fun updateTicketNumber(ticketNum: String) {
        _ticketNumber.update { ticketNum }
        getSharedPref().put(TicketNumber, ticketNumber.value)
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
                            LogLevel.ASSERT, "getAllWorksUseCase", message = "ERROR: " + it.message
                        )
                    }

                    AsyncStatus.LOADING -> {
                        _reload.update { false }
                        Napier.log(LogLevel.ASSERT, "getAllWorksUseCase", message = "LOADING: ")
                        // updateState(ViewStates.Loading)
                    }

                    AsyncStatus.EMPTY -> {
                        tasks.clear()
                        updateState(ViewStates.EMPTY)
                        Napier.log(
                            LogLevel.ASSERT, "getAllWorksUseCase", message = "EMPTY: ${it.data}"
                        )
                        _reload.update { true }
                    }

                    AsyncStatus.SUCCESS -> {
                        tasks.clear()
                        updateState(ViewStates.Success())
                        Napier.log(
                            LogLevel.ASSERT, "getAllWorksUseCase", message = "SUCCESS: " + it.data
                        )
                        it.data?.let { it1 ->
                            tasks.addAll(it1)
                            _reload.update { true }
                            getActiveFilterItems()
                        }
                        Napier.log(
                            LogLevel.ASSERT, "getAllWorksUseCase", message = "initialTasks: $tasks"
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

                        else -> {}
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

                        else -> {}
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

                    else -> {}
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

        val uriAttachment =
            if (suspendTaskDomain.value.attachmentsUri.isEmpty()) suspendTaskDomain.value.attachmentsUri.plus(
                imgUri
            )
            else suspendTaskDomain.value.attachmentsUri.plus(",$imgUri")

        suspendTaskDomain.value = SuspendTaskDomain(
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
            selectedTask.value?.basic_info?.ticket_number ?: "0", "0", "0", 0, "", "", "0"
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
                                photoDomain.value = PhotoDomain(
                                    selectedTask.value?.basic_info?.ticket_number ?: "0",
                                    "0",
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

                    else -> {}
                }
            }
        }
    }

    private fun updatePhotoDomain(imgUri: String) {

        photoDomain.value = PhotoDomain(
            selectedTask.value?.basic_info?.ticket_number ?: "0", "0", "0", 0, imgUri, "", "0"
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

                        else -> {}
                    }
                }
            }
        }
    }


    fun updateSuspendTicketImageUriForDeletePhoto(imgUri: String, po: Int) {

        val attachmentUriList: List<String> = suspendTaskDomain.value.attachmentsUri.split(",")
        val updatedList = attachmentUriList.filter { it != imgUri }
        val updatedListAsString = updatedList.joinToString(",")

        suspendTaskDomain.value = SuspendTaskDomain(
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
        updateState(MainEvent.SuspendTicket)

//        if (photoDomainList.size == 0) {
//            events.value = MainEvent.SuspendTicket
//        } else {
//            events.value = MainEvent.PhotoPreview
//
//        }
    }

    fun checkIfTicketIsEdited() {
        viewModelScope.launch {
            checkForEditedTicketUseCase(
                selectedTask.value?.basic_info?.ticket_number ?: ""
            ).collect {
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
                            if (!isEdited) updateShowAcceptDialog(true)

                        }
                        updateState(ViewStates.Success())
                        updateTicketNumber(selectedTask.value?.basic_info?.ticket_number ?: "")

                    }

                    else -> {}
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

                        else -> {}
                    }
                }
            }
        }
    }

    fun updateEdited(isEdited: Boolean) {
        viewModelScope.launch {
            updateIsEditedTicketUseCase(
                Pair(
                    selectedTask.value?.basic_info?.ticket_number.toString(), isEdited
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
                        checkIfTicketIsEdited()
                    }

                    else -> {}
                }
            }
        }
    }

    fun logoutCallApi() {
        viewModelScope.launch {
            logoutUseCase(Unit).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        println("apiiLogout   ${"ERROR"}")
                    }

                    AsyncStatus.SUCCESS -> {
                        println("apiiLogout   ${"success"}")
                    }

                    else -> {}
                }
            }
        }
    }

    suspend fun updateTask() {
        updateTaskUseCase(Unit).collect {
            when (it.status) {
                AsyncStatus.ERROR -> {
                    println("TaskCallApi${"ERROR"}")
                }

                AsyncStatus.SUCCESS -> {
                    updateSteps()
                    println("PullToRefreshCallApi${"SUCCESS"}")
                }

                else -> {}
            }
        }
    }

    private suspend fun updateSteps() {
        updateStepsUseCase(Unit).collect {
            when (it.status) {
                AsyncStatus.ERROR -> {
                    Napier.log(
                        LogLevel.ASSERT, "updateSteps", message = "ERROR: " + it.message
                    )
                }

                AsyncStatus.LOADING -> {
                    Napier.log(LogLevel.ASSERT, "updateSteps", message = "LOADING: ")
                }

                AsyncStatus.SUCCESS -> {
                    getTasks()

                    Napier.log(
                        LogLevel.ASSERT, "" + "", message = "SUCCESS: " + it.data
                    )
                }

                else -> {}
            }
        }
    }

    fun openInMapHandler() {
        viewModelScope.launch {
            getInitialFormByTask(
                selectedTask.value?.basic_info?.ticket_number ?: ""
            ).collect { it ->
                when (it.status) {
                    AsyncStatus.SUCCESS -> {
                        it.data?.let {
                            val location = it.initForms.firstOrNull { it.key == "location" }?.value
                            val latLong = location?.split(",")
                            if (latLong != null && latLong.size == 2) {
                                openInMap(latLong[0], latLong[1])
                                updateState(MainEvent.Default)
                            } else {
                                updateState(MainEvent.NoLocationFound)
//                                openInMap("35.715298", "51.404343")
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}