package presentation.screens.main.components.formViewer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import domain.models.form_struct.ProcessLogicDomain
import domain.models.form_struct.ValueDomain
import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceBrandDisabled
import presentation.theme.textInverseDisabled
import presentation.theme.textPlaceHolder
import presentation.theme.textSecondary


@Composable
fun CheckList(
    readOnly : Boolean,
    disable : Boolean,
    processLogicDomain: ProcessLogicDomain,
    title: String,
    errorMessage: ResourceFormattedStringDesc,
    selectItem: String,
    itemList: List<ValueDomain>,
    onSelect: (ValueDomain) -> Unit
) {
    val disableLogic = processLogicDomain.disabled ||disable
    val hideLogic = processLogicDomain.shouldHide
    val readOnlyLogic = processLogicDomain.readOnly|| readOnly
    val requiredLogic = processLogicDomain.required
    val validateLogic = processLogicDomain.validate
    val errorMessageValidateLogic = processLogicDomain.errorMessage


    val backgroundColor = if (errorMessage.localized() != "" || validateLogic){
        Color.Red
    }else if(readOnlyLogic || disableLogic){
        surfaceBrandDisabled
    }else{
        strokeDefaultLight
    }

    val currentErrorMessage by rememberUpdatedState(errorMessage)
    val selectedListState = remember { itemList.map { it.isSelected }.toMutableStateList() }

    LaunchedEffect(selectItem) {
        selectedListState.clear()
        selectedListState.addAll(itemList.map { it.isSelected }.toMutableStateList())
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
                    append(title)
                }
                if (requiredLogic) {
                    withStyle(style = SpanStyle(color = Color.Red, fontSize = 18.sp)) {
                        append(" *")
                    }
                }
            }


            Text(
                text = styledString,
                style = TextStyle(color = backgroundColor, fontSize = 16.sp),
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            )

            LazyColumn(modifier = Modifier.heightIn(0.dp, 500.dp)) {
                items(itemList.size) { index ->
                    val item = itemList[index]
                    val selectedItem = selectedListState[index]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox( enabled = (!disableLogic || !readOnlyLogic), checked = selectedItem, colors = CheckboxDefaults.colors(
                            checkedColor = if(readOnlyLogic || disableLogic) surfaceBrandDisabled else surfaceBrandDefault,
                            uncheckedColor = if(readOnlyLogic || disableLogic) surfaceBrandDisabled else surfaceBrandDefault,
                        ), onCheckedChange = { checked_ ->
                            if(!(disableLogic || readOnlyLogic)) {
                                selectedListState[index] = checked_
                                item.isSelected = checked_
                                onSelect(item)
                            }
                        })
                        Text(
                            text = item.label?:"",
                            style = TextStyle(color = if(readOnlyLogic || disableLogic) textPlaceHolder else textSecondary)
                        )
                    }
                }
            }


            if (currentErrorMessage.localized().isNotEmpty()) {
                Text(
                    text = currentErrorMessage.localized(),
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

