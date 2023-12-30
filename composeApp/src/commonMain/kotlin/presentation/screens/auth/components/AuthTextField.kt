package com.irancell.nwg.wfm.presentation.screens.auth.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.spacing15X
import presentation.theme.surfaceDefault
import presentation.theme.textPlaceHolder
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import irancell.nwg.wfm.MR
import presentation.theme.body_large


@Composable
fun showAuthTextField() {
    AuthTextField(
        modifier = Modifier,
        AuthTextFieldItem("Password", imageResource = null,"password", hasPassword = false, false)
    ) {}
}


@Composable
fun AuthTextField(
    modifier: Modifier = Modifier,
    authTextFieldItem: AuthTextFieldItem,
    updateText: (text: TextFieldValue) -> Unit
) {
    var value by remember { mutableStateOf(TextFieldValue("")) }

    var visualTransformation by remember {
        mutableStateOf(if (authTextFieldItem.passwordVisibility) VisualTransformation.None else PasswordVisualTransformation())
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = surfaceDefault, shape = RoundedCornerShape(
                    spacing15X
                )
            ), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start

    ) {
        Row(
            modifier
                .padding(spacing15X)
        ) {

            authTextFieldItem.imageResource?.let {
                Image(
                    painter = painterResource(authTextFieldItem.imageResource),
                    contentDescription = authTextFieldItem.contentDescriptor,
                    modifier = modifier.size(24.dp)
                )

                Spacer(modifier = modifier.width(spacing15X))
            }
            BasicTextField(
                modifier = modifier.weight(8f),
                value = value,
                visualTransformation = visualTransformation,
                textStyle = body_large,
                onValueChange = {
                    value = it
                    updateText(value)

                },
                decorationBox = {
                    Row(
                        Modifier
                            .background(surfaceDefault)
                            .fillMaxWidth()
                    ) {

                        if (value.text.isEmpty()) {
                            Text(
                                authTextFieldItem.hint,
                                color = textPlaceHolder,
                                style = body_large,
                                modifier = Modifier.wrapContentSize()
                            )
                        }
                        it()
                    }
                })

            if (authTextFieldItem.hasPassword) {
                if (visualTransformation == PasswordVisualTransformation()) {
                    Image(
                        painter = painterResource(MR.images.eye_hide),
                        contentDescription = "",
                        modifier.clickable {
                            visualTransformation = VisualTransformation.None
                        })

                } else {
                    Image(
                        painter = painterResource(MR.images.eye_show),
                        contentDescription = "",
                        modifier.clickable {
                            visualTransformation = PasswordVisualTransformation()
                        })

                }
            }

        }
    }

}


data class AuthTextFieldItem(
    val hint: String,
    val imageResource: ImageResource? = null,
    val contentDescriptor: String,
    val hasPassword: Boolean = false,
    val passwordVisibility: Boolean = true
)