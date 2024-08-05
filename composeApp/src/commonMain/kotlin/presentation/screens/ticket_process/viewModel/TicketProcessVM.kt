package presentation.screens.ticket_process.viewModel


import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import arrow.core.Tuple4
import arrow.core.Tuple5
import domain.models.PhotoDomain
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ValueDomain
import domain.usecase.usecase.photo.DeleteByComponentKeyUseCase
import domain.usecase.usecase.steps.UpdateStepFormUseCase
import domain.usecase.usecase.steps.StepDetail
import domain.usecase.usecase.steps.StoreStepFormUseCase
import domain.usecase.usecase.photo.GetPhotoByComponentKeyUseCase
import domain.usecase.usecase.photo.InsertPhotoUseCase
import domain.usecase.usecase.steps.SendStepOfTicketToServer
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import presentation.screens.main.events.TicketProcessEvent
import presentation.screens.ticket_process.events.StepEvent
import utils.AsyncStatus
import utils.BaseViewModel
import utils.FormViewerTypes
import utils.LogicCalculation
import utils.PROCEED
import utils.ViewStates

class TicketProcessVM(
    private val updateStepFormUseCase: UpdateStepFormUseCase,
    private val getPhotoByComponentKeyUseCase: GetPhotoByComponentKeyUseCase,
    private val storeStepFormUseCase: StoreStepFormUseCase,
    private val deleteByComponentKeyUseCase: DeleteByComponentKeyUseCase,
    private val insertPhotoUseCase: InsertPhotoUseCase,
    private val sendStepOfTicketToServer: SendStepOfTicketToServer
) : BaseViewModel() {
    private val _currentLevel = MutableStateFlow(0)
    val currentLevel = _currentLevel.asStateFlow()

    private val _completed = MutableStateFlow(false)
    val completed = _completed.asStateFlow()

    private val _reloadState = MutableStateFlow(false)
    var reloadState = _reloadState.asStateFlow()

    private val _currentLevelName = MutableStateFlow("")
    val currentLevelName = _currentLevelName.asStateFlow()

    private val _stepDetails = MutableStateFlow(emptyList<StepDetail>())
    val stepDetails = _stepDetails.asStateFlow()

    private val _stepEvent = MutableStateFlow<StepEvent>(StepEvent.INITIAL)
    var stepEvent = _stepEvent.asStateFlow()


    var tempComponentList = mutableStateListOf<ComponentDomain>()
    var photoDomainList = mutableStateListOf<PhotoDomain>()

    var events = mutableStateOf<TicketProcessEvent>(TicketProcessEvent.Default)

    private val _positionSelected = MutableStateFlow(0)
    val positionSelected = _positionSelected.asStateFlow()

    private val _ticketNumber = MutableStateFlow("0")
    val ticketNumber = _ticketNumber.asStateFlow()


    val logicCalculation: LogicCalculation = LogicCalculation(tempComponentList)

    fun getMokStepsForm(proceed: String) {
        viewModelScope.launch(Dispatchers.Main) {
            updateStepFormUseCase(
                Tuple5(
                    _ticketNumber.value,
                    proceed,
                    tempComponentList.toList(),
                    photoDomainList.toList(),
                    _currentLevel.value
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

                        it.data?.let { data ->

                            data.activityDomain.form.form_structure.components?.let {
                                Napier.log(
                                    LogLevel.ASSERT,
                                    "form_structure.components",
                                    message = it.toString()
                                )
                                tempComponentList.clear()
                                tempComponentList.addAll(it.toList())

                            }
                            Napier.log(
                                LogLevel.ASSERT,
                                "data.stepCounter",
                                message = it.data.activityDomain.photoDomainList.toString()
                            )

                            photoDomainList.clear()
                            photoDomainList.addAll(data.activityDomain.photoDomainList)

                            Napier.log(
                                LogLevel.ASSERT,
                                "data.stepCounter",
                                message = data.stepCounter.toString()
                            )
                            Napier.log(
                                LogLevel.ASSERT,
                                "data.stepCounter",
                                message = data.stepTitle
                            )

                            _currentLevel.update { data.stepCounter }
                            _currentLevelName.update { data.stepTitle }
                            _stepDetails.update { data.stepDetails }
                            _reloadState.update { true }
                            _stepEvent.update { StepEvent.IN_PROCESS }

                            updateState(ViewStates.Success())
                            getPhotoByComponentKey()
                        }
                    }
                }
            }

        }
    }


    fun updateLevel(proceed: String) {
        Napier.log(
            LogLevel.ASSERT,
            tag = "updateLevelupdateLevel",
            message = _stepEvent.value.toString()
        )
        if (_currentLevel.value == _stepDetails.value.size - 1 && proceed != PROCEED.PREVIOUS) {
            storeLastStep()
        } else {
            if (!(proceed == PROCEED.PREVIOUS && _currentLevel.value == 0)) {
                _reloadState.update { false }
                getMokStepsForm(proceed)

            } else {
                storeStepForm()
            }
        }


    }

    private fun sendToServer() {
        viewModelScope.launch {
            sendStepOfTicketToServer(_ticketNumber.value).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)
                    }

                    AsyncStatus.LOADING -> {
                        updateState(ViewStates.Loading)
                    }

                    AsyncStatus.SUCCESS -> {
                        _completed.update { true }
                        updateState(ViewStates.Success())
                    }
                }
            }
        }
    }

    private fun storeLastStep() {
        viewModelScope.launch(Dispatchers.Main) {
            storeStepFormUseCase(
                Triple(
                    _ticketNumber.value,
                    tempComponentList.toList(),
                    photoDomainList.toList()
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
                        sendToServer()

                    }
                }
            }
        }
    }

    private fun storeStepForm() {
        viewModelScope.launch(Dispatchers.Main) {
            storeStepFormUseCase(
                Triple(
                    _ticketNumber.value,
                    tempComponentList.toList(),
                    photoDomainList.toList()
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
                        _stepEvent.update { StepEvent.START }

                    }
                }
            }
        }
    }


    fun updateTicketNumber(ticketNumber: String) {
        _ticketNumber.update { ticketNumber }
    }


    fun updateTempComponentList(newList: List<ComponentDomain>) {
        tempComponentList.clear()
        tempComponentList.addAll(newList)

    }


    private fun checkLogicsForAll(components: List<ComponentDomain>) {

        // Create a copy of the components list to iterate over
        val componentsCopy = components.toMutableList()

        for (cmp in componentsCopy) {
            logicCalculation.extractLogics(componentsCopy, cmp)
            cmp.components?.let { cmps ->
                if (cmps.isNotEmpty()) {
                    checkLogicsForAll(cmps)

                }
            }
        }


    }


    fun handleLogics() {


        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { checkLogicsForAll(tempComponentList) }

                withContext(Dispatchers.Main) {

                    arrayListOf<ComponentDomain>().apply {
                        this.addAll(tempComponentList)
                        tempComponentList.clear()
                        tempComponentList.addAll(this)
                    }
                }
            } catch (_: Exception) {

                async { checkLogicsForAll(tempComponentList) }.await()
                withContext(Dispatchers.Main) {
                    arrayListOf<ComponentDomain>().apply {
                        this.addAll(tempComponentList)
                        tempComponentList.clear()
                        tempComponentList.addAll(this)
                    }
                }

            }

        }
    }

    fun addOrRemoveComponentDomainRepeatableToList(
        listComponent: List<ComponentDomain>,
        indexChild: Int
    ) {
        if (listComponent[0].type == FormViewerTypes.Group) {
            if (listComponent[0].removable) {
                val newComponents = tempComponentList.apply {
                    add(indexChild + 1, listComponent[0])
                }
                tempComponentList = newComponents
            } else {
                val newComponents = tempComponentList.apply {
                    removeAt(indexChild)
                }
                tempComponentList = newComponents

            }
        }
    }

    /////////////////////////////photo//////////////////////////////////////////////////////////


    private val photoDomain = MutableStateFlow<PhotoDomain>(
        PhotoDomain(
            ticketNumber.value ?: "0",
            "0",
            0,
            "",
            "",
            "0"
        )
    )


    fun findPhotosByComponentId(id: String?): MutableList<PhotoDomain> {
        val list: MutableList<PhotoDomain> = arrayListOf()
        photoDomainList.forEach {
            if (it.component_key == id) {
                list.add(it)
            }
        }
        return list
    }


    private fun getPhotoByComponentKey() {

        viewModelScope.launch {

            getPhotoByComponentKeyUseCase(ticketNumber.value).collect {
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
                                        ticket_number = ticketNumber.value,
                                        it1[i].component_key,
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

    fun updatePhotoDomain(key: String, imgUri: String) {

        photoDomain.value = PhotoDomain(
            ticket_number = ticketNumber.value, key, (photoDomainList.size + 1L), imgUri, "", "0"
        )
        photoDomainList.add(photoDomain.value)

    }


    fun findPhotoIndexByIdAndPosition(key: String, position: Int): Int? {

        val filteredList = photoDomainList.filter { it.component_key == key }


        if (filteredList.isNotEmpty()) {
            if (position in filteredList.indices) {
                val itemIndex = photoDomainList.indexOf(filteredList[position])
                return if (itemIndex != -1) itemIndex else null
            }
        }
        return null
    }

    fun updateImageUriForDeletePhoto(po: Int, id: String) {
        val itemIndex = findPhotoIndexByIdAndPosition(id, po)
        itemIndex?.let {
            photoDomainList.removeAt(it)
            events.value = TicketProcessEvent.Default
        }
    }


    fun updateImageUriForEditPhoto(editUri: String, po: Int, key: String) {


        val itemIndex = findPhotoIndexByIdAndPosition(key, po)
        itemIndex?.let { index ->


            photoDomainList.getOrNull(index)?.let {
                photoDomainList[index] = it.copy(edited_uri = editUri)

            }
            events.value = TicketProcessEvent.PhotoPreview


        }
    }

    fun updatePositionSelected(position: Int) {
        _positionSelected.update { position }
    }


    fun updateUriPhotoComponent(
        components: List<ComponentDomain>,
        indexParent: List<Int>,
        indexChild: Int,
        newValue: List<ValueDomain>
    ): List<ComponentDomain> {
        if (indexParent.isEmpty()) {
            val updatedComponents = components.mapIndexed { idx, component ->
                if (idx == indexChild) {
                    if (component.type == FormViewerTypes.ImageView) {
                        val imgUri =
                            newValue.find { it.label == "${component.type}:${component.key}" }?.value
                                ?: ""
                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "componettttt",
                            message = component.toString()
                        )
                        updatePhotoDomain(component.key ?: "", imgUri)

                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "componettttt",
                            message = component.toString()
                        )
                    }
                    component.copy(values = newValue)

                } else {
                    component
                }

            }
            return updatedComponents
        } else {
            val parentIndex = indexParent[0]
            val remainingIndexes = indexParent.drop(1)
            val updatedComponents = components.mapIndexed { idx, component ->
                if (idx == parentIndex && component.components != null) {
                    component.copy(
                        components = updateUriPhotoComponent(
                            component.components!!, remainingIndexes, indexChild, newValue
                        )
                    )
                } else {
                    component
                }
            }
            return updatedComponents
        }
    }


    suspend fun saveAndDeletePhotoByComponentKey() {
        if (photoDomainList.isNotEmpty()) {
            _ticketNumber.value.let {
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
                            insertNewPhoto()
                        }

                    }
                }
            }
        } else {
            updateState(ViewStates.Success())
        }
    }


    private suspend fun insertNewPhoto() {


        for (i in photoDomainList.indices) {
            insertPhotoUseCase(
                PhotoDomain(
                    ticketNumber.value,
                    photoDomainList[i].component_key,
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






