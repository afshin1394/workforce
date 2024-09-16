package presentation.screens.ticket_process.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import presentation.components.CustomButton
import presentation.components.CustomButtonData
import presentation.model.BottomSheetActionModel
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import com.irancell.nwg.wfm.presentation.theme.spacing3X
import presentation.theme.surfaceDefault

@Composable
fun bottomDoubleActionSheet(bottomSheetActionModel: BottomSheetActionModel, onFirstButtonClick : () -> Unit = {}, onSecondButtonClick : () -> Unit = {}) {

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
                title = bottomSheetActionModel.firstButtonText,
                textColor = bottomSheetActionModel.firstButtonTextColor,
                bottomSheetActionModel.firstButtonColor
            ),
            modifier = Modifier
                .weight(1f).clickable {
                    onFirstButtonClick()
                }
        )
        Spacer(modifier = Modifier.padding(horizontal = spacing2X))
        CustomButton(
            customButtonData = CustomButtonData(
                title = bottomSheetActionModel.secondButtonText,
                textColor = bottomSheetActionModel.secondColorTextColor,
                bottomSheetActionModel.secondButtonColor
            ), modifier = Modifier
                .weight(1f).clickable {
                    onSecondButtonClick()
                }
        )
    }
}