package presentation.screens.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.*
import presentation.theme.body_large_strong
import presentation.theme.surfaceDefault

@Composable
fun AuthVerificationCodeRow(
    modifier: Modifier = Modifier,
    otpText: String,
    otpCount: Int = 6,
    onOtpTextChange: (String, Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        if (otpText.length > otpCount) {
            throw IllegalArgumentException("Otp text value must not have more than otpCount: $otpCount characters")
        }
    }

    BasicTextField(
        modifier = modifier.fillMaxWidth(),
        value = TextFieldValue(otpText, selection = TextRange(otpText.length)),
        onValueChange = {
            if (it.text.length <= otpCount) {
                onOtpTextChange.invoke(it.text, it.text.length == otpCount)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .widthIn(max = 400.dp),
                    horizontalArrangement = Arrangement.spacedBy(spacing15X)
                ) {
                    repeat(otpCount) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(
                                    color = surfaceDefault,
                                    shape = RoundedCornerShape(radiusLarge)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            val char =
                                if (index >= otpText.length) "" else otpText[index].toString()
                            Text(
                                text = char,
                                style = body_large_strong,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    )
}


@Composable
fun show() {

    var otpValue by remember {
        mutableStateOf("")
    }

    AuthVerificationCodeRow(
        otpText = otpValue,
        onOtpTextChange = { value, _ ->
            otpValue = value
        }
    )
}