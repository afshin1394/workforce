package presentation.screens.main.components.formViewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import domain.models.initialForm.ValueDomain
import irancell.nwg.wfm.FilePicker
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.provideAppContext
import presentation.model.UploadFileModel
import presentation.theme.body_small


@Composable
fun UploadFileComponent (modifier: Modifier,uploadList:List<ValueDomain>, onSelected: (List<ValueDomain>) -> Unit){


    var openFilePic by remember { mutableStateOf(false) }

    val uploadDomain = remember { mutableStateOf(ValueDomain(label = "", value = "")) }
    val uploadDomainListForSend = remember { mutableStateListOf<ValueDomain>() }

    val fileExtensions = listOf("pdf", "docx")
   val fileIcons = listOf(MR.images.pdf,MR.images.docx)


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
                ValueDomain(
                    label = fileName.toString(),
                    value = destinationFile.toString()
                )

            uploadDomainListForSend.add(uploadDomain.value)
        }

        onSelected(uploadDomainListForSend)

    }


    if (openFilePic) {

        FilePicker.launchFilePicker()
        openFilePic = false

    }



    AttachedFileComponent(
        modifier = Modifier.padding(all = 14.dp),

        onAttachClick = {

            openFilePic = true

        })



    if (uploadList.isNotEmpty()){
        LazyColumn (modifier = Modifier.heightIn(0.dp, 500.dp)){
            itemsIndexed(items = uploadList) { index: Int, item: ValueDomain ->


                val fileIconRes = getFileIcon(item.label?:"")

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
                            FilePicker.openFile(item.value , provideAppContext())
                        },
                        text = item.label.toString(),
                        style = body_small,
                        color = Color.DarkGray
                    )
                }
            }
            }
        }

    }

