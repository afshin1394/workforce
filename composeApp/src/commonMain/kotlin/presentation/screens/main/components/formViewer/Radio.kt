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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import dev.icerock.moko.resources.compose.painterResource
import domain.models.initialForm.ValueDomain
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.getSharedPref
import presentation.theme.h4
import presentation.theme.h5
import presentation.theme.surfaceBrandDefault
import presentation.theme.textSecondary
import utils.Language

@Composable
fun Radio( title:String, itemList: List<ValueDomain>, selectItem: String, onItemSelected: (selectItem: String) -> Unit) {

   var select by remember { mutableStateOf(selectItem) }
 // var select =selectItem

    println("recomposeeee ${"Radio"}")


    Column(Modifier.padding(16.dp)) {

        Text(
            text = title,
            style = TextStyle(color = textSecondary, fontSize = 14.sp),
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),

            )

        Spacer(modifier = Modifier.padding(top = spacing15X))
        LazyColumn(modifier = Modifier.heightIn(0.dp, 500.dp)) {
            items(itemList.size) { index ->
                val item = itemList[index]

                ItemRadioList(
                    isSelected = select,
                    item = item.label?:"",
                    onItemSelected = {
                        select = item.label?:""
                        onItemSelected(it)

                    }

                )

            }
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