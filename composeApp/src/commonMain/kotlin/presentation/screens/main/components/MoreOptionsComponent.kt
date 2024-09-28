package presentation.screens.main.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.irancell.nwg.wfm.presentation.components.ItemComponent
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.MR
import presentation.model.ItemComponentModel

@Composable
fun MoreOptions(
    onSuspendClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onOpenInMapClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ItemComponent(
            itemComponentModel = ItemComponentModel(text = stringResource(MR.strings.suspend_ticket)),
            modifier = Modifier.clickable {
                onSuspendClick()
            })
        ItemComponent(
            itemComponentModel = ItemComponentModel(text = stringResource(MR.strings.cancel_ticket)),
            modifier = Modifier.clickable {
                onCancelClick()
            })
        ItemComponent(
            itemComponentModel = ItemComponentModel(text = stringResource(MR.strings.openInMap)),
            modifier = Modifier.clickable {
                onOpenInMapClick()
            })
    }

}