package presentation.screens.main.components.formViewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irancell.nwg.wfm.presentation.theme.spacing05X
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import domain.models.initialForm.ProcessLogicDomain
import domain.models.initialForm.ValueDomain
import irancell.nwg.wfm.FilePicker
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.provideAppContext
import presentation.model.UploadFileModel
import presentation.theme.body_small
import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceBrandDark
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceBrandDisabled
import presentation.theme.surfaceDefault
import presentation.theme.textInverseDisabled
import presentation.theme.textSecondary


@Composable
fun UploadFileComponent(
    processLogicDomain: ProcessLogicDomain,
    titlePicker: String,
    errorMessage: ResourceFormattedStringDesc,
    modifier: Modifier,
    uploadList: List<ValueDomain>,
    onSelected: (MutableList<ValueDomain>) -> Unit
) {

    val disableLogic = processLogicDomain.disabled
    val hideLogic = processLogicDomain.shouldHide
    val readOnlyLogic = processLogicDomain.readOnly
    val requiredLogic = processLogicDomain.required
    val validateLogic = processLogicDomain.validate
    val errorMessageValidateLogic = processLogicDomain.errorMessage

    val backgroundColor = if (errorMessage.localized() != "" || validateLogic) {
        Color.Red
    } else if (readOnlyLogic || disableLogic) {
        surfaceBrandDisabled
    } else {
        surfaceBrandDefault
    }

    var openFilePic by remember { mutableStateOf(false) }


    val uploadDomain = remember { mutableStateOf(ValueDomain(label = "", value = "")) }
    val uploadDomainListForSend = remember { mutableStateListOf<ValueDomain>() }

    val fileExtensions = listOf("pdf", "docx", "png", "jpg")
    val fileIcons = listOf(MR.images.pdf, MR.images.docx, MR.images.icon_png, MR.images.icon_jpg)

    println("recomposeeee ${"UploadFile"}")


    fun getFileIcon(fileName: String): ImageResource {
        val fileExtension = fileName.substringAfterLast(".")
        val index = fileExtensions.indexOf(fileExtension.toLowerCase())
        return if (index != -1) fileIcons[index] else MR.images.about
    }

    FilePicker.onResult { files ->
        files.forEach { pair ->
            val fileName = pair.first
            val destinationFile = pair.second
            uploadDomain.value =
                ValueDomain(label = fileName.toString(), value = destinationFile.toString())

            uploadDomainListForSend.add(uploadDomain.value)
        }

        onSelected(uploadDomainListForSend)

    }


    if (openFilePic) {

        FilePicker.launchFilePicker()
        openFilePic = false

    }

    val styledString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = if (disableLogic || readOnlyLogic) textInverseDisabled else textSecondary,
                fontSize = 14.sp
            )
        ) {
            append(titlePicker)
        }
        if (requiredLogic) {
            withStyle(style = SpanStyle(color = Color.Red, fontSize = 18.sp)) {
                append(" *")
            }
        }
    }


    if (!hideLogic) {
        Column(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(topEnd = 4.dp, topStart = 4.dp))
                .fillMaxWidth()

        ) {

            Text(
                text = styledString,
                style = TextStyle(
                    color = if (disableLogic || readOnlyLogic) textInverseDisabled else textSecondary,
                    fontSize = 14.sp
                ),
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                    .padding(end = 18.dp, start = 18.dp),
            )
            Spacer(modifier = Modifier.height(spacing05X))

            AttachedFileComponent(
                modifier = Modifier.padding(all = 14.dp),
                backgroundColor = backgroundColor,
                onAttachClick = {
                    if (!(disableLogic || readOnlyLogic)) {
                        openFilePic = true
                    }

                })

            if (uploadList.isNotEmpty()) {
                LazyColumn(modifier = Modifier.heightIn(0.dp, 500.dp)) {
                    itemsIndexed(items = uploadList) { index: Int, item: ValueDomain ->


                        val fileIconRes = getFileIcon(item.label ?: "")

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .sizeIn(
                                    minWidth = 112.dp,
                                    maxWidth = 280.dp,
                                    minHeight = MenuTokens.ListItemContainerHeight
                                )
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Image(
                                painter = painterResource(fileIconRes),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                            Text(
                                modifier = Modifier.padding(8.dp).clickable {
                                    item.value?.let { FilePicker.openFile(it, provideAppContext()) }
                                },
                                text = item.label.toString().substringAfterLast("/"),
                                style = body_small,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }



            errorMessage.localized().let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(end = 14.dp, start = 14.dp)
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

