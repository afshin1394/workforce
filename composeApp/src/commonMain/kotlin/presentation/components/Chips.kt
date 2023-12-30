package com.irancell.nwg.wfm.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import com.irancell.nwg.wfm.presentation.theme.fontFamily
import com.irancell.nwg.wfm.presentation.theme.radius2XLarge
import com.irancell.nwg.wfm.presentation.theme.spacing05X
import com.irancell.nwg.wfm.presentation.theme.spacing15X

@Composable
fun ChipsView(
    modifier: Modifier = Modifier,
    chipsItem: ChipsItem = ChipsItem(),
    onChipsClick: (chipsItem: ChipsItem) -> Unit = {}
) {
    val border = if (chipsItem.hasBorder) BorderStroke(
        chipsItem.borderWidth,
        chipsItem.chipsBorderColor
    ) else null
    Card(modifier = modifier
        .clickable {
            onChipsClick(chipsItem)
        }
        .background(
            color = chipsItem.chipsColor,
            shape = RoundedCornerShape(chipsItem.chipsRadius)
        ),
//        colors = CardDefaults.cardColors(chipsItem.chipsColor),

        border = border) {
        Text(
            text = chipsItem.text,
            color = chipsItem.textColor,
            style = chipsItem.textStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = spacing05X, horizontal = spacing15X)
        )
    }
}


data class ChipsItem(
    val hasBorder: Boolean = true,
    val chipsColor: Color = Color.Transparent,
    val borderWidth: Dp = 1.dp,
    val chipsBorderColor: Color = Color.Red,
    val chipsRadius: Dp = radius2XLarge,
    val textColor: Color = Color.Red,
    val text: String = "Level: 1",
    val textStyle: TextStyle = TextStyle(
//        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
)


