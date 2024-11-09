package presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CardDefaults

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.compose.painterResource
import irancell.nwg.wfm.MR
import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceDefault
import presentation.theme.surfaceInputDefault
import utils.debounceClick

@Composable
fun CustomSearchBar(
    modifier: Modifier = Modifier,
    textFieldState: String = "",
    updatedText: (text: String) -> Unit = {},
    hasFilter: Boolean = true,
    onFilterClick: () -> Unit = {}
) {
    androidx.compose.material3.Card(
        modifier = modifier
            .background(
                color = surfaceDefault,
                shape = RoundedCornerShape(spacing15X)
            ),
        colors = CardDefaults.cardColors(surfaceDefault),
        border = BorderStroke(1.dp, strokeDefaultLight)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier

                .background(
                    color = surfaceInputDefault,
                    shape = RoundedCornerShape(size = spacing15X)
                )
                .wrapContentHeight()
                .padding(vertical = spacing1X, horizontal = spacing05X)
        ) {
            Image(
                painterResource(MR.images.search),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
            )
            Spacer(modifier = Modifier.padding(end = 6.dp))
            Column(
                modifier = Modifier
                    .weight(12f)

                    .height(22.dp), horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                BasicTextField(
                    modifier = modifier.fillMaxWidth(),
                    value = textFieldState,
                    onValueChange = {
                        updatedText(it)
                    })

            }
            Spacer(modifier = Modifier.padding(end = 6.dp))
            if (hasFilter) {
                val onClickOnButton =
                    debounceClick(debounceTime = 1000L, onClick = onFilterClick)
                Image(
                    painterResource(MR.images.filter2),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp)
                        .clickable { onClickOnButton() }
                )
            }
        }
    }
}

