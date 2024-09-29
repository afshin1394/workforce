package presentation.screens.ticket_process.viewModel


import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import com.irancell.nwg.wfm.presentation.model.View
import data.network.response.task.Component
import data.network.response.task.Value
import dev.icerock.moko.resources.desc.StringDesc
import domain.models.PhotoDomain
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ValueDomain
import domain.usecase.ResultStatus
import domain.usecase.usecase.photo.DeleteByComponentKeyUseCase
import domain.usecase.usecase.steps.UpdateStepFormUseCase
import domain.usecase.usecase.steps.StepDetail
import domain.usecase.usecase.steps.StoreStepFormUseCase
import domain.usecase.usecase.photo.GetPhotoByComponentKeyUseCase
import domain.usecase.usecase.photo.InsertPhotoUseCase
import domain.usecase.usecase.steps.SendStepsOfTicketToServerUseCase
import domain.usecase.usecase.steps.UpdateStepsUseCase
import domain.usecase.usecase.ticket.UpdateTaskUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import presentation.model.ExtractLogicsModel
import presentation.screens.main.events.TicketProcessEvent
import presentation.screens.ticket_process.events.StepEvent
import utils.AsyncStatus
import utils.BaseViewModel
import utils.FormViewerTypes
import utils.LogicCalculation
import utils.PROCEED
import utils.ServiceState
import utils.ViewStates
import utils.updateValueDomain

class TicketProcessVM(
    private val updateStepFormUseCase: UpdateStepFormUseCase,
    private val getPhotoByComponentKeyUseCase: GetPhotoByComponentKeyUseCase,
    private val storeStepFormUseCase: StoreStepFormUseCase,
    private val deleteByComponentKeyUseCase: DeleteByComponentKeyUseCase,
    private val insertPhotoUseCase: InsertPhotoUseCase,
    private val sendStepsOfTicketToServerUseCase: SendStepsOfTicketToServerUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val updateStepUseCase : UpdateStepsUseCase
) : BaseViewModel() {
    private val _scrollingPosition = MutableStateFlow(Pair(-1,-1))
    val scrollingPosition = _scrollingPosition.asStateFlow()

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

    var tempComponent = mutableStateOf<ComponentDomain?>(null)

    var events = mutableStateOf<TicketProcessEvent>(TicketProcessEvent.Default)

    private val _positionSelected = MutableStateFlow(0)
    val positionSelected = _positionSelected.asStateFlow()

    private val _ticketId = MutableStateFlow("0")
    val ticketId = _ticketId.asStateFlow()

    private val _ticketNumber = MutableStateFlow("0")
    val ticketNumber = _ticketNumber.asStateFlow()

    private val _ticketFlowCompleted = MutableStateFlow(false)
    var ticketFlowCompleted = _ticketFlowCompleted.asStateFlow()

    fun updateTicketFlowState(completed : Boolean){
        _ticketFlowCompleted.update { completed }
    }

    private val _savedIndex = MutableStateFlow(0)
    var savedIndex =  _savedIndex.asStateFlow()

    private val _savedParentIndex = MutableStateFlow(0)
    var savedParentIndex =  _savedParentIndex.asStateFlow()

    private val _updateTasksComplete = MutableStateFlow(false)
    var updateTasksComplete = _updateTasksComplete.asStateFlow()


    val logicCalculation: LogicCalculation = LogicCalculation(viewModelScope,tempComponentList)
    var extractLogicsModel = mutableListOf<ExtractLogicsModel>()

     fun getMokStepsForm(proceed: String) {
        events.value = TicketProcessEvent.InProgress
        viewModelScope.launch(Dispatchers.Main) {
        updateStepFormUseCase(
            Tuple6(
                _ticketNumber.value,
                proceed,
                tempComponentList.toList(),
                photoDomainList.toList(),
                _currentLevel.value,
                _ticketId.value
            )
        ).collect {
            when (it.status) {
                AsyncStatus.ERROR -> {
                    handleError(it.resultStatus)
                    events.value = TicketProcessEvent.Default

                }

                AsyncStatus.LOADING -> {
                    updateState(ViewStates.Loading)
                }

                AsyncStatus.EMPTY->{


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
                        events.value = TicketProcessEvent.Default

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


    private fun storeLastStep() {
        viewModelScope.launch(Dispatchers.Main) {
            storeStepFormUseCase(
                Tuple5(
                    _ticketNumber.value,
                    tempComponentList.toList(),
                    photoDomainList.toList(),
                    _currentLevel.value,
                    _ticketId.value
                )
            ).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)
                    }

                    AsyncStatus.LOADING -> {
                        updateState(ViewStates.Loading)
                    }
                    AsyncStatus.EMPTY->{
                    }

                    AsyncStatus.SUCCESS -> {
                        sendToServer()
                    }
                }
            }
        }
    }

    private fun sendToServer() {
        viewModelScope.launch {
            sendStepsOfTicketToServerUseCase(_ticketNumber.value).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)
                    }

                    AsyncStatus.LOADING -> {
                        updateState(ViewStates.Loading)
                    }

                    AsyncStatus.EMPTY -> {
                    }

                    AsyncStatus.SUCCESS -> {

                        _ticketFlowCompleted.update { true }
                        events.value=TicketProcessEvent.TicketFlowCompleted
                        updateState(ViewStates.Success())

                    }
                }
            }
        }
    }

    private fun storeStepForm() {
        events.value = TicketProcessEvent.InProgress

        viewModelScope.launch {
            storeStepFormUseCase(
                Tuple5(
                    _ticketNumber.value,
                    tempComponentList.toList(),
                    photoDomainList.toList(),
                    _currentLevel.value,
                    _ticketId.value
                )
            ).collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)
                        events.value = TicketProcessEvent.Default

                    }

                    AsyncStatus.LOADING -> {
                        updateState(ViewStates.Loading)
                    }

                    AsyncStatus.EMPTY -> {
                    }

                    AsyncStatus.SUCCESS -> {
                        events.value = TicketProcessEvent.Default

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


    private suspend fun checkLogicsForAll(components: List<ComponentDomain>) {

        // Create a copy of the components list to iterate over
        val componentsCopy = components.toMutableList()

        for (cmp in componentsCopy) {
            extractLogicsModel.addAll( logicCalculation.extractLogics(componentsCopy, cmp))
            cmp.components?.let { cmps ->
                if (cmps.isNotEmpty()) {
                    checkLogicsForAll(cmps)

                }
            }
        }


    }


    fun handleLogics(onResult:(MutableList<ExtractLogicsModel>) -> Unit) {


        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { checkLogicsForAll(tempComponentList) }

                withContext(Dispatchers.Main) {
                    arrayListOf<ComponentDomain>().apply {
                        this.addAll(tempComponentList)
                        tempComponentList.clear()
                        tempComponentList.addAll(this)
                    }
                    onResult(extractLogicsModel)
                }


            } catch (_: Exception) {

                async { checkLogicsForAll(tempComponentList) }.await()
                withContext(Dispatchers.Main) {
                    arrayListOf<ComponentDomain>().apply {
                        this.addAll(tempComponentList)
                        tempComponentList.clear()
                        tempComponentList.addAll(this)
                    }
                    onResult(extractLogicsModel)

                }

            }

        }
    }

  suspend  fun addOrRemoveComponentDomainRepeatableToList(
        compD:ComponentDomain,
        indexChild: Int,
        scrollCallBack:(position:Int)->Unit
    ) {
      withContext(Dispatchers.IO) {

          val createdIndex = tempComponentList.findComponentsWithKey(compD).size
          Napier.log(LogLevel.ASSERT, "createdIndex", message = createdIndex.toString())
          val tempComponent = arrayListOf<ComponentDomain>()
          if (compD.removable == true) {

              tempComponent.addAll(tempComponentList.apply {
                  add(indexChild + createdIndex, compD)
              })
              tempComponentList.clear()
              tempComponentList.addAll(tempComponent)
              scrollCallBack(createdIndex)

          } else {
              tempComponent.addAll(tempComponentList.apply {
                  removeAt(indexChild)
              })
              tempComponentList.clear()
              tempComponentList.addAll(tempComponent)
              scrollCallBack(indexChild)


          }
      }
  }

    private fun List<ComponentDomain>.findComponentsWithKey(componentDomain: ComponentDomain): List<ComponentDomain> {
        return this.flatMap { component ->
            listOf(component).plus(component.components?.findComponentsWithKey(componentDomain) ?: emptyList())
        }.filter { it.key == componentDomain.key }
    }


    /////////////////////////////photo//////////////////////////////////////////////////////////


    private val photoDomain = MutableStateFlow<PhotoDomain>(
        PhotoDomain(
            ticketNumber.value ?: "0","0",
            "0",
            0,
            "",
            "",
            "0"
        )
    )


    fun findPhotosByComponentId(id: String?,key: String?): MutableList<PhotoDomain> {
        val list: MutableList<PhotoDomain> = arrayListOf()
        photoDomainList.forEach {
            if (it.component_key == key && it.componentId == id) {
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
                        // updateState(ViewStates.Loading)

                    }

                    AsyncStatus.EMPTY -> {
                    }

                    AsyncStatus.SUCCESS -> {
                        photoDomainList.clear()
                        updateState(ViewStates.Success())


                        it.data?.let { it1 ->
                            for (i in it1.indices) {
                                photoDomain.value =
                                    PhotoDomain(
                                        ticket_number = ticketNumber.value,
                                        it1[i].componentId,
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

   private fun updatePhotoDomain(id: String,key: String, imgUri: String) {
        Napier.log(LogLevel.ASSERT,"updatePhotoDomain", message = key)
        photoDomain.value = PhotoDomain(
            ticket_number = ticketNumber.value, id,key, (photoDomainList.size + 1L), imgUri, "", "0"
        )
        photoDomainList.add(photoDomain.value)

    }


    fun findPhotoIndexByIdAndPosition(key: String,id : String, position: Int): Int? {

        val filteredList = photoDomainList.filter { it.component_key == key && it.componentId == id }

        if (filteredList.isNotEmpty()) {
            if (position in filteredList.indices) {
                val itemIndex = photoDomainList.indexOf(filteredList[position])
                return if (itemIndex != -1) itemIndex else null
            }
        }
        return null
    }

    fun updateImageUriForDeletePhoto(po: Int, key: String, id: String) {

        // Since every time the Image component captures a photo, a new item is added to the photo table
        // instead of storing the photos as a list in the value field, I have to use photoDomainList.size
        // to manage the photos. Also, the validation for the photo is only "required" (i.e., it checks if a photo
        // is present, but no other validations like size or format are applied).
        val itemIndex = findPhotoIndexByIdAndPosition(key, id, po)
        itemIndex?.let {
            photoDomainList.removeAt(it)
            events.value = TicketProcessEvent.Default
        }

        // Filtering the photo list based on component key and ID
        val filteredList = photoDomainList.filter { it.component_key == key && it.componentId == id }
        val currentComponent = tempComponent.value

        // Updating the list of component values based on whether there are photos or not
        val updatedValues = currentComponent?.values?.mapIndexed { index, valueDomain ->
            if (filteredList.isEmpty()) {
                valueDomain.copy(value = "")
            } else {
                valueDomain
            }
        }

        tempComponent.value = currentComponent?.copy(values = updatedValues)

        // Finding the index of the component in the list and replacing it with the updated component
        val componentIndex = tempComponentList.indexOfFirst { it.id == currentComponent?.id }
        if (componentIndex != -1) {
            tempComponentList[componentIndex] = tempComponent.value!!
        }
    }


    fun updateImageUriForEditPhoto(editUri: String, po: Int, key: String,id : String) {


        val itemIndex = findPhotoIndexByIdAndPosition(key,id,po)
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
    fun updateUriPhotoComponent(component : ComponentDomain,newValue : List<ValueDomain>){
        val imgUri =
            newValue.find { it.label == "${component.type}:${component.id}" }?.value?:""

        component.key?.let {
            component.id?.let {
                updatePhotoDomain(component.id, component.key, imgUri)
            }
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

                        AsyncStatus.EMPTY -> {
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
                    photoDomainList[i].componentId,
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

                    AsyncStatus.EMPTY -> {

                    }

                    AsyncStatus.SUCCESS -> {
                        updateState(ViewStates.Success())


                    }

                }
            }


        }

    }

    private fun List<ComponentDomain>.findComponentByKey(key: String): ComponentDomain? {
        this.forEach { component ->
            if (component.key == key) {
                return component
            }

            // Recursively search in the children
            val found = component.components?.findComponentByKey(key)
            if (found != null) {
                return found
            }
        }
        return null
    }

    private fun List<ComponentDomain>.findComponentById(id: String?): ComponentDomain? {
        for (component in this) {
            if (component.id == id) {
                return component
            }
            component.components?.findComponentById(id)?.let { return it }
        }
        return null
    }

    fun updateTasks() {
       viewModelScope.launch{
           updateTask()
       }
    }

    private suspend fun updateTask() {
        updateTaskUseCase(Unit)
            .collect {
                when (it.status) {
                    AsyncStatus.ERROR -> {
                        handleError(it.resultStatus)
                        println("TaskCallApi${"ERROR"}")
                    }

                    AsyncStatus.LOADING -> {
                        updateState(ViewStates.Loading)
                    }

                    AsyncStatus.EMPTY -> {

                    }

                    AsyncStatus.SUCCESS -> {
                        updateSteps()
                        println("TaskCallApi${"SUCCESS"}")
                    }
                }
            }
    }

    private suspend fun updateSteps() {
        updateStepUseCase(Unit).collect {
            when (it.status) {
                AsyncStatus.ERROR -> {

                    handleError(it.resultStatus)
                    Napier.log(
                        LogLevel.ASSERT,
                        "updateSteps",
                        message = "ERROR: " + it.message
                    )

                }

                AsyncStatus.EMPTY -> {

                }

                AsyncStatus.LOADING -> {
                    Napier.log(LogLevel.ASSERT, "updateSteps", message = "LOADING: ")

                }

                AsyncStatus.SUCCESS -> {

                    _updateTasksComplete.update { true }
                    updateState(ViewStates.Success())

                    Napier.log(
                        LogLevel.ASSERT,
                        "" +
                                "",
                        message = "SUCCESS: " + it.data
                    )


                }


            }
        }


    }
   suspend fun showFirstError(errors: Map<String, List<StringDesc>>) {
       errors.keys.toList()[0].let {
          val pair = findComponentById(tempComponentList,it)
           pair?.let {
               withContext(Dispatchers.Main) {
                   _scrollingPosition.update { pair }
                   delay(1000)
                   _scrollingPosition.update { Pair(-1,-1) }
               }
           }
       }
    }

    fun findComponentById(components: List<ComponentDomain>, targetId: String): Pair<Int, Int>? {
        components.forEachIndexed { parentIndex, parentComponent ->
            // Check if the parent component itself matches the target ID
            if (parentComponent.id == targetId) {
                return parentIndex to -1  // -1 signifies no child match, found at parent
            }

            // Search in the children of the parent component recursively
            parentComponent.components?.forEachIndexed { childIndex, childComponent ->
                if (childComponent.id == targetId) {
                    return parentIndex to childIndex
                }

                // Recursive search in the children's children

                val childResult = childComponent.components?.let{ findComponentById(it, targetId)}
                if (childResult != null) {
                    return parentIndex to childIndex
                }
            }
        }
        return null // Not found
    }

    fun updateScrollingState(pair: Pair<Int,Int>) {
      _scrollingPosition.update { pair }
    }

    fun updateTicketId(ticketId: String) {
        _ticketId.update { ticketId }
        logicCalculation.ticketId(ticketId)

    }
//
//    fun updateReloadState(reload: Boolean) {
//     _reloadState.update { reload }
//    }
}






