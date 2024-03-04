package presentation.screens.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import presentation.components.DropDownComponent
import presentation.components.CustomEditTextComponent
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.MR
import presentation.components.CustomButton
import presentation.components.CustomButtonData

import presentation.theme.body_large
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceBrandDisabled
import presentation.theme.surfaceDefault
import presentation.theme.textInverse
import presentation.theme.textInverseDisabled
import presentation.theme.textPrimary


@Composable
fun CancelTicketComponent(suspendReasonState: String = "", ticketName : String = "Huawei",onSelectReason : () -> Unit = {},onCompleted : (isComplete : Boolean) -> Unit = {}) {
    Column(
        modifier = Modifier
            .background(surfaceDefault)
            .padding(horizontal = spacing2X)
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        Text(
            text = "${stringResource(MR.strings.why_canceled_ticket)}$ticketName?",
            style = body_large,
            color = textPrimary
        )
        Spacer(modifier = Modifier.padding(vertical = spacing1X))
        DropDownComponent(dropDownText = stringResource(MR.strings.cancel_reason) ,suspendReasonText = suspendReasonState, modifier = Modifier.clickable {
            onSelectReason()

        })
        Spacer(modifier = Modifier.padding(vertical = spacing1X))
        CustomEditTextComponent(editTextHint = stringResource(MR.strings.describe_the_reason), defaultText = "",updateText = {
            if (it.text.isNotEmpty()) {
                onCompleted(true)
            } else {
                onCompleted(false)
            }
        })

    }
}

@Composable
fun CancelTicketBottomBarComponent(isEnabled : Boolean = false,onClick : () -> Unit={}) {
    Row(
        modifier = Modifier
            .background(surfaceDefault)
            .padding(
                top = 32.dp,
                bottom = 24.dp,
                start = spacing2X,
                end = spacing2X
            )
    ) {

        if (isEnabled){
            CustomButton(
                customButtonData = CustomButtonData(
                    stringResource(MR.strings.submit), textColor = textInverse,
                    surfaceBrandDefault
                ), modifier = Modifier.clickable {
                    onClick()
                }
            )
        }else{
            CustomButton(
                customButtonData = CustomButtonData(
                    stringResource(MR.strings.submit), textColor = textInverseDisabled,
                    surfaceBrandDisabled
                )
            )

        }

    }
}