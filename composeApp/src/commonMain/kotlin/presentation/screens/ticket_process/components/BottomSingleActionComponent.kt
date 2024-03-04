package presentation.screens.ticket_process.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import presentation.components.CustomButton
import presentation.components.CustomButtonData
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import com.irancell.nwg.wfm.presentation.theme.spacing3X
import presentation.model.SingleButtonActionModel
import presentation.theme.surfaceDefault

@Composable
fun bottomSingleActionComponent(singleButtonActionModel: SingleButtonActionModel, onClick : () -> Unit = {}) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(surfaceDefault)
            .padding(vertical = spacing3X, horizontal = spacing2X),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        CustomButton(
            customButtonData = CustomButtonData(
                title = singleButtonActionModel.buttonText,
                textColor = singleButtonActionModel.buttonTextColor,
                singleButtonActionModel.buttonColor
            ),
            modifier = Modifier
                .fillMaxWidth().clickable {
                    onClick()
                }
        )

    }
}