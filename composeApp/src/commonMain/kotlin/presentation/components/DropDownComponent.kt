package presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.irancell.nwg.wfm.presentation.theme.*

import presentation.theme.body_large
import presentation.theme.textPlaceHolder
import presentation.theme.textPrimary


@Composable
fun DropDownComponent(
    modifier: Modifier = Modifier,
    suspendReasonText: String = "",
    dropDownText: String = "Select reason*",
    onDropDownClick: () -> Unit = {}
) {

    val suspendReasonTextString = if (suspendReasonText == "") dropDownText else suspendReasonText
    val textColor = if (suspendReasonText == "") textPlaceHolder else textPrimary

    Card(
        modifier,
    ) {


        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(spacing15X),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = suspendReasonTextString,
                style = body_large,
                color = textColor,
                textAlign = TextAlign.Start,
                modifier = modifier.weight(.9f)
            )
            Spacer(modifier = Modifier.padding(horizontal = spacing075X))
//            Image(
//                painter = painterResource(id = R.drawable.ic_chevron_down),
//                contentDescription = "",
//                modifier = modifier.weight(.1f)
//            )
        }
    }
}