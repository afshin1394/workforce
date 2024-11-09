package presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import kotlinx.coroutines.launch
import presentation.model.BottomSheetActionModel
import presentation.model.SingleButtonActionModel
import presentation.theme.h4
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceDefault
import utils.BottomSheetTypes
import utils.ButtonState
import utils.debounceClick

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomBottomSheet(
    bottomSheetState: BottomSheetState,
    title: String = "",
    showClose: Boolean = true,
    type: String = BottomSheetTypes.Default,
    hasHeader: Boolean = true,
    bottomBar: @Composable () -> Unit = { },
    content: @Composable () -> Unit = {},
    onClose: () -> Unit = {}
) {
    Column {
        Column(modifier = Modifier.weight(1f, false)) {
            if (hasHeader)
                BottomSheetHead(bottomSheetState, title, showClose, type, onClose = {
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
    showClose: Boolean = true,
    type: String = BottomSheetTypes.Default,
    onClose: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(spacing2X),
    ) {
        if (showClose) {
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
        }

        if (type == BottomSheetTypes.Default) {
            Text(
                text = title,
                style = h4,
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                textAlign = TextAlign.Center
            )

        } else if (type == BottomSheetTypes.Success) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(spacing2X),
                contentAlignment = Alignment.Center
            ) {

                var scaleState by remember { mutableStateOf(1f) }

                val scale by animateFloatAsState(
                    targetValue = scaleState,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 800,
                            easing = FastOutSlowInEasing
                        ),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                LaunchedEffect(Unit) {
                    scaleState = 1.2f
                }

                Image(
                    painter = painterResource(MR.images.check_square_green),
                    contentDescription = "ic_success",
                    modifier = Modifier
                        .scale(scale)
                        .size(70.dp)
                        .padding(spacing1X)
                )
            }
        }
    }
}

@Composable
fun bottomSheetDoubleActionBottomBar(
    bottomSheetDoubleActionModel: BottomSheetActionModel,
    onFirstButtonClick: () -> Unit = {},
    onSecondButtonClick: () -> Unit = {}
) {
    val firstButtonDebouncedClick =
        debounceClick(debounceTime = 1000L, onClick = onFirstButtonClick)
    val secondButtonDebouncedClick =
        debounceClick(debounceTime = 1000L, onClick = onSecondButtonClick)

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
                    firstButtonDebouncedClick()
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
                    secondButtonDebouncedClick()
                }
        )
    }
}

@Composable
fun bottomSingleActionComponent(
    singleButtonActionModel: SingleButtonActionModel,
    onClick: () -> Unit = {}
) {
    val onClickOnButton =
        debounceClick(debounceTime = 1000L, onClick = onClick)
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
                title = singleButtonActionModel.buttonText,
                textColor = singleButtonActionModel.buttonTextColor,
                singleButtonActionModel.buttonColor
            ),
            modifier = Modifier
                .fillMaxWidth().clickable { onClickOnButton() }
        )
    }
}

@Composable
fun bottomSingleActionComponentWithLoading(
    buttonState: ButtonState,
    singleButtonActionModel: SingleButtonActionModel,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(surfaceDefault)
            .padding(vertical = spacing3X, horizontal = spacing2X),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = surfaceBrandDefault,
                contentColor = Color.White
            ),
            onClick = debounceClick(onClick = onClick),
            enabled = buttonState != ButtonState.LOADING
        ) {
            if (buttonState == ButtonState.LOADING) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(text = singleButtonActionModel.buttonText)
            }
        }
    }
}


@Composable
fun bottomSheetDoubleActionBottomBarWithLoading(
    buttonState: ButtonState,
    bottomSheetDoubleActionModel: BottomSheetActionModel,
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
        Button(
            modifier = Modifier.weight(1f).height(46.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = surfaceDefault,
                contentColor = Color.Black
            ),
            onClick = debounceClick(onClick = onFirstButtonClick)

        ) {
            Text(text = bottomSheetDoubleActionModel.firstButtonText)
        }
        Spacer(modifier = Modifier.padding(horizontal = spacing2X))

        Button(
            modifier = Modifier.weight(1f).height(46.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = surfaceBrandDefault,
                contentColor = Color.White
            ),
            onClick = debounceClick(onClick = onSecondButtonClick),
            enabled = buttonState != ButtonState.LOADING
        ) {
            if (buttonState == ButtonState.LOADING) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(text = bottomSheetDoubleActionModel.secondButtonText)
            }
        }
    }
}

@Composable
fun customBottomSheetWithImage(
    bottomSheetActionModel: BottomSheetActionModel,
    imageResource: ImageResource,
    description: String,
    onButtonClick: () -> Unit = {},
) {
    val onClickOnButton =
        debounceClick(debounceTime = 1000L, onClick = onButtonClick)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(imageResource),
                contentDescription = null,
                modifier = Modifier.size(160.dp).padding(bottom = 16.dp)
            )

            Text(
                text = description,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CustomTextButton(
                customButtonData = CustomButtonData(
                    title = bottomSheetActionModel.firstButtonText,
                    textColor = bottomSheetActionModel.firstButtonTextColor,
                    backgroundColor = bottomSheetActionModel.firstButtonColor
                ),
                modifier = Modifier
                    .height(48.dp)
                    .clickable { onClickOnButton() }
            )
        }
    }
}


@Composable
fun bottomSheetDoubleActionWithMessage(
    bottomSheetDoubleActionModel: BottomSheetActionModel,
    title: StringResource,
    message: StringResource,
    onFirstButtonClick: () -> Unit = {},
    onSecondButtonClick: () -> Unit = {}
) {
    val titleDialog = stringResource(title)
    val messageDialog = stringResource(message)

    val onClickOnFirstButton = debounceClick(debounceTime = 1000L, onClick = onFirstButtonClick)
    val onClickOnSecondButton = debounceClick(debounceTime = 1000L, onClick = onSecondButtonClick)

    Column(
        modifier = Modifier.padding(6.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = titleDialog,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
        )

        Text(
            text = messageDialog,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            ),
            modifier = Modifier.padding(end = 6.dp, start = 6.dp)
        )
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
                    .weight(1f).clickable { onClickOnFirstButton() }
            )
            Spacer(modifier = Modifier.padding(horizontal = spacing2X))
            CustomButton(
                customButtonData = CustomButtonData(
                    title = bottomSheetDoubleActionModel.secondButtonText,
                    textColor = bottomSheetDoubleActionModel.secondColorTextColor,
                    bottomSheetDoubleActionModel.secondButtonColor
                ), modifier = Modifier
                    .weight(1f).clickable { onClickOnSecondButton() }
            )
        }
    }
}