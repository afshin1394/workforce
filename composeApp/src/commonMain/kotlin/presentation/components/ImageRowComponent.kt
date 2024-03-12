package presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.irancell.nwg.wfm.presentation.theme.spacing1X
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier

@Composable
fun ImageRowComponent(
    itemsList: List<String>?,
    modifier: Modifier = Modifier,
    onCameraClick: () -> Unit = {},
    onImageClick: (index: Int) -> Unit
) {
    Row(modifier) {
        TakeImageComponent() {
            onCameraClick()
        }
        Spacer(modifier = Modifier.padding(vertical = spacing2X))
        itemsList?.let {
            Napier.log(LogLevel.ASSERT,tag = "itemsList",message=itemsList.size.toString())
            if (itemsList.isNotEmpty()) {
                if (itemsList[0] != "") {
                    LazyRow() {
                        items(itemsList.size) { index ->
                            val item = itemsList[index]

                            ImageBoxComponent(item) {
                                onImageClick(index)
                            }
                        }
                    }
                }
            }

        }
    }
}