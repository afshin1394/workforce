

package presentation.screens.main.components.formViewer


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import domain.models.form_struct.InitProcessLogicDomain
import domain.models.form_struct.ProcessLogicDomain
import domain.models.form_struct.ValueDomain
import presentation.theme.surfaceBrandDark
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceBrandDisabled
import presentation.theme.textInverseDisabled
import presentation.theme.textSecondary

@Composable
fun TicketInfoRadio(
    readOnly: Boolean,
    disable: Boolean,
    processLogicDomain: InitProcessLogicDomain,
    title: String,
    itemList: List<String>,
    selectItem: String,
    onItemSelected: (selectItem: String) -> Unit
) {

    var select by remember { mutableStateOf(selectItem) }
    val disableLogic = processLogicDomain.disabled || disable
    val readOnlyLogic = processLogicDomain.readOnly || readOnly
    val validateLogic = processLogicDomain.validate
    val requiredLogic = processLogicDomain.required
    val hasInitialMessageLogic = processLogicDomain.hasInitialMessage
    val errorMessageLogic = processLogicDomain.errorMessage

    if (!processLogicDomain.shouldHide) {


        Column(Modifier.padding(16.dp)) {

            val styledString = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = if (disableLogic || readOnlyLogic) textInverseDisabled else textSecondary,
                        fontSize = 14.sp
                    )
                ) {
                    append(title)
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


            Spacer(modifier = Modifier.padding(top = spacing15X))
            LazyColumn(modifier = Modifier.heightIn(0.dp, 500.dp)) {
                items(itemList.size) { index ->
                    val item = itemList[index]

                    TIItemRadioList(
                        disableLogic,
                        readOnlyLogic,
                        isSelected = select,
                        item = item ?: "",
                        onItemSelected = {
                            select = item ?: ""
                            onItemSelected(it)
                        }
                    )
                }
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


        }
    }

}


@Composable
fun TIItemRadioList(
    disable: Boolean,
    readOnly: Boolean,
    isSelected: String,
    item: String,
    onItemSelected: (selectItem: String) -> Unit
) {
    var selectedText by remember { mutableStateOf(item) }
    val isSelected = selectedText == isSelected
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(
            selected = isSelected,
            colors = RadioButtonDefaults.colors(
                selectedColor = if (disable || readOnly) surfaceBrandDisabled else surfaceBrandDefault,
                disabledUnselectedColor = if (disable || readOnly) surfaceBrandDisabled else surfaceBrandDark,
                unselectedColor = if (disable || readOnly) surfaceBrandDisabled else surfaceBrandDark
            ),
            onClick = {
                if (!(disable || readOnly)) {
                    selectedText = item
                    onItemSelected(item)
                }
            }
        )
        Text(
            text = item,
            style = TextStyle(color = if (disable || readOnly) textInverseDisabled else textSecondary)
        )
    }

}