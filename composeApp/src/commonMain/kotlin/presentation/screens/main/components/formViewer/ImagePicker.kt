package presentation.screens.main.components.formViewer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.models.PhotoDomain
import irancell.nwg.wfm.Camera
import irancell.nwg.wfm.InternalStorage
import irancell.nwg.wfm.provideAppContext
import presentation.components.ImageRowComponent
import presentation.theme.h4
import presentation.theme.textSecondary

@Composable
fun ImagePicker(
    titlePicker:String,
    componentId: String,
    photoDomainList: List<PhotoDomain>,
    onTakePhoto: (resultTakePhoto: String) -> Unit = {},
    onImageClick: (index: Int) -> Unit
) {
    println("recomposeeee ${photoDomainList.toString()}")

    var openCamera by remember { mutableStateOf(false) }

    Camera.onResult {
        onTakePhoto(it.toString())
    }

    if (openCamera) {

        InternalStorage.createWorkItemImages(
            provideAppContext(),
            "Process/Original/",
            componentId
        )

        Camera.launchCamera(
            InternalStorage.getProcessRouteOriginal(
                provideAppContext()
            ) + componentId
        )

        openCamera = false

    }


    Column(
        modifier = Modifier

            .clip(shape = RoundedCornerShape(topEnd = 4.dp, topStart = 4.dp))

            .fillMaxWidth()
            .padding(18.dp)
    ) {

        Text(
            text = titlePicker,
            style = TextStyle(color = textSecondary, fontSize = 14.sp),
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            textAlign = TextAlign.Left
        )

        Spacer(modifier = Modifier.height(12.dp))
        ImageRowComponent(photoDomainList, onCameraClick = {

            openCamera = true

        }) {
            onImageClick(it)
        }
    }

}
