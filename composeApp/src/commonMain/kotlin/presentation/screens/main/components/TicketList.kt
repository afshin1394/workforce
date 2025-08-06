package presentation.screens.main.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import presentation.components.CustomSearchBar
import com.irancell.nwg.wfm.presentation.components.FilterRow
import presentation.components.ticketCard
import presentation.screens.main.events.MainEvent
import presentation.theme.backgroundBackground3
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import domain.mappers.toStateFilterList
import domain.models.task.ActivityListDomain
import domain.models.task.TaskDomain
import presentation.model.StateFilter
import presentation.screens.main.viewmodel.MainScreenVM
import utils.TaskState
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun TicketListScreen(
    tasks: List<TaskDomain>,
    tasksActivityListFilter: List<ActivityListDomain>,
    searchText: String = "",
    onEvent: (mainEvents: MainEvent, selectedTask: TaskDomain?) -> Unit = { _: MainEvent, _: TaskDomain? -> },
    onAccept: (item: TaskDomain) -> Unit,
    viewModel: MainScreenVM
) {
    // Memoize search and filter states to prevent unnecessary recompositions
    var searchTextState by remember { mutableStateOf(searchText) }
    var selectState by remember { mutableStateOf("") }

    // Memoize filter list creation to prevent recreation on every recomposition
    val finalFilterList = remember(tasksActivityListFilter) {
        val dynamicFilterList = tasksActivityListFilter.toStateFilterList()
        buildList {
            add(
                StateFilter(
                    id = TaskState.All.id,
                    title = "All", // Use hardcoded string to avoid resource lookup on every recomposition
                    isActive = true
                )
            )
            addAll(dynamicFilterList)
        }
    }

    var selectedFilterTitle by remember(finalFilterList) {
        mutableStateOf(finalFilterList.firstOrNull()?.title ?: "")
    }

    // CRITICAL FIX: Memoize filtered tasks to prevent recalculation on every recomposition
    val filteredTasks = remember(tasks, searchTextState, selectState) {
        val searchFiltered = if (searchTextState.isBlank()) {
            tasks
        } else {
            val searchLower = searchTextState.lowercase()
            tasks.filter { task ->
                task.properties.any { property ->
                    property.key.lowercase().contains(searchLower) ||
                    property.value.lowercase().contains(searchLower)
                } || task.ticket_number.contains(searchTextState)
            }
        }
        
        if (selectState.isBlank()) {
            searchFiltered
        } else {
            val selectLower = selectState.lowercase()
            searchFiltered.filter { task ->
                task.activity__title.lowercase().contains(selectLower)
            }
        }
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(color = backgroundBackground3)
    ) {
        Spacer(modifier = Modifier.padding(top = spacing15X, bottom = spacing15X))
        CustomSearchBar(
            modifier = Modifier
                .padding(horizontal = spacing2X)
                .fillMaxWidth(),
            textFieldState = searchTextState,
            updatedText = { newText ->
                searchTextState = newText
                // Remove ViewModel search calls since we're handling filtering locally
            }, onFilterClick = {
                onEvent(MainEvent.ActionFilter, null)
            })


        FilterRow(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing2X),
            itemTitleSelected = selectedFilterTitle
            ,
            items = finalFilterList
        ) {
            selectedFilterTitle = it.title
            selectState = if (it.id == TaskState.All.id) {
                ""
            } else {
                it.title
            }
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(spacing15X),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(color = backgroundBackground3)
                .padding(horizontal = spacing2X)
        ) {
            // Use the memoized filteredTasks instead of filtering in composition
            itemsIndexed(
                items = filteredTasks,
                key = { _, item -> item.ticket_id } // Add stable key for better performance
            ) { index: Int, item: TaskDomain ->
                ticketCard(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    task = item,
                    onActionClick = {
                        Napier.log(LogLevel.ASSERT, "TicketList", 
                            message = "Accept button clicked for ticket - Number: ${item.ticket_number}, ID: ${item.ticket_id}, State: ${item.ticket_state}")
                        // Only call onAccept - it handles checking if edited and showing dialog
                        onAccept(item)
                        Napier.log(LogLevel.ASSERT, "TicketList", 
                            message = "onAccept callback called for ticket ${item.ticket_number}")
                    },
                    onMoreOptionsClick = {
                        Napier.log(LogLevel.ASSERT, "TicketList", 
                            message = "More options clicked for ticket ${item.ticket_number}")
                        onEvent(MainEvent.MoreOptions, item)
                    }
                )
            }
        }
    }
}