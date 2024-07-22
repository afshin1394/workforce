package presentation.screens.ticket_process.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import domain.models.PhotoDomain
import domain.models.initialForm.ComponentDomain
import domain.models.initialForm.ValueDomain
import domain.usecase.usecase.initialForm.GetInitialFormByTask
import domain.usecase.usecase.photo.GetPhotoByComponentKeyUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import presentation.screens.main.events.TicketInfoEvent
import utils.AsyncStatus
import utils.BaseViewModel
import utils.FormViewerTypes
import utils.LogicCalculation

import utils.ViewStates

class TicketInfoVM(
    private val getInitialFormByTask: GetInitialFormByTask,
    private val getPhotoByComponentKeyUseCase: GetPhotoByComponentKeyUseCase,
) : BaseViewModel() {


    private var componentsList = ArrayList<ComponentDomain>()
    var tempComponentList = mutableStateListOf<ComponentDomain>()


    var events = mutableStateOf<TicketInfoEvent>(TicketInfoEvent.Default)

    private val _ticketNumber = MutableStateFlow("0")
    val ticketNumber = _ticketNumber.asStateFlow()




    fun updateTicketNumber(ticketNumber: String) {
        _ticketNumber.update { ticketNumber }
    }

    private val _positionSelected = MutableStateFlow(0)
    val positionSelected = _positionSelected.asStateFlow()

    val logicCalculation: LogicCalculation = LogicCalculation(tempComponentList)


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

                    AsyncStatus.SUCCESS -> {
                        val initialFormDomain = it.data

                        if (initialFormDomain!!.structure.components!=null){
                            componentsList.clear()
                            componentsList.addAll(initialFormDomain.structure.components!!)
                            tempComponentList.clear()
                            tempComponentList.addAll(componentsList)
                            getPhotoByComponentKey()
//                        checkLogicsForAll(tempComponentList)
                            arrayListOf<ComponentDomain>().apply {
                                this.addAll(tempComponentList)
                                tempComponentList.clear()
                                tempComponentList.addAll(this)
                            }


                        }
                        updateState(ViewStates.Success())


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


    fun updateTempComponentList(newList: List<ComponentDomain>) {
        tempComponentList.clear()
        tempComponentList.addAll(newList)

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
    var photoDomainList = mutableStateListOf<PhotoDomain>()


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
                                        ticket_number = ticketNumber.value ,
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

    fun updatePhotoDomain(id: String, imgUri: String) {

        photoDomain.value = PhotoDomain(
            ticket_number = ticketNumber.value, id, (photoDomainList.size + 1L), imgUri, "", "0"
        )
        photoDomainList.add(photoDomain.value)

    }


    fun findPhotoIndexByIdAndPosition(id: String, position: Int): Int? {

        val filteredList = photoDomainList.filter { it.component_key == id }


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
            events.value = TicketInfoEvent.Default
        }
    }


    fun updateImageUriForEditPhoto(editUri: String, po: Int, id: String) {


        val itemIndex = findPhotoIndexByIdAndPosition(id, po)
        itemIndex?.let { index ->


            photoDomainList.getOrNull(index)?.let {
                photoDomainList[index] = it.copy(edited_uri = editUri)

            }
            events.value = TicketInfoEvent.PhotoPreview


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
                            newValue.find { it.label == "${component.type}:${component.id}" }?.value
                                ?: ""
                        updatePhotoDomain(component.id!!, imgUri)
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

}






