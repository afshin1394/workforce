package presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.*
import presentation.theme.strokeDefaultLight

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    customButtonData: CustomButtonData,
) {
    Row(
        modifier = modifier
            .background(customButtonData.backgroundColor, shape = RoundedCornerShape(radiusLarge))
            .border(1.dp, color = strokeDefaultLight, shape = RoundedCornerShape(radiusLarge))
            .fillMaxWidth()
            .wrapContentHeight(),


        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,


        ) {
        Row(
            modifier
                .fillMaxWidth()
                .padding(vertical = 13.dp, horizontal = spacing2X),

            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = customButtonData.title,
                color = customButtonData.textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

data class CustomButtonData(val title: String, val textColor: Color, val backgroundColor: Color)

