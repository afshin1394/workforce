package presentation.screens.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.spacing05X
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import dev.icerock.moko.resources.compose.stringResource
import domain.models.PhotoDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.RotateImageUri
import kotlinx.coroutines.delay
import presentation.screens.main.components.formViewer.draw.ControlsBarPreview
import presentation.theme.surfaceDefault



@Composable
fun PhotoPreviewComponent(

    photoDomainList: MutableList<PhotoDomain>,
    positionPhotoSelected: Int,
    onEditPhotoClick: (position: Int) -> Unit,
    onDeletePhoto: (position: Int) -> Unit = {},
    onSaveChangeAngle:(MutableList<PhotoDomain>)->Unit={}

) {

    var selectedItemImage by remember { mutableStateOf(positionPhotoSelected) }
    val imageSelected = remember { mutableStateOf("") }
    val angle = remember { mutableStateOf(photoDomainList[selectedItemImage].angle) }
    val isRotate = remember { mutableStateOf(false) }
    var firstTimeInitPager by remember { mutableStateOf(true) }
    var photoDomainList = photoDomainList


    val scope = rememberCoroutineScope()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceDefault)
            .padding(horizontal = spacing05X)
            .verticalScroll(
                rememberScrollState()
            ), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LaunchedEffect(Unit) {
            delay(300)
            firstTimeInitPager = false

        }



        Column(
            modifier = Modifier.fillMaxSize(),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Pager(
                items = photoDomainList,
                firstTimeInit = firstTimeInitPager,
                modifier = Modifier
                    .weight(0.8F)
                    .clipToBounds()
                    .fillMaxWidth(),

                initialIndex = selectedItemImage,
                itemSpacing = 30.dp,
                angle = angle.value,
                onItemSelectedPosition = {
                    firstTimeInitPager = false
                    selectedItemImage = it
                },
                onItemSelect = {
                    imageSelected.value = it.origin_uri
                    Napier.log(
                        LogLevel.ASSERT,
                        "indexPic",
                        message = positionPhotoSelected.toString()
                    )
                    angle.value = it.angle.toFloat()
                },
                contentFactory = { item ->

                    CoilImage(
                        modifier =
                        if(isRotate.value) Modifier
                            .fillMaxSize()
                            .aspectRatio(0.8f)
                            .rotate(item.angle) else     Modifier
                            .fillMaxSize()
                            .aspectRatio(0.8f)


                        ,
                        imageModel = { if (item.edited_uri == "") item.origin_uri else item.edited_uri },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Fit,
                            alignment = Alignment.Center
                        )
                    )

                }
            )
        }


        Spacer(modifier = Modifier.weight(1f))
        val of = stringResource(MR.strings.Of)
        Text(
            text = "${selectedItemImage + 1} $of ${photoDomainList.size}",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        ControlsBarPreview(


            onRotateClick = {
                    firstTimeInitPager = false
                    isRotate.value = true
                    angle.value = (angle.value + 90)

                    photoDomainList.getOrNull(selectedItemImage)?.let {
                        photoDomainList[selectedItemImage] =
                            it.copy(
                                angle = angle.value,
                            )
                    }
            },


            onEditClick = {
                firstTimeInitPager = false
                onEditPhotoClick(selectedItemImage)

            }, onDeletePhoto = {
                firstTimeInitPager = false
                onDeletePhoto(selectedItemImage)


            }, onSaveClick = {
                firstTimeInitPager = false


                photoDomainList.getOrNull(selectedItemImage)?.let {

                    photoDomainList[selectedItemImage] = it.copy(
                        origin_uri =  RotateImageUri(it.origin_uri, it.component_key, if (it.isFirstClick) it.angle+90 else it.angle)
                        , isFirstClick = false
                    )
                }


                onSaveChangeAngle(photoDomainList)


            },
            isRotate = isRotate
        )


    }


}




