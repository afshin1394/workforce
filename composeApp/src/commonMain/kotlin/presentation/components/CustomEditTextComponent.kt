package presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.material.Text

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.*
import presentation.theme.body_large
import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceDefault
import presentation.theme.textPlaceHolder


@Composable
fun CustomEditTextComponent(
    modifier: Modifier = Modifier,
    defaultText : String,
    editTextHint: String = "Describe the reason",
    disabled : Boolean = false ,
    updateText: (value: TextFieldValue) -> Unit = {}
) {
    var value by remember { mutableStateOf(TextFieldValue(defaultText)) }
    Card(
        modifier = Modifier.background(
            color = surfaceDefault,
            shape = RoundedCornerShape(spacing15X)
        ),
        border = BorderStroke(1.dp, strokeDefaultLight)
    ) {
        Row(
            modifier = modifier
                .padding(vertical = 10.dp, horizontal = spacing15X),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            BasicTextField(
                modifier = modifier.height(304.dp),
                value = value,
                textStyle = body_large,
                onValueChange = {
                    if(!disabled) {
                        value = it
                        updateText(value)
                    }
                },
                decorationBox = {
                    Row(
                        Modifier
                            .background(surfaceDefault)
                            .fillMaxWidth()
                    ) {

                        if (value.text.isEmpty()) {
                            Text(
                                editTextHint,
                                color = textPlaceHolder,
                                style = body_large,
                                modifier = Modifier.wrapContentSize()
                            )
                        }
                        it()
                    }
                })
        }
    }
}