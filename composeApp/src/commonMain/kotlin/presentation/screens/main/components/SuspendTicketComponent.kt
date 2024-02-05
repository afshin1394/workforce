package com.irancell.nwg.wfm.presentation.screens.main.components

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
import com.irancell.nwg.wfm.presentation.components.CustomButton
import com.irancell.nwg.wfm.presentation.components.CustomButtonData
import com.irancell.nwg.wfm.presentation.components.CustomEditTextComponent
import presentation.components.TakeImageComponent
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.MR

import presentation.theme.body_large
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceBrandDisabled
import presentation.theme.surfaceDefault
import presentation.theme.textInverse
import presentation.theme.textInverseDisabled
import presentation.theme.textPrimary


@Composable
fun SuspendTicketContentComponent(suspendReason: String = "" ,ticketName: String = "Huawei",onSelectReason : () -> Unit = {},onCompleted : (isComplete : Boolean) -> Unit = {},onCameraClick : () -> Unit = {} ) {

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
        DropDownComponent(suspendReasonText = suspendReason,modifier = Modifier.clickable {
            onSelectReason()

        })
        Spacer(modifier = Modifier.padding(vertical = spacing1X))
        CustomEditTextComponent(updateText = {
           if (it.text.isNotEmpty()){
               onCompleted(true)
           }else{
               onCompleted(false)
           }
        })
        Spacer(modifier = Modifier.padding(vertical = spacing1X))
        Text(text = stringResource(MR.strings.photo_for_ticket_suspend), style = body_large, color = textPrimary)
        Spacer(modifier = Modifier.padding(vertical = spacing1X))
        TakeImageComponent(){
            onCameraClick()
        }
    }


}

@Composable
fun SuspendTicketBottomBarComponent(isEnabled : Boolean = false,onClick : () -> Unit={}) {
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