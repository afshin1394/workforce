package presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.irancell.nwg.wfm.presentation.theme.spacing1X
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import domain.models.PhotoDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier

@Composable
fun ImageRowComponent(
    itemsList: List<PhotoDomain>?,
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
            if (itemsList.isNotEmpty()) {
                if (itemsList[0] .origin_uri!= "") {
                    LazyRow() {
                        items(itemsList.size) { index ->
                            val item = if (itemsList[index].edited_uri=="")itemsList[index].origin_uri else itemsList[index].edited_uri
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