package presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import presentation.theme.backgroundBackground3
import presentation.theme.textPrimary
import dev.icerock.moko.resources.compose.painterResource
import irancell.nwg.wfm.MR
import presentation.theme.h4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopAppBar(
    title: String,
    onBackClick: () -> Unit = {}
) {
    Column {
        TopAppBar(
            modifier = Modifier.background(color = backgroundBackground3),
            title = {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = title, color = textPrimary, style = h4)
                }
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Image(
                        painterResource(MR.images.arrow_left),
                        contentDescription = "Back",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(24.dp)
                            .height(24.dp)
                    )
                }
            }
        )
    }
}