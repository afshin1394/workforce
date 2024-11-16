package presentation.screens.main.components.formViewer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irancell.nwg.wfm.presentation.theme.spacing05X
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import com.mohamedrejeb.calf.ui.datepicker.AdaptiveDatePicker
import com.mohamedrejeb.calf.ui.datepicker.rememberAdaptiveDatePickerState
import com.mohamedrejeb.calf.ui.timepicker.AdaptiveTimePicker
import com.mohamedrejeb.calf.ui.timepicker.rememberAdaptiveTimePickerState
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import domain.models.form_struct.ProcessLogicDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.ConvertStringToTimeStamp
import irancell.nwg.wfm.DatePickerFormat.format
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.getSharedPref
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import presentation.theme.h4
import presentation.theme.strokeDefaultLight
import presentation.theme.subtleDefault
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceBrandDisabled
import presentation.theme.textInverseDisabled
import presentation.theme.textSecondary
import utils.IsScrollDateTimePickerInList
import utils.getLocalDateTimeFromLong


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDateTimePicker(
    readOnly: Boolean,
    disable: Boolean,
    processLogicDomain: ProcessLogicDomain,
    title: String,
    titleDatePiker: String,
    onDateSelected: (selectDateItem: String) -> Unit,


    ) {
    val disableLogic = processLogicDomain.disabled || disable
    val hideLogic = processLogicDomain.shouldHide
    val readOnlyLogic = processLogicDomain.readOnly || readOnly
    val requiredLogic = processLogicDomain.required
    val validateLogic = processLogicDomain.validate
    val errorMessageLogic = processLogicDomain.errorMessage
    val hasInitialMessageLogic = processLogicDomain.hasInitialMessage
    val isAutoFilling = processLogicDomain.isAutoFillLoading

    val backgroundColor = if (validateLogic || (requiredLogic && !hasInitialMessageLogic)) {
        Color.Red
    } else if (readOnlyLogic || disableLogic) {
        surfaceBrandDisabled
    } else {
        strokeDefaultLight
    }
    val scope = rememberCoroutineScope()
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by remember { mutableStateOf(title) }

    Napier.log(LogLevel.ASSERT, tag = "calculatedValue", message = processLogicDomain.toString())

    LaunchedEffect(processLogicDomain.calculatedValue) {
        processLogicDomain.calculatedValue?.let {
            if (it.isNotEmpty()) {
                title = it
            }
        }
    }
    Napier.log(
        LogLevel.ASSERT,
        tag = "calculatedValue",
        message = processLogicDomain.calculatedValue.toString()
    )

    var selectDate by remember {
        mutableStateOf(
            if (title.take(2).all { it.isDigit() }) title.trim().substringBefore("  ") else ""
        )
    }
    var selectTime by remember { mutableStateOf("") }

    fun updateTitleAndDate() {
        val updatedValue = "$selectDate  $selectTime"
        title = updatedValue
        onDateSelected(updatedValue)
    }
    if (!hideLogic) {
        Column(Modifier.padding(12.dp)) {

            val styledString = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = if (disableLogic || readOnlyLogic) textInverseDisabled else textSecondary,
                        fontSize = 14.sp
                    )
                ) {
                    append(titleDatePiker)
                }
                if (requiredLogic || validateLogic) {
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(4.dp)
            ) {
                if (isAutoFilling) {
                    val infiniteTransition = rememberInfiniteTransition()
                    val progress by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        )
                    )
                    Canvas(
                        modifier = Modifier
                            .matchParentSize()
                            .padding(2.dp)
                    ) {
                        val strokeWidth = 2.dp.toPx()
                        val pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(15f, 15f),
                            phase = progress * 300f
                        )
                        drawRoundRect(
                            color = surfaceBrandDefault,
                            topLeft = Offset(0f, 0f),
                            size = Size(size.width, size.height),
                            cornerRadius = CornerRadius(15.dp.toPx(), 15.dp.toPx()),
                            style = Stroke(
                                width = strokeWidth,
                                pathEffect = pathEffect,
                                cap = StrokeCap.Round
                            )
                        )
                    }
                } else {
                    Canvas(
                        modifier = Modifier
                            .matchParentSize()
                            .padding(2.dp)
                    ) {
                        val strokeWidth = 0.5.dp.toPx()
                        drawRoundRect(
                            color = backgroundColor,
                            topLeft = Offset(0f, 0f),
                            size = Size(size.width, size.height),
                            cornerRadius = CornerRadius(15.dp.toPx(), 15.dp.toPx()),
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Round
                            )
                        )
                    }
                }

                TextField(value = title,
                    onValueChange = { },
                    modifier = if (isAutoFilling) {
                        Modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                    } else {
                        Modifier.fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = backgroundColor,
                                shape = RoundedCornerShape(15.dp)
                            ).clickable {
                                scope.launch {
                                    if (!(disableLogic || readOnlyLogic)) {
                                        isBottomSheetVisible = !isBottomSheetVisible
                                        sheetState.expand()
                                    }
                                }
                            }
                    },
                    readOnly = true,
                    shape = RoundedCornerShape(15.dp),
                    textStyle = TextStyle(color = textSecondary),
                    colors =  TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedContainerColor = if (readOnlyLogic || disableLogic) surfaceBrandDisabled else Color.White,
                        unfocusedContainerColor = if (readOnlyLogic || disableLogic) surfaceBrandDisabled else Color.White,
                        disabledContainerColor = if (readOnlyLogic || disableLogic) surfaceBrandDisabled else Color.White
                    ),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(MR.images.calendar),
                            "deleteAllSelected",
                            Modifier.width(28.dp).height(28.dp).padding(end = 8.dp).clickable {
                                scope.launch {
                                    if (!(disableLogic || readOnlyLogic)) {
                                        isBottomSheetVisible = !isBottomSheetVisible
                                        sheetState.expand()
                                        getSharedPref().put(IsScrollDateTimePickerInList, true)
                                    }

                                }
                            },
                            tint = textSecondary
                        )
                    }
                )
            }
            if (validateLogic || (requiredLogic && !hasInitialMessageLogic)) {
                errorMessageLogic?.localized()?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        style = TextStyle(fontSize = 12.sp),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            BottomSheetDate(
                isBottomSheetVisible = isBottomSheetVisible,
                sheetState = sheetState,
                dateSelected = title,
                onDateSelected = {
                    selectDate = it
                    updateTitleAndDate()
                },
                titleDatePiker = titleDatePiker,
                onTimeSelected = {
                    selectTime = it
                    updateTitleAndDate()
                },
                onDismiss = {
                    scope.launch { sheetState.hide() }
                    isBottomSheetVisible = false

                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDate(
    isBottomSheetVisible: Boolean,
    sheetState: SheetState,
    titleDatePiker: String,
    dateSelected: String,
    onDateSelected: (selectItem: String) -> Unit,
    onTimeSelected: (selectItem: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var isTimeBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val sheetTimeState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isCancelclick by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val datePickerState = rememberAdaptiveDatePickerState()
    var data by remember { mutableStateOf(dateSelected) }
    var timeSelected by remember {
        mutableStateOf(
            if (dateSelected.contains("AM") || dateSelected.contains(
                    "PM"
                )
            ) dateSelected.substringAfter("  ") else ""
        )
    }
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            if (isCancelclick) {
                isCancelclick = false
            } else {
                if (getSharedPref().getBool(IsScrollDateTimePickerInList, false)) {
                    data = getLocalDateTimeFromLong(it).format("yyyy-MM-dd")
                    onDateSelected(data)
                    if (dateSelected.contains("AM") || dateSelected.contains("PM"))
                        onTimeSelected(dateSelected.substringAfter("  "))
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
                    Row(Modifier.height(IntrinsicSize.Min)) {
                        TextField(value = timeSelected,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp).weight(2f)
                                .border(
                                    width = 1.dp,
                                    color = strokeDefaultLight,
                                    shape = RoundedCornerShape(15.dp)
                                ),
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
                                        .clickable {
                                            scope.launch {
                                                isTimeBottomSheetVisible = !isTimeBottomSheetVisible
                                                sheetTimeState.expand()

                                            }
                                        },
                                    tint = textSecondary
                                )

                            })

                        Text(
                            text = stringResource(MR.strings.now),
                            style = TextStyle(color = Color.Blue),
                            modifier = Modifier.weight(1f).padding(top = 18.dp).clickable {
                                val currentTime = Clock.System.now()
                                    .toLocalDateTime(TimeZone.currentSystemDefault())
                                    .format("HH:mm a")
                                timeSelected = currentTime
                                onTimeSelected(timeSelected)


                            },
                            textAlign = TextAlign.Center
                        )
                    }
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
            BottomSheetTimeDate(isBottomSheetVisible = isTimeBottomSheetVisible,
                sheetState = sheetTimeState,
                onTimeSelected = {
                    timeSelected = it
                    onTimeSelected(it)
                },
                titleDatePiker = titleDatePiker,
                onDismiss = {
                    scope.launch { sheetTimeState.hide() }
                    isTimeBottomSheetVisible = false
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetTimeDate(
    isBottomSheetVisible: Boolean,
    sheetState: SheetState,
    titleDatePiker: String,
    onTimeSelected: (selectItem: String) -> Unit,
    onDismiss: () -> Unit
) {

    val datePickerState = rememberAdaptiveDatePickerState()
    var startTime by remember { mutableStateOf("") }
    val startTimePickerState = rememberAdaptiveTimePickerState()
    var initialSelection by remember { mutableStateOf("") }
    val time = getTimeProgress(
        datePickerState.selectedDateMillis, startTimePickerState.hour, startTimePickerState.minute
    )
    LaunchedEffect(
        time
    ) {
        if (initialSelection != "") {
            startTime = time
            onTimeSelected(time)
        } else {
            initialSelection = time
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
                    .padding(24.dp)
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
                            })

                        Text(
                            text = titleDatePiker,
                            style = h4,
                            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                            textAlign = TextAlign.Center
                        )

                    }
                    Spacer(modifier = Modifier.padding(top = spacing15X))
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        AdaptiveTimePicker(
                            state = startTimePickerState, colors = TimePickerDefaults.colors(
                                clockDialColor = subtleDefault,
                                containerColor = Color.White,
                                periodSelectorSelectedContainerColor = subtleDefault,
                                timeSelectorSelectedContainerColor = subtleDefault,
                                timeSelectorUnselectedContainerColor = strokeDefaultLight,
                                timeSelectorSelectedContentColor = textSecondary,
                                selectorColor = surfaceBrandDefault
                            )
                        )
                    }
                }
            }
        }
    }
}
