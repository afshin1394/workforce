package presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.irancell.nwg.wfm.presentation.theme.spacing1X

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
        Spacer(modifier = Modifier.padding(vertical = spacing1X))
        itemsList?.let {
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