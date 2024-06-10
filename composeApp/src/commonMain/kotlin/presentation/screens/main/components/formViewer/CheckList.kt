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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.models.initialForm.ValueDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceBrandDefault
import presentation.theme.textSecondary



@Composable
fun CheckList(title:String,  itemList: List<ValueDomain>,onSelect : (ValueDomain) -> Unit) {
    val selectedListState = remember {   itemList.map { it.isSelected }}.toMutableStateList()
    Column(Modifier.padding(16.dp)) {
        Text(
            text = title,
            style = TextStyle(color = textSecondary, fontSize = 16.sp),
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),

            )


        LazyColumn(modifier = Modifier.heightIn(0.dp, 500.dp)) {
            items(itemList.size) { index ->
                val item = itemList[index]
                val selectedItem =   selectedListState[index]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Checkbox(checked = selectedItem, colors = CheckboxDefaults.colors(
                        checkedColor = surfaceBrandDefault,
                        uncheckedColor = strokeDefaultLight
                    ), onCheckedChange = { checked_ ->
                        selectedListState[index] = checked_
                        item.isSelected =checked_
                        onSelect(item)
                    })
                    Text(
                        text = item.label!!,
                        style = TextStyle(color = textSecondary)
                    )
                }

            }
        }

    }


}

