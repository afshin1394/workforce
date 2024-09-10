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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irancell.nwg.wfm.presentation.theme.spacing05X
import com.mohamedrejeb.calf.ui.datepicker.AdaptiveDatePicker
import com.mohamedrejeb.calf.ui.datepicker.rememberAdaptiveDatePickerState
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import domain.models.form_struct.ProcessLogicDomain
import irancell.nwg.wfm.ConvertStringToTimeStamp
import irancell.nwg.wfm.DatePickerFormat.format
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.getSharedPref
import kotlinx.coroutines.launch
import presentation.theme.h4
import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceBrandDisabled
import presentation.theme.textInverseDisabled
import presentation.theme.textSecondary
import utils.IsScrollDateTimePickerInList
import utils.getLocalDateTimeFromLong


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDatePicker(
    readOnly : Boolean,
    processLogicDomain: ProcessLogicDomain,
    title: String,
    titleDatePiker: String,
    errorMessage: ResourceFormattedStringDesc,
    onDateSelected: (selectItem: String) -> Unit
) {

    val disableLogic = processLogicDomain.disabled
    val hideLogic = processLogicDomain.shouldHide
    val readOnlyLogic = processLogicDomain.readOnly || readOnly
    val requiredLogic = processLogicDomain.required
    val validateLogic = processLogicDomain.validate
    val errorMessageValidateLogic = processLogicDomain.errorMessage




    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by remember { mutableStateOf(title) }


    var selectDate by remember {
        mutableStateOf(
            if (title.take(2).all { it.isDigit() }) title.trim().substringBefore("  ") else ""
        )
    }


    fun updateTitleAndDate() {
        val updatedValue = "${selectDate} "
        title = updatedValue
        onDateSelected(updatedValue)
    }
    if(!hideLogic) {
        Column(Modifier.padding(16.dp)) {

            val styledString = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = if (disableLogic || readOnlyLogic) textInverseDisabled else textSecondary,
                        fontSize = 14.sp
                    )
                ) {
                    append(titleDatePiker)
                }
                if (requiredLogic) {
                    withStyle(style = SpanStyle(color = Color.Red, fontSize = 18.sp)) {
                        append(" *")
                    }
                }
            }

            Text(
                text = styledString,
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            )
            Spacer(modifier = Modifier.padding(top = spacing05X))

            TextField(value = title,
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (errorMessage.localized() != "") Color.Red else strokeDefaultLight,
                        shape = RoundedCornerShape(15.dp)
                    ).clickable {
                        scope.launch {
                            if (!(disableLogic || readOnlyLogic)) {
                                isBottomSheetVisible = !isBottomSheetVisible
                                sheetState.expand()
                            }
                        }
                    },
                readOnly = true,
                shape = RoundedCornerShape(15.dp),
                textStyle = TextStyle(color = textSecondary),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = if (readOnlyLogic || disableLogic) surfaceBrandDisabled else Color.Transparent,
                    disabledIndicatorColor = if (readOnlyLogic || disableLogic) surfaceBrandDisabled else Color.Transparent,
                    unfocusedIndicatorColor = if (readOnlyLogic || disableLogic) surfaceBrandDisabled else Color.Transparent,
                    focusedContainerColor = if (readOnlyLogic || disableLogic) surfaceBrandDisabled else Color.White,
                    unfocusedContainerColor = if (readOnlyLogic || disableLogic) surfaceBrandDisabled else Color.White,
                    disabledContainerColor = if (readOnlyLogic || disableLogic) surfaceBrandDisabled else Color.White
                ),

                trailingIcon = {

                    Icon(
                        painter = painterResource(MR.images.calendar),
                        "deleteAllSelected",
                        Modifier.width(28.dp).height(28.dp).padding(end = 8.dp)
                            .clickable {
                                scope.launch {
                                    if (!(disableLogic || readOnlyLogic)) {
                                        isBottomSheetVisible = !isBottomSheetVisible
                                        sheetState.expand()
                                    }
                                }
                            },
                        tint = textSecondary
                    )

                })


            if (errorMessage.localized() != "") {
                Text(
                    text = errorMessage.localized(),
                    color = Color.Red,
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (validateLogic) {
                errorMessageValidateLogic?.let {
                    Text(
                        text = errorMessageValidateLogic.localized(),
                        color = Color.Red,
                        style = TextStyle(fontSize = 12.sp),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            BottomSheet(
                isBottomSheetVisible = isBottomSheetVisible,
                sheetState = sheetState,
                dateSelected = title,
                onDateSelected = {
                    selectDate = it
                    updateTitleAndDate()

                },
                titleDatePiker = titleDatePiker,
                onDismiss = {
                    scope.launch { sheetState.hide() }

                    isBottomSheetVisible = false
                }

            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    isBottomSheetVisible: Boolean,
    sheetState: SheetState,
    titleDatePiker: String,
    dateSelected: String,
    onDateSelected: (selectItem: String) -> Unit,
    onDismiss: () -> Unit,

    ) {

    var isCancelclick by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberAdaptiveDatePickerState()
    var data by remember { mutableStateOf(dateSelected) }


    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {


            if (isCancelclick) {

                isCancelclick = false

            } else {

                if (getSharedPref().getBool(IsScrollDateTimePickerInList, false)) {
                    data = getLocalDateTimeFromLong(it).format("yyyy-MM-dd")
                    onDateSelected(data)

                }


            }

        } ?: let {

            if (dateSelected.take(2).all { it.isDigit() })
                datePickerState.setSelection(
                    ConvertStringToTimeStamp(
                        dateSelected.trim().substringBefore("  ")
                    )
                )
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
                    .background(color = MaterialTheme.colorScheme.background).fillMaxWidth()
                    .padding(12.dp)
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {


                    Box(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight()
                    ) {

                        Image(painter = painterResource(MR.images.close),
                            contentDescription = "ic_close",
                            modifier = Modifier.clickable {
                                onDismiss()
                                isCancelclick = true

                            })

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






                    Row(Modifier.height(IntrinsicSize.Min).padding(bottom = 16.dp)) {

                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                isCancelclick = true
                            },
                            modifier = Modifier.fillMaxHeight().padding(end = 12.dp).height(48.dp)
                                .weight(1f),
                            border = BorderStroke(1.dp, strokeDefaultLight),
                            shape = RoundedCornerShape(20),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.Black, containerColor = Color.White
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
                            modifier = Modifier.fillMaxHeight().padding(start = 12.dp).height(48.dp)
                                .weight(1f)
                        ) {
                            Text(
                                text = stringResource(MR.strings.accept),
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
