package presentation.screens.main.components.formViewer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import domain.models.PhotoDomain
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ProcessLogicDomain
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.logging.LogLevel
import irancell.nwg.wfm.Camera
import irancell.nwg.wfm.InternalStorage
import irancell.nwg.wfm.provideAppContext
import presentation.components.ImageRowComponent
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceBrandDisabled
import presentation.theme.textInverseDisabled
import presentation.theme.textSecondary
@Composable
fun ImagePicker(
    item : ComponentDomain,
    componentId: String,
    errorMessage: ResourceFormattedStringDesc,
    photoDomainList: List<PhotoDomain>,
    onTakePhoto: (resultTakePhoto: String) -> Unit,
    onCameraClick : (item : ComponentDomain) -> Unit,
    onImageClick: (index: Int) -> Unit
) {


    val disableLogic = item.processLogicDomain.disabled
    val hideLogic = item.processLogicDomain.shouldHide
    val readOnlyLogic = item.processLogicDomain.readOnly || item.readOnly
    val requiredLogic = item.processLogicDomain.required
    val validateLogic = item.processLogicDomain.validate
    val errorMessageValidateLogic =item.processLogicDomain.errorMessage

    val backgroundColor = if (errorMessage.localized() != "" || validateLogic) {
        Color.Red
    } else if (readOnlyLogic || disableLogic) {
        surfaceBrandDisabled
    } else {
        surfaceBrandDefault
    }

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
            ) + componentId,item.key!!
        )

        openCamera = false

    }

    if(!hideLogic) {
        Column(
            modifier = Modifier

                .clip(shape = RoundedCornerShape(topEnd = 4.dp, topStart = 4.dp))
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            val styledString = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = if (disableLogic || readOnlyLogic) textInverseDisabled else textSecondary,
                        fontSize = 14.sp
                    )
                ) {
                    append(item.label?:"")
                }
                if (requiredLogic) {
                    withStyle(style = SpanStyle(color = Color.Red, fontSize = 18.sp)) {
                        append(" *")
                    }
                }
            }

            Text(
                text = styledString,
                style = TextStyle(color = textSecondary, fontSize = 14.sp),
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            )

            Spacer(modifier = Modifier.height(12.dp))
            ImageRowComponent(backgroundColor,photoDomainList, onCameraClick = {


                if(!(disableLogic || readOnlyLogic)) {
                    openCamera = true
                    onCameraClick(item)
                }

            }) {
                onImageClick(it)
            }
            Spacer(modifier = Modifier.height(6.dp))
            errorMessage.localized().let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier
                )
            }

            if (validateLogic) {
                errorMessageValidateLogic?.let {
                    Text(
                        text = errorMessageValidateLogic.localized(),
                        color = Color.Red,
                        style = TextStyle(fontSize = 12.sp),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }

}
