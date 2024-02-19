package presentation.screens.main.components.formViewer

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceBrandDefault
import presentation.theme.textSecondary


@Composable
fun CheckList(itemList: List<String>, onItemSelected: (List<String>) -> Unit) {
    val selectedItems = remember { mutableStateListOf<String>() }

    LazyColumn(modifier = Modifier.heightIn(0.dp, 500.dp)) {
        items(itemList.size) { index ->
            val item = itemList[index]

            ItemCheckList(

                item=item,
                onItemSelected={

                    val isSelected = selectedItems.contains(it)
                    if (isSelected) {
                        selectedItems.remove(item)
                    } else {
                        selectedItems.add(item)
                    }
                    onItemSelected(selectedItems)

                }

            )

        }
    }




}


@Composable
fun ItemCheckList(item:String,onItemSelected: (selectItem: String) -> Unit){

    val selectedItems = remember { mutableStateListOf<String>() }
    var isSelected = selectedItems.contains(item)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Checkbox(checked = isSelected, colors = CheckboxDefaults.colors(
            checkedColor = surfaceBrandDefault,
            uncheckedColor = strokeDefaultLight
        ), onCheckedChange = { checked_ ->

            if (isSelected) {
                selectedItems.remove(item)
            } else {
                selectedItems.add(item)
            }
            onItemSelected(item)
        })
        Text(
            text = item,
            style = TextStyle(color = textSecondary)
        )
    }

}