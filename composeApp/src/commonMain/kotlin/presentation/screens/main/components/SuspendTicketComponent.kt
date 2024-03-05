package presentation.screens.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import presentation.components.CustomButton
import presentation.components.CustomButtonData
import presentation.components.DropDownComponent
import presentation.components.CustomEditTextComponent
import presentation.components.TakeImageComponent
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.compose.stringResource
import domain.models.SuspendTaskDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier

import irancell.nwg.wfm.MR
import presentation.components.ImageRowComponent

import presentation.theme.body_large
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceBrandDisabled
import presentation.theme.surfaceDefault
import presentation.theme.textInverse
import presentation.theme.textInverseDisabled
import presentation.theme.textPrimary


@Composable
fun SuspendTicketContentComponent(
    suspendTaskDomain: SuspendTaskDomain?,
    taskid: String,
    onSelectReason: () -> Unit = {},
    onDescription: (text: String) -> Unit = { s: String -> },
    onCameraClick: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .background(surfaceDefault)
            .padding(horizontal = spacing2X)
            .verticalScroll(
                rememberScrollState()
            )
    ) {
        val taskTitle = suspendTaskDomain?.taskId ?: taskid
        Text(
            text = "${stringResource(MR.strings.why_canceled_ticket)}${taskTitle}",
            style = body_large,
            color = textPrimary
        )
        Spacer(modifier = Modifier.padding(vertical = spacing1X))
        DropDownComponent(
            dropDownText = stringResource(MR.strings.suspend_reason),
            suspendReasonText = suspendTaskDomain?.reason ?: "",
            modifier = Modifier.clickable {
                onSelectReason()

            })
        Spacer(modifier = Modifier.padding(vertical = spacing1X))
        CustomEditTextComponent(defaultText = suspendTaskDomain?.description ?: "", updateText = {
            if (it.text.isNotEmpty()) {
                onDescription(it.text)
            }
        }, editTextHint = stringResource(MR.strings.describe_the_reason))
        Spacer(modifier = Modifier.padding(vertical = spacing1X))
        Text(
            text = stringResource(MR.strings.photo_for_ticket_suspend),
            style = body_large,
            color = textPrimary
        )
        Spacer(modifier = Modifier.padding(vertical = spacing1X))

        ImageRowComponent(suspendTaskDomain?.attachmentsUri?.split(","), onCameraClick = {
            onCameraClick()
        }) {

        }
    }
}

@Composable
fun SuspendTicketBottomBarComponent(isEnabled: Boolean = false, onComplete: () -> Unit = {}) {
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

        if (isEnabled) {
            CustomButton(
                customButtonData = CustomButtonData(
                    stringResource(MR.strings.submit), textColor = textInverse,
                    surfaceBrandDefault
                ), modifier = Modifier.clickable {
                    onComplete()
                }
            )
        } else {
            CustomButton(
                customButtonData = CustomButtonData(
                    stringResource(MR.strings.submit), textColor = textInverseDisabled,
                    surfaceBrandDisabled
                )
            )

        }

    }
}