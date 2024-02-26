package presentation.screens.main.components.formViewer

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import presentation.theme.strokeDefaultLight
import presentation.theme.textSecondary


@Composable
fun Editable(
    type: TypeEditable,
    placeholder:String,
    imeAction: ImeAction,
    leadingIcon: ImageResource?=null,
    trailingIcon: ImageResource?=null,
    keyboardType : KeyboardType,
    readOnly:Boolean,
    maxLines:Int,
   onValueChange: (value: String) -> Unit
) {

    var valueChange by remember { mutableStateOf("") }


    Column(Modifier.padding(16.dp)) {
        TextField(
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                keyboardType = keyboardType,
                imeAction = imeAction
            ),

            maxLines =maxLines ,
            value = valueChange,
            onValueChange = {
                valueChange=it
                onValueChange(valueChange) },
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = strokeDefaultLight,
                    shape = RoundedCornerShape(15.dp)
                ),
            readOnly = readOnly,
            shape = RoundedCornerShape(8.dp),
            textStyle = TextStyle(color = textSecondary),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White
            ),

            trailingIcon = {

                if (trailingIcon!=null){
                    Icon(

                        painter = painterResource(trailingIcon),
                        "",
                        Modifier.width(28.dp).height(28.dp).padding(end = 8.dp)
                            .clickable {},
                        tint = textSecondary
                    )

                }




            },
            leadingIcon = {

                if (leadingIcon!=null){
                    Icon(
                        painter = painterResource(leadingIcon),
                        "",
                        Modifier.width(28.dp).height(28.dp).padding(end = 8.dp)
                            .clickable {},
                        tint = textSecondary
                    )

                }




            },
            placeholder = { Text(text = placeholder, style = TextStyle(color = textSecondary)) }


        )

    }


}


enum class TypeEditable{
    PHONE,
    EMAIL,
    SHORT_TEXT,
    LONG_TEXT,
    LAT,
    LONG,
    NUMBER


}