@file:OptIn(ExperimentalMaterialApi::class)

package presentation.screens.main.compose


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.material3.SnackbarHostState


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
import androidx.compose.material3.TimePickerDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.theme.radius
import com.mohamedrejeb.calf.ui.datepicker.AdaptiveDatePicker
import com.mohamedrejeb.calf.ui.datepicker.rememberAdaptiveDatePickerState
import com.mohamedrejeb.calf.ui.sheet.AdaptiveBottomSheet
import com.mohamedrejeb.calf.ui.sheet.rememberAdaptiveSheetState
import com.mohamedrejeb.calf.ui.timepicker.AdaptiveTimePicker
import com.mohamedrejeb.calf.ui.timepicker.rememberAdaptiveTimePickerState
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.DatePickerFormat.format
import irancell.nwg.wfm.MR
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import presentation.components.MenuItemsTopBar
import presentation.screens.main.components.formViewer.CheckList
import presentation.screens.main.components.formViewer.DropDownMultiChoice
import presentation.screens.main.components.formViewer.DropDownSingleChoice
import presentation.screens.main.components.formViewer.Editable
import presentation.screens.main.components.formViewer.ModalDatePicker
import presentation.screens.main.components.formViewer.ModalDateTimePicker
import presentation.screens.main.components.formViewer.ModalTimePicker
import presentation.screens.main.components.formViewer.Radio
import presentation.screens.main.components.formViewer.TypeEditable
import presentation.screens.main.viewmodel.FormViewVM

import presentation.theme.h4
import presentation.theme.strokeDefaultLight
import presentation.theme.subtleDefault
import presentation.theme.surfaceBrandDefault
import presentation.theme.textInverse
import presentation.theme.textSecondary
import utils.FormViewerTypes.Radio

import utils.getLocalDateTimeFromLong


@OptIn(ExperimentalMaterialApi::class)
class FormViewerScreen() : Screen {

    @Composable
    override fun Content() {
        val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()

        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        val items = listOf(
            "Item1",
            "Item2",
            "Item3",
            "Item4",

            )
        var selectedItem by remember { mutableStateOf("") }
        val viewModel : FormViewVM =  koinInject()






        BaseScreen(
            viewModel = viewModel,

            title = stringResource(MR.strings.form),
            scaffoldState = scaffoldState,
            topBar = {
                MenuItemsTopBar(stringResource(MR.strings.form)) {
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
                        titleDropDown = stringResource(MR.strings.select_an_item),
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
                        titleDropDown = stringResource(MR.strings.select_an_item),
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


                    Radio(title=stringResource(MR.strings.select_an_item),itemList = items, selectItem = "", onItemSelected = {

                        println("multiChoooice>>>>${it}")

                    })

                    CheckList(

                        title=stringResource(MR.strings.choose_more),

                        itemList = items,
                        onItemSelected = {

                            println("multiChoooice>>>>${it.size}")

                        })

                    Editable(TypeEditable.EMAIL, placeholder = "email", imeAction = ImeAction.Next, leadingIcon =null , trailingIcon =null, keyboardType = KeyboardType.Text, maxLines = 2, readOnly = false,onValueChange = {} )


                }

            }

        )


    }

}



