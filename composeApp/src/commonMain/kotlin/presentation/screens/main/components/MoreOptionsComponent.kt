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
import utils.debounceClick
import utils.defaultDebounceClick

@Composable
fun MoreOptions(
    onTicketInfoClick: () -> Unit = {},
    onSuspendClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onOpenInMapClick: () -> Unit = {}
) {
    val onTicketInfoDebounce =
        debounceClick(debounceTime = defaultDebounceClick, onClick = onTicketInfoClick)
    val onSuspendDebounce =
        debounceClick(debounceTime = defaultDebounceClick, onClick = onSuspendClick)
    val onCancelDebounce =
        debounceClick(debounceTime = defaultDebounceClick, onClick = onCancelClick)
    val onOpenInMapDebounce =
        debounceClick(debounceTime = defaultDebounceClick, onClick = onOpenInMapClick)

    Column(modifier = Modifier.fillMaxWidth()) {
        ItemComponent(
            itemComponentModel = ItemComponentModel(text = stringResource(MR.strings.ticket_info)),
            modifier = Modifier.clickable {
                onTicketInfoDebounce()
            })
        ItemComponent(
            itemComponentModel = ItemComponentModel(text = stringResource(MR.strings.suspend_ticket)),
            modifier = Modifier.clickable {
                onSuspendDebounce()
            })
        ItemComponent(
            itemComponentModel = ItemComponentModel(text = stringResource(MR.strings.cancel_ticket)),
            modifier = Modifier.clickable {
                onCancelDebounce()
            })
        ItemComponent(
            itemComponentModel = ItemComponentModel(text = stringResource(MR.strings.openInMap)),
            modifier = Modifier.clickable {
                onOpenInMapDebounce()
            })
    }

}