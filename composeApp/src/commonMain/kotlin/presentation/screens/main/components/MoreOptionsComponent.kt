package presentation.screens.main.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.irancell.nwg.wfm.presentation.components.ItemComponent
import presentation.model.ItemComponentModel

@Composable
fun MoreOptions(onSuspendClick : () -> Unit = {},onCancelClick : () -> Unit = {}){
    Column(modifier = Modifier.fillMaxWidth()) {
        ItemComponent(itemComponentModel =  ItemComponentModel( text = "Suspend ticket"),modifier = Modifier.clickable {
            onSuspendClick()
        })
        ItemComponent(itemComponentModel = ItemComponentModel(text = "Cancel ticket"),modifier = Modifier.clickable {
            onCancelClick()
        })
    }

}