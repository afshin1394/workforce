package com.irancell.nwg.wfm.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import com.irancell.nwg.wfm.presentation.model.Task
import com.irancell.nwg.wfm.presentation.components.CustomSearchBar
import com.irancell.nwg.wfm.presentation.components.FilterRow
import presentation.components.ticketCard
import presentation.screens.main.events.MainEvent
import presentation.theme.backgroundBackground3
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import com.irancell.nwg.wfm.presentation.theme.spacing2X

@Composable
fun TicketListScreen(
    tasks: ArrayList<Task>,
    searchText: String = "",
    onEvent: (mainEvents: MainEvent,selectedTask : Task?) -> Unit = { _: MainEvent, _: Task? -> }
) {



//    val context = LocalContext.current
    var searchTextState by remember {
        mutableStateOf(searchText)
    }
    var selectState by remember {
        mutableStateOf("")
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
            updatedText = {
                searchTextState = it
            }, onFilterClick = {
                onEvent(MainEvent.ActionFilter,null)
            })
        FilterRow(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing2X),
            itemIdSelected = 6
        ) {

            selectState = if (it.id.toString()=="6"){
               ""
            }else{
                it.id.toString()
            }

        }
        LazyColumn(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(color = backgroundBackground3)
                .padding(horizontal = spacing2X)
        ) {
           val filteredList =  tasks.filter {


                        it.title.lowercase().contains(searchTextState.lowercase()) ||
                        it.faultLevel.lowercase().contains(searchTextState.lowercase()) ||
                        it.step.lowercase().contains(searchTextState.lowercase()) ||
                        it.address.lowercase().contains(searchTextState.lowercase()) ||
                        it.type.lowercase().contains(searchTextState.lowercase()
                        )
            }.filter {  it.idState.toString().lowercase().contains(selectState.toString().lowercase()) }


            itemsIndexed(items = filteredList) { index: Int, item: Task ->
                ticketCard(modifier = Modifier.wrapContentHeight(),
                    task = item,
                    onActionClick = {
                        onEvent(MainEvent.AcceptTicket,item)
                    },
                    onMoreOptionsClick = {
                        onEvent(MainEvent.MoreOptions,item)
                    }

                )
            }
        }

    }
}