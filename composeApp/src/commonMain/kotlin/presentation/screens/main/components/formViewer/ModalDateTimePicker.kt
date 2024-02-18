package presentation.screens.main.components.formViewer

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.mohamedrejeb.calf.ui.datepicker.AdaptiveDatePicker
import com.mohamedrejeb.calf.ui.datepicker.rememberAdaptiveDatePickerState
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.DatePickerFormat.format
import irancell.nwg.wfm.MR
import kotlinx.coroutines.launch
import presentation.theme.h4
import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceBrandDefault
import presentation.theme.textSecondary
import utils.getLocalDateTimeFromLong


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDateTimePicker (title:String,titleDatePiker:String, onDateSelected: (selectItem: String) -> Unit) {

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
                    painter = painterResource(MR.images.calendar),
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

        BottomSheet2(
            isBottomSheetVisible = isBottomSheetVisible,
            sheetState = sheetState,
            onDateSelected = {
                title = it
                onDateSelected(it)

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
fun BottomSheet2(
    isBottomSheetVisible: Boolean,
    sheetState: SheetState,
    titleDatePiker:String,
    onDateSelected: (selectItem: String) -> Unit,
    onDismiss: () -> Unit
) {


    var isCancelclick by rememberSaveable { mutableStateOf(false) }

    val datePickerState = rememberAdaptiveDatePickerState()
    var data by remember { mutableStateOf("") }

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {


            if (isCancelclick){

                isCancelclick=false
            }else{
                data = getLocalDateTimeFromLong(it).format("yyyy-mm-dd")
                onDateSelected(data)
            }

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
                    .padding(20.dp)
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
                                    isCancelclick=true

                                }
                        )

                        Text(
                            text = titleDatePiker,
                            style = h4,
                            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                            textAlign = TextAlign.Center
                        )

                    }

                    AdaptiveDatePicker(
                        state = datePickerState,
                        colors = DatePickerDefaults.colors(
                            weekdayContentColor = textSecondary,
                            containerColor = Color.White,
                            dayContentColor = textSecondary,
                            selectedDayContainerColor = surfaceBrandDefault,
                            selectedYearContainerColor = surfaceBrandDefault

                        ),


                        )

                    Row(Modifier.height(IntrinsicSize.Min)) {


                        TextField(value = "",
                            onValueChange = {  },
                            modifier = Modifier
                                .fillMaxWidth().padding(bottom = 18.dp)
                                .weight(2f)
                                .border(
                                    width = 1.dp,
                                    color = strokeDefaultLight,
                                    shape = RoundedCornerShape(15.dp)
                                )
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

                            leadingIcon = {

                                    Icon(
                                        painter = painterResource(MR.images.clock),
                                        "deleteAllSelected",
                                        Modifier.width(28.dp).height(28.dp).padding(end = 8.dp)
                                            .clickable { },
                                        tint = textSecondary)

                            })


                        Text(
                            text = "Now",
                            style = TextStyle(color = Color.Blue),
                            modifier = Modifier.weight(1f).padding(top=18.dp),
                            textAlign = TextAlign.Center
                        )




                    }





                    Row(Modifier.height(IntrinsicSize.Min).padding(bottom = 16.dp)) {

                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                isCancelclick=true
                            },
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(end = 12.dp)
                                .height(48.dp)
                                .weight(1f),
                            border = BorderStroke(1.dp, strokeDefaultLight),
                            shape = RoundedCornerShape(20),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.Black,
                                containerColor = Color.White
                            )
                        ) {

                            Text(
                                stringResource(MR.strings.cancel),
                                color = textSecondary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp
                            )

                        }

                        Button(
                            onClick = {
                                onDismiss()

                            },
                            shape = RoundedCornerShape(20),
                            colors = ButtonDefaults.buttonColors(containerColor = surfaceBrandDefault),
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(start = 12.dp)
                                .height(48.dp)
                                .weight(1f)
                        ) {
                            Text(
                                text = "Accept",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp
                            )
                        }
                    }

                }

            }

        }


    }

}