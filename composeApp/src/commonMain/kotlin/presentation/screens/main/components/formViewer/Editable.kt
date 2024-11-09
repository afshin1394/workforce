package presentation.screens.main.components.formViewer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irancell.nwg.wfm.presentation.theme.spacing05X
import dev.icerock.moko.resources.compose.localized
import domain.models.form_struct.ProcessLogicDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceBrandDisabled
import presentation.theme.textInverseDisabled
import presentation.theme.textSecondary

@OptIn(FlowPreview::class)
@Composable
fun Editable(
    type: TypeEditable,
    processLogicDomain: ProcessLogicDomain,
    value: String,
    placeholder: String,
    imeAction: ImeAction,
    keyboardType: KeyboardType,
    readOnly: Boolean,
    disable: Boolean,
    maxLines: Int,
    onValueChange: (value: String) -> Unit
) {
    val initialCalculatedValue = processLogicDomain.calculatedValue ?: value
    val valueChange = remember { mutableStateOf(initialCalculatedValue) }


    val disableLogic = processLogicDomain.disabled || disable
    val hideLogic = processLogicDomain.shouldHide
    val readOnlyLogic = processLogicDomain.readOnly || readOnly
    val requiredLogic = processLogicDomain.required
    val validateLogic = processLogicDomain.validate
    val errorMessageLogic = processLogicDomain.errorMessage
    val hasInitialMessageLogic = processLogicDomain.hasInitialMessage
    val isAutoFilling = processLogicDomain.isAutoFillLoading

    val valueChangeFlow = remember { MutableStateFlow(valueChange.value) }


    LaunchedEffect(Unit) {
        valueChangeFlow
            .debounce(200) // Only emit if 500 ms has passed since the last change
            .distinctUntilChanged() // Only emit if the value has actually changed
            .flatMapLatest { latestValue ->
                flow {
                    emit(latestValue)
                }
            }
            .collectLatest { latestValue ->
                if (!disableLogic && !readOnlyLogic ) {
                    onValueChange(latestValue)
                }
            }
    }
    val textFieldBackground =
        if (validateLogic || (requiredLogic && !hasInitialMessageLogic)) {
            Color.Red
        } else if (readOnlyLogic || disableLogic) {
            surfaceBrandDisabled
        } else {
            strokeDefaultLight
        }
    if (hideLogic) {
        valueChange.value = ""
        processLogicDomain.calculatedValue = ""
    }
    if (!hideLogic) {
        Column(Modifier.padding(16.dp)) {
            val styledString = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = if (disableLogic || readOnlyLogic) textInverseDisabled else textSecondary,
                        fontSize = 14.sp
                    )
                ) {
                    append(placeholder)
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
                            color = textFieldBackground,
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

                TextField(
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = keyboardType,
                        imeAction = imeAction
                    ),
                    maxLines = maxLines,
                    value = valueChange.value,
                    onValueChange = {
                        if (!disableLogic && !readOnlyLogic && it != valueChange.value) {
                            valueChange.value = it // Update UI immediately
                            valueChangeFlow.value = it // Emit new value to flow

                        }
                    },
                    modifier = if (isAutoFilling) {
                        Modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                            .border(
                                width = 1.dp,
                                color = textFieldBackground,
                                shape = RoundedCornerShape(15.dp)
                            )
                    },
                    readOnly = readOnlyLogic,
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
                )


            }
            if (validateLogic || (requiredLogic && !hasInitialMessageLogic)) {
                errorMessageLogic?.let {
                    Text(
                        text = errorMessageLogic.localized(),
                        color = Color.Red,
                        style = TextStyle(fontSize = 12.sp),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

enum class TypeEditable {
    PHONE,
    EMAIL,
    SHORT_TEXT,
    TEXTAREA,
    LATLONG,
    NUMBER
}

@Composable
fun SimpleEditable(
    key: String,
    value: String,
) {
    Napier.log(LogLevel.ASSERT, tag = "Editablevalue", message = value)
    val valueChange by remember { mutableStateOf(value) }
    Column(Modifier.padding(16.dp)) {
        val styledString = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = textSecondary,
                    fontSize = 14.sp
                )
            ) { append(key) }
        }
        Text(
            text = styledString,
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        )
        Spacer(modifier = Modifier.padding(top = spacing05X))

        TextField(
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
            ),
            value = valueChange,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = surfaceBrandDefault,
                    shape = RoundedCornerShape(15.dp)
                ),
            shape = RoundedCornerShape(15.dp),
            textStyle = TextStyle(color = textSecondary),
            readOnly = true,
            colors =
            TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White
            ),
        )
    }
}