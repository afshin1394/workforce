package presentation.screens.main.components.formViewer

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import presentation.theme.surfaceBrandDefault
import presentation.theme.textSecondary

@Composable
fun Radio(itemList: List<String>, selectItem: String, onItemSelected: (selectItem: String) -> Unit) {

    var select by remember { mutableStateOf(selectItem) }
    LazyColumn(modifier = Modifier.heightIn(0.dp, 500.dp)) {
        items(itemList.size) { index ->
            val item = itemList[index]

                ItemRadioList(
                    isSelected = select,
                    item=item,
                    onItemSelected={
                        select=item
                        onItemSelected(it)

                    }

                )

        }
    }

}


@Composable
fun ItemRadioList(isSelected:String,item:String,onItemSelected: (selectItem: String) -> Unit){
    var selectedText by remember { mutableStateOf(item) }
    val isSelected = selectedText==isSelected
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(
            selected = isSelected,
            colors = RadioButtonDefaults.colors(
                selectedColor = surfaceBrandDefault
            ),
            onClick = {
                selectedText = item
                onItemSelected(item)
            }
        )
        Text(
            text = item,
            style = TextStyle(color = textSecondary)
        )
    }

}