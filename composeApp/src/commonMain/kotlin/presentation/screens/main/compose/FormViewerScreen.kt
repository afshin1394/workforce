@file:OptIn(ExperimentalMaterialApi::class)

package presentation.screens.main.compose


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.components.MenuItemsTopBar

import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.MR
import presentation.screens.main.components.formViewer.CheckList
import presentation.screens.main.components.formViewer.DropDownMultiChoice
import presentation.screens.main.components.formViewer.DropDownSingleChoice
import presentation.screens.main.components.formViewer.ModalDatePicker
import presentation.screens.main.components.formViewer.ModalDateTimePicker
import presentation.screens.main.components.formViewer.ModalTimePicker
import presentation.screens.main.components.formViewer.Radio



@OptIn(ExperimentalMaterialApi::class)
class FormViewerScreen(private val title: String) : Screen {

    @Composable
    override fun Content() {
        val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()

        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        val items = listOf(
            "Item 1",
            "Item 2",
            "Item 3",
            "Item 4",

        )
        var selectedItem by remember { mutableStateOf("") }







        BaseScreen(
            title = title,
            scaffoldState = scaffoldState,
            topBar = {
                MenuItemsTopBar(title) {
                    navigator.pop()
                }
            },

            bottomSheetTitle = "",

            bottomSheetContent = {


            }, onCloseBottomSheet = {

            },


            content = {


                Column(
                    modifier = Modifier.fillMaxHeight().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    DropDownSingleChoice(
                        titleDropDown = "select an item",
                        itemList = items,
                        selectItem = selectedItem,
                        searchText = "",
                        onItemSelected = {
                            selectedItem = it
                            println("selectSingleChoice>>>>${it}")
                        },
                        onSearchButtonClicked = {
                            println("searchSingleChoice>>>>${it}")
                        })

                    Spacer(modifier = Modifier.padding(vertical = 4.dp))

                    DropDownMultiChoice(
                        titleDropDown = "select an item",
                        searchText = "",
                        itemList = items,
                        onItemSelected = {

                        },
                        onSearchButtonClicked = {

                        })


                    ModalDatePicker(
                        stringResource(MR.strings.selected_date),
                        stringResource(MR.strings.date_picker),
                        onDateSelected = {

                        })


                    ModalTimePicker(
                        stringResource(MR.strings.selected_time),
                        stringResource(MR.strings.time_picker),
                        onTimeSelected = {

                        })

                    ModalDateTimePicker(
                        stringResource(MR.strings.selected_date_time),
                        stringResource(MR.strings.date_time_picker),
                        onDateSelected = {

                        },
                        onTimeSelected = {})


                    Radio(itemList = items, selectItem = "", onItemSelected = {

                        println("multiChoooice>>>>${it}")

                    })

                    CheckList(  itemList = items,
                        onItemSelected = {

                            println("multiChoooice>>>>${it.size}")

                        })


                }

            }

        )


    }

}



