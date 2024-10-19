package presentation.screens.main.components.formViewer

import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
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
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import domain.models.form_struct.ProcessLogicDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceBrandDisabled
import presentation.theme.textInverseDisabled
import presentation.theme.textSecondary


@Composable
fun Editable(
    type: TypeEditable,
    processLogicDomain: ProcessLogicDomain,
    value: String,
    showErrorMessageValidation:Boolean,
    placeholder: String,
    imeAction: ImeAction,
    keyboardType: KeyboardType,
    readOnly: Boolean,
    disable: Boolean,
    maxLines: Int,
    errorMessage: ResourceFormattedStringDesc,
    onValueChange: (value: String) -> Unit
) {
    Napier.log(LogLevel.ASSERT, tag = "Editable Editable", message =  placeholder)
    Napier.log(LogLevel.ASSERT, tag = "Editable calculatedValue", message =   processLogicDomain.calculatedValue.toString())
    Napier.log(LogLevel.ASSERT, tag = "Editable value", message =   value)
    val valueChange  =    mutableStateOf(processLogicDomain.calculatedValue?:value)



    val disableLogic = processLogicDomain.disabled ||disable
    val hideLogic = processLogicDomain.shouldHide
    val readOnlyLogic = processLogicDomain.readOnly || readOnly
    val requiredLogic = processLogicDomain.required
    val validateLogic = processLogicDomain.validate
    val errorMessageValidateLogic = processLogicDomain.errorMessage
    val textFieldBackground = if (errorMessage.localized() != "" && !showErrorMessageValidation|| validateLogic) {
        Color.Red
    } else if (readOnlyLogic || disableLogic) {
        surfaceBrandDisabled
    } else {
        strokeDefaultLight
    }

    Napier.log(LogLevel.ASSERT, tag = "processLogicDomain", message = placeholder)
    Napier.log(LogLevel.ASSERT, tag = "processLogicDomain", message = processLogicDomain.toString())

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
                if (requiredLogic||errorMessage.localized() != "") {
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
                        valueChange.value = it
                        onValueChange(valueChange.value)
                        processLogicDomain.calculatedValue = it
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = textFieldBackground,
                        shape = RoundedCornerShape(15.dp)
                    ),
                readOnly = readOnly,
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
            if (errorMessage.localized() != "" && !showErrorMessageValidation) {
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
    var valueChange by remember { mutableStateOf(value) }




    Column(Modifier.padding(16.dp)) {

        val styledString = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = textSecondary,
                    fontSize = 14.sp
                )
            ) {
                append(key)
            }

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
            onValueChange = {


            },
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