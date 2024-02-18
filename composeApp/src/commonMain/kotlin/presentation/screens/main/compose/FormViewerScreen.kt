@file:OptIn(ExperimentalMaterialApi::class)

package presentation.screens.main.compose


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue


import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults

import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.components.MenuItemsTopBar
import com.irancell.nwg.wfm.presentation.theme.radius
import com.mohamedrejeb.calf.ui.datepicker.AdaptiveDatePicker
import com.mohamedrejeb.calf.ui.datepicker.rememberAdaptiveDatePickerState
import dev.icerock.moko.resources.compose.painterResource
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.DatePickerFormat.format
import irancell.nwg.wfm.MR
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import presentation.screens.main.components.formViewer.DropDownMultiChoice
import presentation.screens.main.components.formViewer.DropDownSingleChoice
import presentation.screens.main.components.formViewer.ModalDatePicker

import presentation.theme.h4
import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceBrandDefault
import presentation.theme.textSecondary

import utils.getLocalDateTimeFromLong


@OptIn(ExperimentalMaterialApi::class)
class FormViewerScreen(private val title: String) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()

        val sheetState = rememberBottomSheetScaffoldState(
            bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
        )
        val navigator = LocalNavigator.currentOrThrow


        var bottomSheetIsOpen by rememberSaveable { mutableStateOf(false) }


        val scope = rememberCoroutineScope()
        val datePickerState = rememberAdaptiveDatePickerState()
        var data by remember { mutableStateOf("") }


        LaunchedEffect(datePickerState.selectedDateMillis) {
            datePickerState.selectedDateMillis?.let {
                val currentDate =
                    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                data = getLocalDateTimeFromLong(it).format("yyyy-mm-dd")

            }

        }


        val items = listOf("Item 1", "Item 2", "Item 3", "Item 4")
        var selectedItem by remember { mutableStateOf("") }
        datePickerState.selectedDateMillis



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
                   // verticalArrangement = Arrangement.Center,
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


                    ModalDatePicker("selected date", "Date-time-picker", onDateSelected = {

                    })


                }

            }

        )

    }
}



