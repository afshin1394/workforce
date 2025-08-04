package presentation.screens.main.viewmodel

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.irancell.nwg.wfm.presentation.components.FilterSectionItem
import presentation.model.FilterType
import com.irancell.nwg.wfm.presentation.model.SelectableItem
import com.plusmobileapps.konnectivity.Konnectivity
import com.plusmobileapps.konnectivity.NetworkConnection
import database.entity.GeneralLocationEntity
import domain.models.LiveLocationDomain
import domain.models.PhotoDomain
import presentation.model.StateFilter
import presentation.screens.main.events.MainEvent
import domain.models.SuspendTaskDomain
import domain.models.task.ActivityListDomain
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
import domain.usecase.usecase.suspendTask.DeleteByTaskIdUseCase
import domain.usecase.usecase.suspendTask.GetSuspendTaskByIdUseCase
import domain.usecase.usecase.suspendTask.StoreSuspendTaskUseCase
import domain.usecase.usecase.ticket.GetActivityListUseCase
import domain.usecase.usecase.ticket.GetTasksUseCase
import domain.usecase.usecase.ticket.GetTasksPaginatedUseCase
import domain.usecase.usecase.ticket.PaginationParams
import domain.usecase.usecase.ticket.PaginatedTasksResult
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
import utils.AvailabilityStatus
import utils.BaseViewModel
import utils.ServiceState
import utils.TicketId
import utils.TicketNumber
import utils.ViewStates
import utils.getCurrentDate

sealed class TicketListStatus {
    data object UnRecognized : TicketListStatus()
    data object Filled : TicketListStatus()
    data object Empty : TicketListStatus()
    data object Loading : TicketListStatus()
}

class MainScreenVM(
    private val getAvailabilityUseCase: GetAvailabilityUseCase,
    private val changeServerAvailabilityUseCase: ChangeServerAvailabilityUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val getTasksPaginatedUseCase: GetTasksPaginatedUseCase,
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
    private val getInitialFormByTask: GetInitialFormByTask,
    private val getActivityListUseCase: GetActivityListUseCase
) : BaseViewModel() {
    private val _availability = MutableStateFlow(false)
    val availability = _availability.asStateFlow()
    private val _openCamera = MutableStateFlow(false)
    val openCamera = _openCamera.asStateFlow()
    private val _tasks = mutableStateListOf<TaskDomain>()
    val tasks: List<TaskDomain> = _tasks

    private val _tasksActivityList = mutableStateListOf<ActivityListDomain>()
    val tasksActivityList: List<ActivityListDomain> = _tasksActivityList

    // Pagination state
    private val _currentPage = MutableStateFlow(0)
    val currentPage = _currentPage.asStateFlow()
    
    private val _totalPages = MutableStateFlow(0)
    val totalPages = _totalPages.asStateFlow()
    
    private val _totalTaskCount = MutableStateFlow(0)
    val totalTaskCount = _totalTaskCount.asStateFlow()
    
    private val _hasNextPage = MutableStateFlow(false)
    val hasNextPage = _hasNextPage.asStateFlow()
    
    private val _hasPreviousPage = MutableStateFlow(false)
    val hasPreviousPage = _hasPreviousPage.asStateFlow()
    
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore = _isLoadingMore.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    
    companion object {
        const val PAGE_SIZE = 10
    }


    private val _profileName = MutableStateFlow("")
    val profileName = _profileName.asStateFlow()
    private val _reload = MutableStateFlow(false)
    val reload = _reload.asStateFlow()
    private val _positionSelected = MutableStateFlow(0)
    val positionSelected = _positionSelected.asStateFlow()
    private val _ticketIsEdited = MutableStateFlow<Boolean>(false)
    var ticketIsEdited = _ticketIsEdited.asStateFlow()
    private val _ticketNumber =
        MutableStateFlow(getSharedPref().getString(TicketNumber).orEmpty())
    var ticketNumber = _ticketNumber.asStateFlow()
    private val _ticketId =
        MutableStateFlow(getSharedPref().getString(TicketId).orEmpty())
    var ticketId = _ticketId.asStateFlow()
    var selectedTask: MutableState<TaskDomain?> = mutableStateOf(null)
    private val _events = MutableStateFlow<MainEvent>(MainEvent.Default)
    var events = _events.asStateFlow()
    var enableCancelSubmit = mutableStateOf(false)
    var suspendReason = mutableStateOf("")
    var cancelReason = mutableStateOf("")
    private val _showAcceptDialog = MutableStateFlow(false)
    var showAcceptDialog = _showAcceptDialog.asStateFlow()
    
    // Navigation control to prevent multiple navigations
    private val _navigationRequest = MutableStateFlow<Pair<String, String>?>(null)
    val navigationRequest = _navigationRequest.asStateFlow()
    val generalLocationList = mutableStateListOf<GeneralLocationEntity>()
    val konnectivity: Konnectivity = Konnectivity()
    private val _ticketListStatus =
        MutableStateFlow<TicketListStatus>(TicketListStatus.UnRecognized)
    val ticketListStatus = _ticketListStatus.asStateFlow()
    private var selectedTicketType: String? = null

    init {
        Napier.log(
            LogLevel.ASSERT, "getAllWorksUseCase", message = " call function init"
        )
        traceNetwork()
        getProfileName()
        // Load first page immediately on startup
        loadTasksPaginated(page = 0, isLoadMore = false)
        getActivityList()
        updateTicketNumber("")
        updateTicketId("")
        collectTicketListState()


    }

    fun addTasks(tasks: List<TaskDomain>) {
        _tasks.addAll(tasks)
    }

    fun clearTasks() {
        _tasks.clear()
    }

    fun updateTicketListState(ticketListStatus: TicketListStatus) {
        _ticketListStatus.update { ticketListStatus }
    }

    fun updateState(eventState: MainEvent) {
        _events.value = eventState
    }

    private fun collectTicketListState() {
        viewModelScope.launch {
            BackgroundServiceApp.ticketListState.collect {
                when (it) {
                    TicketListStatus.Filled -> {
                        println("getAllTask onStartCommand: CallApi  ")
                        _ticketListStatus.update { TicketListStatus.Filled }
                    }

                    TicketListStatus.Empty -> {
                        _ticketListStatus.update { TicketListStatus.Empty }
                    }

                    else -> {
                        _ticketListStatus.update { TicketListStatus.UnRecognized }
                    }
                }
            }

        }
    }


    private fun traceNetwork() {
        viewModelScope.launch(Dispatchers.Main) {
            konnectivity.currentNetworkConnectionState.collect { connection ->
                when (connection) {
                    NetworkConnection.NONE -> {
                        updateAvailabilityState(AvailabilityStatus.NoInternet)
                        println("checkUpdate")
                    }

                    else -> {
                        getCurrentAvailability()
                        val serviceRunning = BackgroundServiceApp.isServiceRunning()
                        if (_availability.value) {
                            updateAvailabilityState(
                                if (serviceRunning) AvailabilityStatus.Available else AvailabilityStatus.NotRunning
                            )
                        } else {
                            updateAvailabilityState(AvailabilityStatus.Unavailable)
                        }
                    }
                }
            }
        }
    }

    private fun filterTasksForNoInternet() {


        val filteredTasks = _tasks.filter { it.instancePrefix.contains("w") }


        _tasks.clear()
        _tasks.addAll(filteredTasks)


    }


    private fun startWorkerManager() {
        BackgroundWorker.start(900000L) {
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
                    AsyncStatus.SUCCESS -> {
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
                    AsyncStatus.SUCCESS -> {
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
        if (isEdited) {
            selectedTask.value?.let { task ->
                _navigationRequest.update { 
                    Pair(task.ticket_id.toString(), task.ticket_number ?: "") 
                }
            }
        }
    }

    fun updateShowAcceptDialog(showDialog: Boolean) {
        _showAcceptDialog.update { showDialog }
    }
    
    fun clearNavigationRequest() {
        _navigationRequest.update { null }
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
        println("getCurrentAvailability ")

        viewModelScope.launch(Dispatchers.Main) {
            getAvailabilityUseCase(
                Unit
            ).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus, it.message)
                        Napier.log(LogLevel.ASSERT, "resrrrr", message = it.resultStatus.toString())
                    }

                    AsyncStatus.LOADING -> {
                        updateState(ViewStates.Loading)
                    }

                    AsyncStatus.SUCCESS -> {
                        updateState(ViewStates.Success())
                        it.data?.let { available ->
                            updateAvailabilityState(if (available) AvailabilityStatus.Available else AvailabilityStatus.Unavailable)
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
                        handleError(result.resultStatus, result.message)
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
                            updateAvailabilityState(AvailabilityStatus.Available)
                            BackgroundServiceApp.startBackgroundService()
                            BackgroundServiceApp.updateServiceState(ServiceState.Normal)
                            loadTasksPaginated(page = 0, isLoadMore = false)

                        } else {
                            updateAvailabilityState(AvailabilityStatus.Unavailable)
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


    fun createTicketTypeFilterSection(tasks: List<TaskDomain>): FilterSectionItem {

        val distinctPrefixes = tasks.map { it.instancePrefix }.distinct()


        val prefixFilters = distinctPrefixes.mapIndexed { index, prefix ->
            StateFilter(

                id = index + 1,
                title = prefix,
                isActive = false,
                type = FilterType.TICKET_TYPE
            )
        }


        return FilterSectionItem(
            title = "Ticket Type",
            filterStates = ArrayList(prefixFilters)
        )
    }

    // val filterSectionItems: MutableList<FilterSectionItem> = mutableListOf(items, items2, items3)


    private val _filterSectionItems = mutableStateListOf<FilterSectionItem>()
    val filterSectionItems: MutableList<FilterSectionItem> get() = _filterSectionItems


    private fun buildFiltersFromTasks() {

        val filterTicketType = createTicketTypeFilterSection(_tasks)
        _filterSectionItems.clear()
        /*        _filterSectionItems.add(items)
                _filterSectionItems.add(items2)
                _filterSectionItems.add(items3)*/
        _filterSectionItems.add(filterTicketType)


    }

    data class FilterModel(val type: FilterType, val filter: StateFilter)

    fun getActiveFilterItems() {
        val filterMaps: ArrayList<FilterModel> = arrayListOf()

        filterSectionItems.forEach { section ->
            section.filterStates.forEach { filterState ->
                if (filterState.isActive) {

                    if (filterState.type == FilterType.TICKET_TYPE) {
                        selectedTicketType = filterState.title
                    }
                    filterMaps.add(FilterModel(filterState.type, filterState))
                }
            }
        }

        var tasksList: List<TaskDomain> = tasks.toList()

        for (key in filterMaps) {
            tasksList = when (key.type) {
                FilterType.TICKET_TYPE -> {
                    val filteredByTicketType =
                        tasksList.filter { it.instancePrefix == key.filter.title }
                    filteredByTicketType
                }

                else -> tasksList
            }
        }
        clearTasks()
        addTasks(tasksList)

    }


    /*  fun getActiveFilterItems() {
          val filterMaps: ArrayList<FilterModel> = arrayListOf()

          filterSectionItems.forEach { section ->
              section.filterStates.forEach { filterState ->
                  if (filterState.isActive) {
                      filterMaps.add(FilterModel(filterState.type, filterState))
                  }
              }
          }

          var tasksList: List<TaskDomain> = tasks.toList()

          for (key in filterMaps) {
              tasksList = tasksList.filter { task ->
                  task.properties.any { property ->
                      property.key.equals(key.filter.title , ignoreCase = true) &&
                              property.value.equals(key.filter.title, ignoreCase = true)
                  }
              }
          }

          clearTasks()
          addTasks(tasksList)
      }*/


    fun removeAllFilters() {
        filterSectionItems.forEach {
            it.filterStates.forEach { stateFilter ->
                run {
                    stateFilter.isActiveState = false
                    stateFilter.isActive = false
                }
            }
        }
        selectedTicketType = null
        clearTasks()
        addTasks(tasks)
    }

    fun clearFilters() {
        _filterSectionItems.clear()
    }

    fun openCamera() {
        viewModelScope.launch {
            _openCamera.update { true }
        }
    }

    fun updateCameraStatus(openCamera: Boolean) {
        _openCamera.update { openCamera }
    }

    private var isFiltersBuilt = false
    fun loadTasksPaginated(page: Int = 0, isLoadMore: Boolean = false, searchQuery: String? = null) {
        viewModelScope.launch(Dispatchers.Main) {
            if (!isLoadMore) {
                _isLoadingMore.update { false }
                updateTicketListState(TicketListStatus.Loading)
                updateState(ViewStates.Loading)
            } else {
                _isLoadingMore.update { true }
            }
            
            val params = PaginationParams(
                page = page,
                pageSize = PAGE_SIZE,
                searchQuery = searchQuery?.takeIf { it.isNotBlank() }
            )
            
            getTasksPaginatedUseCase(params).collect { result ->
                when (result.status) {
                    AsyncStatus.ERROR -> {
                        handleError(result.resultStatus, result.message)
                        _isLoadingMore.update { false }
                        if (!isLoadMore) {
                            updateState(ViewStates.Error(result.message ?: "Unknown error"))
                        }
                        Napier.log(LogLevel.ASSERT, "loadTasksPaginated", message = "ERROR: ${result.message}")
                    }

                    AsyncStatus.LOADING -> {
                        if (!isLoadMore) {
                            _reload.update { false }
                        }
                        Napier.log(LogLevel.ASSERT, "loadTasksPaginated", message = "LOADING")
                    }

                    AsyncStatus.EMPTY -> {
                        if (!isLoadMore) {
                            clearTasks()
                            updateState(ViewStates.EMPTY)
                            updateTicketListState(TicketListStatus.Empty)
                        }
                        _isLoadingMore.update { false }
                        updatePaginationState(result.data)
                        Napier.log(LogLevel.ASSERT, "loadTasksPaginated", message = "EMPTY")
                        _reload.update { true }
                    }

                    AsyncStatus.SUCCESS -> {
                        if (!isLoadMore) {
                            clearTasks()
                        }
                        
                        result.data?.let { paginatedResult ->
                            if (isLoadMore) {
                                addTasks(paginatedResult.tasks)
                            } else {
                                addTasks(paginatedResult.tasks)
                            }
                            
                            updatePaginationState(paginatedResult)
                            updateState(ViewStates.Success())
                            updateTicketListState(TicketListStatus.Filled)
                            
                            if (!isFiltersBuilt && !isLoadMore && page == 0) {
                                buildFiltersFromTasks()
                                isFiltersBuilt = true
                            }
                        }
                        
                        _isLoadingMore.update { false }
                        _reload.update { true }
                        Napier.log(LogLevel.ASSERT, "loadTasksPaginated", message = "SUCCESS: page=$page, totalTasks=${result.data?.totalCount}")
                    }
                }
            }
        }
    }

    private fun updatePaginationState(paginatedResult: PaginatedTasksResult?) {
        paginatedResult?.let { result ->
            _currentPage.update { result.currentPage }
            _totalPages.update { result.totalPages }
            _totalTaskCount.update { result.totalCount }
            _hasNextPage.update { result.hasNextPage }
            _hasPreviousPage.update { result.hasPreviousPage }
        }
    }

    fun loadNextPage() {
        if (_hasNextPage.value && !_isLoadingMore.value) {
            loadTasksPaginated(
                page = _currentPage.value + 1,
                isLoadMore = true,
                searchQuery = _searchQuery.value.takeIf { it.isNotBlank() }
            )
        }
    }

    fun loadPreviousPage() {
        if (_hasPreviousPage.value && !_isLoadingMore.value) {
            loadTasksPaginated(
                page = _currentPage.value - 1,
                isLoadMore = false,
                searchQuery = _searchQuery.value.takeIf { it.isNotBlank() }
            )
        }
    }

    fun searchTasks(query: String) {
        _searchQuery.update { query }
        loadTasksPaginated(page = 0, isLoadMore = false, searchQuery = query)
    }

    fun clearSearch() {
        _searchQuery.update { "" }
        loadTasksPaginated(page = 0, isLoadMore = false, searchQuery = null)
    }

    fun refreshTasks() {
        loadTasksPaginated(
            page = 0, 
            isLoadMore = false, 
            searchQuery = _searchQuery.value.takeIf { it.isNotBlank() }
        )
    }

    fun getTasks() {
        viewModelScope.launch(Dispatchers.Main) {
            // Remove the 30-second delay that was causing performance issues
            getTasksUseCase(Unit).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        //handleError(it.resultStatus)
                        handleError(it.resultStatus, it.message)
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
//                        tasks.clear()
                        updateState(ViewStates.EMPTY)
                        Napier.log(
                            LogLevel.ASSERT, "getAllWorksUseCase", message = "EMPTY: ${it.data}"
                        )
                        _reload.update { true }
                    }

                    AsyncStatus.SUCCESS -> {
                        clearTasks()

                        updateState(ViewStates.Success())
                        Napier.log(
                            LogLevel.ASSERT, "getAllWorksUseCase", message = "SUCCESS: " + it.data
                        )
                        it.data?.let { it1 ->


                            addTasks(it1)



                            _reload.update { true }

                            if (!isFiltersBuilt) {

                                buildFiltersFromTasks()
                                isFiltersBuilt = true
                            }



                            konnectivity.currentNetworkConnectionState.collect { connection ->
                                if (connection == NetworkConnection.NONE) {
                                    isFiltersBuilt = false
                                    clearFilters()
                                    filterTasksForNoInternet()

                                } else {
                                    getActiveFilterItems()

                                }
                            }


                        }

                        Napier.log(
                            LogLevel.ASSERT, "getAllWorksUseCase", message = "initialTasks: $tasks"
                        )
                    }
                }
            }
        }
    }

    fun getActivityList() {
        viewModelScope.launch(Dispatchers.Main) {

            getActivityListUseCase(Unit).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {

                        handleError(it.resultStatus, it.message)
                        Napier.log(
                            LogLevel.ASSERT,
                            "getActivityListUseCase",
                            message = "ERROR: " + it.message
                        )
                    }

                    AsyncStatus.LOADING -> {
                        _reload.update { false }
                        Napier.log(LogLevel.ASSERT, "getActivityListUseCase", message = "LOADING: ")

                    }

                    AsyncStatus.EMPTY -> {
                        updateState(ViewStates.EMPTY)
                        Napier.log(
                            LogLevel.ASSERT, "getActivityListUseCase", message = "EMPTY: ${it.data}"
                        )
                        _reload.update { true }
                    }

                    AsyncStatus.SUCCESS -> {
                        _tasksActivityList.clear()
                        updateState(ViewStates.Success())

                        it.data?.let { it1 ->

                            println("Comparing: instancePrefix=    ${it.data}")


                            val filteredActivities = selectedTicketType?.let { prefix ->
                                it1.filter { act ->
                                    println("Comparing: instancePrefix='${act.instancePrefix}', prefix='${prefix}'")
                                    act.instancePrefix.trim()
                                        .equals(prefix.trim(), ignoreCase = true)
                                }
                            } ?: it1

                            _tasksActivityList.addAll(filteredActivities)

                            println("Comparing: instancePrefix=    ${filteredActivities.size}")



                            Napier.log(
                                LogLevel.ASSERT, "getActivityListUseCase", message = "SUCCESS: $it1"
                            )

                        }

                    }
                }
            }
        }
    }

    val suspendTaskDomain = MutableStateFlow<SuspendTaskDomain>(
        SuspendTaskDomain(
            selectedTask.value?.ticket_number ?: "0",
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
                getSuspendTaskByIdUseCase(it.ticket_number ?: "0").collect {
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
            selectedTask.value?.ticket_number?.let {
                deleteByTaskIdUseCase(it).collect {
                    when (it.status) {
                        AsyncStatus.ERROR -> {
                            handleError(it.resultStatus, it.message)
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
                        handleError(it.resultStatus, it.message)
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
            selectedTask.value?.ticket_number ?: "0",
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
            selectedTask.value?.ticket_number ?: "0", "0", "0", 0, "", "", 0F
        )
    )
    var photoDomainList = mutableStateListOf<PhotoDomain>()

    private fun getPhotoByComponentKey() {

        viewModelScope.launch {

            getPhotoByComponentKeyUseCase(
                selectedTask.value?.ticket_number ?: "0"
            ).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus, it.message)
                    }

                    AsyncStatus.LOADING -> {
                        Napier.log(LogLevel.ASSERT, "getAllPhotoUseCase", message = "LOADING: ")
                        updateState(ViewStates.Loading)
                    }

                    AsyncStatus.EMPTY -> {}

                    AsyncStatus.SUCCESS -> {
                        photoDomainList.clear()
                        updateState(ViewStates.Success())


                        it.data?.let { it1 ->
                            for (i in it1.indices) {
                                photoDomain.value = PhotoDomain(
                                    selectedTask.value?.ticket_number ?: "0",
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
                }
            }
        }
    }

    private fun updatePhotoDomain(imgUri: String) {

        photoDomain.value = PhotoDomain(
            selectedTask.value?.ticket_number ?: "0", "0", "0", 0, imgUri, "", 0F
        )
        photoDomainList.add(photoDomain.value)
    }

    private fun insertNewPhoto() {

        viewModelScope.launch {

            for (i in photoDomainList.indices) {
                insertPhotoUseCase(
                    PhotoDomain(
                        selectedTask.value?.ticket_number ?: "0",
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
                            handleError(it.resultStatus, it.message)
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
                selectedTask.value?.ticket_number ?: ""
            ).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus, it.message)
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
                        updateTicketNumber(selectedTask.value?.ticket_number ?: "")
                        updateTicketId(selectedTask.value?.ticket_id.toString())
                    }

                    else -> {}
                }
            }
        }
    }

    fun updateTicketNumber(ticketNum: String) {
        _ticketNumber.update { ticketNum }
        getSharedPref().put(TicketNumber, ticketNumber.value)
    }

    fun updateTicketId(ticketIdentifier: String) {
        _ticketId.update { ticketIdentifier }
        getSharedPref().put(TicketId, ticketId.value)
    }

    private fun saveAndDeletePhotoByComponentKey() {
        viewModelScope.launch {
            selectedTask.value?.ticket_number?.let {
                deleteByComponentKeyUseCase(it).collect {
                    when (it.status) {
                        AsyncStatus.ERROR -> {
                            handleError(it.resultStatus, it.message)
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
                    selectedTask.value?.ticket_number.toString(), isEdited
                )
            ).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus, it.message)
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

    fun logoutCallApi(onSuccess: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase(Unit).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                    }

                    AsyncStatus.SUCCESS -> {
                        onSuccess()
                    }

                    else -> {}
                }
            }
        }
    }

    fun updateTask() {
        viewModelScope.launch(Dispatchers.IO) {
            updateTaskUseCase(Unit).collect {
                when (it.status) {
                    AsyncStatus.LOADING -> {
                        updateTicketListState(TicketListStatus.Loading)
                    }

                    AsyncStatus.ERROR -> {
                        Napier.log(LogLevel.ASSERT, "OnTasksEmpty", message = "error")

                    }

                    AsyncStatus.SUCCESS -> {
                        // Use paginated loading instead of loading all tasks
                        loadTasksPaginated(page = 0, isLoadMore = false)
                        updateTicketListState(TicketListStatus.Filled)
                        Napier.log(LogLevel.ASSERT, "OnTasksEmpty", message = "Success")

                    }

                    AsyncStatus.EMPTY -> {
                        updateTicketListState(TicketListStatus.Empty)
                        Napier.log(LogLevel.ASSERT, "OnTasksEmpty", message = "TaskIsEmpty")
                    }

                    else -> {}
                }
            }
        }
    }


//    private suspend fun updateSteps() {
//        updateStepsUseCase(Unit).collect {
//            when (it.status) {
//                AsyncStatus.ERROR -> {
//                    Napier.log(
//                        LogLevel.ASSERT, "updateSteps", message = "ERROR: " + it.message
//                    )
//                }
//
//                AsyncStatus.LOADING -> {
//                    Napier.log(LogLevel.ASSERT, "updateSteps", message = "LOADING: ")
//                }
//
//                AsyncStatus.SUCCESS -> {
//                    getTasks()
//
//                    Napier.log(
//                        LogLevel.ASSERT, "" + "", message = "SUCCESS: " + it.data
//                    )
//                }
//
//                else -> {}
//            }
//        }
//    }

    fun openInMapHandler() {
        viewModelScope.launch {
            getInitialFormByTask(
                selectedTask.value?.ticket_number ?: ""
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