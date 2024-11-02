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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irancell.nwg.wfm.presentation.theme.spacing05X
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ProcessLogicDomain
import domain.models.form_struct.ValueDomain
import irancell.nwg.wfm.FilePicker
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.provideAppContext
import presentation.theme.body_small
import presentation.theme.strokeDefaultLight
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceBrandDisabled
import presentation.theme.textInverseDisabled
import presentation.theme.textSecondary


@Composable
fun UploadFileComponent(
    disable : Boolean,
    readOnly : Boolean,
    processLogicDomain: ProcessLogicDomain,
    index : Int,
    item : ComponentDomain,
    label : String,
    uploadList: List<ValueDomain>?,
    onClickUpload : (index : Int) -> Unit,
    onChooseFileFromDevice: (MutableList<ValueDomain>) -> Unit,
    onRemoveFile: (ValueDomain) -> Unit
) {

    val hideLogic = processLogicDomain.shouldHide
    val disableLogic = processLogicDomain.disabled || disable
    val readOnlyLogic = processLogicDomain.readOnly || readOnly
    val requiredLogic = processLogicDomain.required
    val validateLogic = processLogicDomain.validate
    val errorMessageLogic = processLogicDomain.errorMessage
    val hasInitialMessageLogic = processLogicDomain.hasInitialMessage

    val backgroundColor =  if (validateLogic || (requiredLogic && !hasInitialMessageLogic)) {
        Color.Red
    } else if (readOnlyLogic || disableLogic) {
        surfaceBrandDisabled
    } else {
        surfaceBrandDefault
    }

    var openFilePic by remember { mutableStateOf(false) }


    var uploadDomain = ValueDomain(label = "", value = "")
    val uploadDomainListForSend = arrayListOf<ValueDomain>()

    val fileExtensions = listOf("pdf", "docx", "png", "jpg")
    val fileIcons = listOf(MR.images.pdf, MR.images.docx, MR.images.icon_png, MR.images.icon_jpg)

    fun getFileIcon(fileName: String): ImageResource {
        val fileExtension = fileName.substringAfterLast(".")
        val index = fileExtensions.indexOf(fileExtension.toLowerCase())
        return if (index != -1) fileIcons[index] else MR.images.about
    }
    item.key?.let {
        FilePicker.onResult(it) { files ->
            files.forEach { pair ->
                val fileName = pair.first
                val destinationFile = pair.second
                uploadDomain =
                    ValueDomain(label = fileName.toString(), value = destinationFile.toString())

                uploadDomainListForSend.add(uploadDomain)
            }

            onChooseFileFromDevice(uploadDomainListForSend)

        }
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
            append(label)
        }
        if (requiredLogic || validateLogic) {
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
                        onClickUpload(index)
                    }

                })

            if (uploadList?.isNotEmpty() == true) {
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
                            Image(
                                painter = painterResource(MR.images.close),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        // Delete item from the list


                                        onRemoveFile(item)
                                    },
                            )
                        }
                    }
                }
            }




            if (validateLogic || (requiredLogic && !hasInitialMessageLogic)) {
                errorMessageLogic?.let {
                    Text(
                        text = errorMessageLogic.localized(),
                        color = Color.Red,
                        style = TextStyle(fontSize = 12.sp),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}