package presentation.screens.main.components.formViewer


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import com.mohamedrejeb.calf.ui.datepicker.rememberAdaptiveDatePickerState
import com.mohamedrejeb.calf.ui.timepicker.AdaptiveTimePicker
import com.mohamedrejeb.calf.ui.timepicker.rememberAdaptiveTimePickerState
import dev.icerock.moko.resources.compose.painterResource
import irancell.nwg.wfm.DatePickerFormat.format
import irancell.nwg.wfm.IntentHandler
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.getSharedPref
import irancell.nwg.wfm.provideAppContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import presentation.theme.h4
import presentation.theme.strokeDefaultLight
import presentation.theme.subtleDefault
import presentation.theme.surfaceBrandDefault
import presentation.theme.textSecondary
import utils.Language
import utils.SelectLanguage
import utils.getLocalDateTimeFromLong
import utils.isRunningGPS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalTimePicker(title:String,titleDatePiker:String, onTimeSelected: (selectItem: String) -> Unit) {

    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by remember { mutableStateOf(title) }




    Column(Modifier.padding(16.dp)) {
        TextField(value = title,
            onValueChange = {  },
            modifier = Modifier
                .fillMaxWidth()

                .border(
                    width = 1.dp,
                    color = strokeDefaultLight,
                    shape = RoundedCornerShape(15.dp)
                ).clickable {
                    scope.launch {
                        isBottomSheetVisible = !isBottomSheetVisible
                        sheetState.expand()
                    }
                }
            ,
            readOnly = true,
            shape = RoundedCornerShape(8.dp),
            textStyle = TextStyle(color = textSecondary),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White
            ),

            trailingIcon = {

                Icon(
                    painter = painterResource(MR.images.clock),
                    "deleteAllSelected",
                    Modifier.width(28.dp).height(28.dp).padding(end = 8.dp)
                        .clickable {


                            scope.launch {
                                isBottomSheetVisible = !isBottomSheetVisible
                                sheetState.expand()
                            }
                        },
                    tint = textSecondary
                )

            })

        BottomSheetTime(
            isBottomSheetVisible = isBottomSheetVisible,
            sheetState = sheetState,
            onTimeSelected = {
                title = it
                onTimeSelected(it)

            },
            titleDatePiker = titleDatePiker,
            onDismiss = {
                scope.launch { sheetState.hide() }

                isBottomSheetVisible = false
            }

        )
    }
}






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetTime(
    isBottomSheetVisible: Boolean,
    sheetState: SheetState,
    titleDatePiker:String,
    onTimeSelected: (selectItem: String) -> Unit,
    onDismiss: () -> Unit
) {




    val datePickerState = rememberAdaptiveDatePickerState()

    var initialSelection  by remember { mutableStateOf("") }

    var startTime by remember { mutableStateOf("") }

    val startTimePickerState = rememberAdaptiveTimePickerState()
    val time = getTimeProgress(
        datePickerState.selectedDateMillis,
        startTimePickerState.hour,
        startTimePickerState.minute
    )
    LaunchedEffect(
        time
    ) {

        if (initialSelection!=""){
            startTime = time
            onTimeSelected(time)

        }else{
            initialSelection =time
        }

    }



    if (isBottomSheetVisible) {

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,


            dragHandle = null,
            scrimColor = Color.Black.copy(alpha = .5f),
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {

            Column(
                modifier = Modifier

                    .clip(shape = RoundedCornerShape(topEnd = 4.dp, topStart = 4.dp))
                    .background(color = MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {


                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {

                        Image(
                            painter = painterResource(MR.images.close),
                            contentDescription = "ic_close",
                            modifier = Modifier
                                .clickable {
                                    onDismiss()
                                }
                        )

                        Text(
                            text = titleDatePiker,
                            style = h4,
                            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                            textAlign = TextAlign.Center
                        )

                    }
                    Spacer(modifier = Modifier.padding(top = spacing15X))

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr ) {

                        AdaptiveTimePicker(
                            state = startTimePickerState,

                            colors = TimePickerDefaults.colors(
                                clockDialColor= subtleDefault,
                                containerColor=Color.White,
                                periodSelectorSelectedContainerColor= subtleDefault,
                                timeSelectorSelectedContainerColor= subtleDefault,
                                timeSelectorUnselectedContainerColor=strokeDefaultLight,
                                timeSelectorSelectedContentColor= textSecondary,
                                selectorColor=surfaceBrandDefault

                            )


                        )

                    }



                }

            }

        }



    }




}

fun getTimeProgress(date: Long?, hour: Int, minute: Int): String {
    val localDate = getLocalDateTimeFromLong(date ?: Clock.System.now().toEpochMilliseconds())
    val localTime = LocalTime(hour, minute)
    val localObj = LocalDateTime(localDate.date, localTime)
    return localObj.format("hh:mm a")

}



