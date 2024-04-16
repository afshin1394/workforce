package com.irancell.nwg.wfm.presentation.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

import presentation.model.BottomSheetDoubleActionModel
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.compose.painterResource
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import kotlinx.coroutines.launch
import presentation.components.CustomButton
import presentation.components.CustomButtonData
import presentation.theme.h4
import presentation.theme.surfaceDefault


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomBottomSheet(
    bottomSheetState: BottomSheetState,
    title: String = "",
    hasHeader: Boolean = true,
    bottomBar: @Composable () -> Unit = { },
    content: @Composable () -> Unit = {},
    onClose: () -> Unit = {}
) {
    Column() {
        Column(modifier = Modifier.weight(1f, false)) {
            if (hasHeader)
                BottomSheetHead(bottomSheetState, title, onClose = {
                    onClose()
                })
            content()
        }
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.Center) {
            bottomBar()
        }
    }


}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetHead(
    bottomSheetState: BottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Expanded),
    title: String = "Filter",
    onClose: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(spacing2X)
    ) {


        Image(
            painter = painterResource(MR.images.close),
            contentDescription = "ic_close",
            modifier = Modifier
                .clickable {
                    Napier.i("bottomSheetClick")
                    scope.launch {
                        bottomSheetState.collapse()
                    }
                    onClose()
                }.padding(spacing1X)
        )

        androidx.compose.material.Text(
            text = title,
            style = h4,
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            textAlign = TextAlign.Center
        )

    }


}

@Composable
fun bottomSheetDoubleActionBottomBar(
    bottomSheetDoubleActionModel: BottomSheetDoubleActionModel,
    onFirstButtonClick: () -> Unit = {},
    onSecondButtonClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(surfaceDefault)
            .padding(vertical = spacing3X, horizontal = spacing2X),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        CustomButton(
            customButtonData = CustomButtonData(
                title = bottomSheetDoubleActionModel.firstButtonText,
                textColor = bottomSheetDoubleActionModel.firstButtonTextColor,
                bottomSheetDoubleActionModel.firstButtonColor
            ),
            modifier = Modifier
                .weight(1f).clickable {
                    onFirstButtonClick()
                }
        )
        Spacer(modifier = Modifier.padding(horizontal = spacing2X))
        CustomButton(
            customButtonData = CustomButtonData(
                title = bottomSheetDoubleActionModel.secondButtonText,
                textColor = bottomSheetDoubleActionModel.secondColorTextColor,
                bottomSheetDoubleActionModel.secondButtonColor
            ), modifier = Modifier
                .weight(1f).clickable {
                    onSecondButtonClick()
                }
        )

    }
}



@Composable
fun bottomSheetSingleActionBottomBar(
    bottomSheetDoubleActionModel: BottomSheetDoubleActionModel,
    onFirstButtonClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(surfaceDefault)
            .padding(vertical = spacing3X, horizontal = spacing2X),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        CustomButton(
            customButtonData = CustomButtonData(
                title = bottomSheetDoubleActionModel.firstButtonText,
                textColor = bottomSheetDoubleActionModel.firstButtonTextColor,
                bottomSheetDoubleActionModel.firstButtonColor
            ),
            modifier = Modifier
                .weight(1f).clickable {
                    onFirstButtonClick()
                }
        )


    }
}
