package com.irancell.nwg.wfm.presentation.screens.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.*

import presentation.theme.body_large
import presentation.theme.surfaceBrandDefault
import presentation.theme.textInverse


@Composable
fun showAuthButton() {
    AuthButton( authButtonItem = AuthButtonItem("Verify")) {

    }
}

@Composable
fun AuthButton(modifier: Modifier = Modifier, authButtonItem: AuthButtonItem, onClick: () -> Unit) {

    Row(
        modifier = modifier
            .background(surfaceBrandDefault, shape = RoundedCornerShape(radiusLarge))
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                onClick()
            },

        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,


        ) {
        Row(modifier.padding(vertical = 13.dp, horizontal = spacing2X)) {
            Text(text = authButtonItem.text, style = body_large, color = textInverse)
        }
    }


}


data class AuthButtonItem(val text : String)
