package presentation.screens.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.irancell.nwg.wfm.presentation.theme.spacing05X
import domain.models.PhotoDomain
import irancell.nwg.wfm.ParseUri
import irancell.nwg.wfm.UriToImageBitmap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import presentation.screens.main.components.formViewer.draw.ControlsBarPreview

import presentation.theme.surfaceDefault


@Composable
fun PhotoPreviewComponent(

     photoDomainList:MutableList<PhotoDomain>,
     positionPhotoSelected:Int,
     onEditPhotoClick:(position:Int)->Unit,
     onDeletePhoto:(position:Int)->Unit={},
     onSaveChangeAngle:(MutableList<PhotoDomain>)->Unit={}

) {

    var selectedItemImage by remember { mutableStateOf(positionPhotoSelected) }
    val imageSelected = remember { mutableStateOf("") }
    val angle = remember { mutableStateOf(0f) }
    val isRotate = remember { mutableStateOf(false) }
    var firstTimeInitPager by remember { mutableStateOf(true) }
    var photoDomainList = photoDomainList




    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceDefault)
            .padding(horizontal = spacing05X)
            .verticalScroll(
                rememberScrollState()
            )
            ,verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {




       println("Photoselected${selectedItemImage}")
        LaunchedEffect(Unit){
//            delay(100)
            firstTimeInitPager=false

        }



            Pager(
                items = photoDomainList,
                firstTimeInit = firstTimeInitPager,
                modifier = Modifier
                    .width(500.dp)
                    .height(256.dp),
                itemFraction = .75f,
                initialIndex = selectedItemImage,
                overshootFraction = .75f,
                angle = angle.value,
                itemSpacing = 50.dp,
                onItemSelectedPosition = {
                    firstTimeInitPager=false
                    selectedItemImage = it

                    println("onItemSelectedPosition${it}")
                },
                onItemSelect = {

                    imageSelected.value = it.origin_uri
                    angle.value = it.angle.toFloat()

                },
                contentFactory = { item ->
                    Box(
                        modifier = Modifier
                            .background(Color.Transparent).fillMaxSize().align(alignment = Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {



                        Image(
                            bitmap = (UriToImageBitmap(
                                ParseUri(  if(item.edited_uri=="")item.origin_uri else item.edited_uri),
                                item.angle.toFloat()


                            ) as ImageBitmap),
                            contentDescription = null,
                            modifier = Modifier
                                //.rotate(item.angle.toFloat())
                                .background(Color.Transparent)
                                .clip(shape = RoundedCornerShape(2.dp)),
                            contentScale = ContentScale.FillBounds
                        )

                    }
                },
            )

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${selectedItemImage + 1} of ${photoDomainList.size}",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            ControlsBarPreview(


                onRotateClick = {
                    firstTimeInitPager=false
                    isRotate.value = true
                    angle.value = (angle.value + 90)

                    photoDomainList.getOrNull(selectedItemImage)?.let {

                        photoDomainList[selectedItemImage] =
                            it.copy(angle = angle.value.toString())

                    }
                },


                onEditClick = {
                    firstTimeInitPager=false
                    onEditPhotoClick(selectedItemImage)

                }, onDeletePhoto = {
                    firstTimeInitPager=false
                    onDeletePhoto(selectedItemImage)

                }, onSaveClick = {
                    firstTimeInitPager=false
                    onSaveChangeAngle(photoDomainList)

                },
                isRotate = isRotate
            )








    }


}