package presentation.screens.main.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.irancell.nwg.wfm.presentation.components.*
import presentation.theme.backgroundBackground3
import com.irancell.nwg.wfm.presentation.theme.radius
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.GPS
import irancell.nwg.wfm.provideAppContext

import utils.BaseViewModel
import utils.ViewStates
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.painterResource
import irancell.nwg.wfm.BackButtonHandler
import irancell.nwg.wfm.MR
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import utils.GpsState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T : BaseViewModel> BaseScreen(
    viewModel: T,
    scaffoldState: BottomSheetScaffoldState,
    title: String,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    hasDrawer: Boolean = false,
    content: @Composable (snackBarHost: SnackbarHostState) -> Unit = {},
    topBar: @Composable () -> Unit = {},
    drawerContent: @Composable () -> Unit = {},
    bottomSheetHasHeader: Boolean = true,
    bottomSheetTitle: String = "",
    bottomSheetContent: @Composable (bottomSheetState: BottomSheetState) -> Unit = {},
    bottomBarBottomSheetContent: @Composable (bottomSheetState: BottomSheetState) -> Unit = {},
    onCloseBottomSheet: () -> Unit = {},
    onBackPressed: () -> Unit = {},
    hasSwipeDrawer: Boolean = true,

    ) {
    val navigator = LocalNavigator.currentOrThrow
    val loginScreen = rememberScreen(presentation.nav.Screen.Auth.Login)
    val verifyScreen = rememberScreen(presentation.nav.Screen.Auth.Verify)
    val splashScreen = rememberScreen(presentation.nav.Screen.Splash)
    val state by viewModel.state.collectAsState()
    val gpsState by viewModel.gpsState.collectAsState()

    var isDrawerInitialized = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .background(color = backgroundBackground3)
            .fillMaxSize()
    ) {

        if (hasDrawer) {
            ModalDrawer(

                modifier = Modifier.background(color = backgroundBackground3),
                gesturesEnabled = hasSwipeDrawer,
                drawerState = drawerState,
                drawerContent = {

                    drawerContent()

                }) {
                BottomSheetScaffold(
                    modifier = Modifier.onGloballyPositioned {
                        if (!isDrawerInitialized.value) {
                            isDrawerInitialized.value = true
                            scope.launch {
                                drawerState.close()
                            }
                        }

                    },
                    scaffoldState = scaffoldState,
                    topBar = {
                        topBar()
                    },


                    sheetPeekHeight = 0.dp,
                    sheetGesturesEnabled = false,
                    sheetShape = RoundedCornerShape(topEnd = radius, topStart = radius),
                    sheetContent = {
                        CustomBottomSheet(
                            scaffoldState.bottomSheetState,
                            hasHeader = bottomSheetHasHeader,
                            title = bottomSheetTitle,
                            content = {
                                bottomSheetContent(scaffoldState.bottomSheetState)
                            },
                            onClose = {
                                onCloseBottomSheet()
                            },
                            bottomBar = {
                                bottomBarBottomSheetContent(scaffoldState.bottomSheetState)
                            })
                    }
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(backgroundBackground3)

                    ) {
                        content(scaffoldState.snackbarHostState)
                        BackButtonHandler.backPress(onBackPressed = {
                            println("checkkkkvalueeee")
                            onBackPressed()


                        })
                        when (gpsState) {
                            GpsState.Default -> {

                            }

                            GpsState.Disabled -> {
                                GPS.enableGpsDialog(provideAppContext())
                            }

                            GpsState.Enabled -> {

                            }
                        }
                        when (state) {
                            ViewStates.Default -> {

                            }

                            ViewStates.EMPTY -> {

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentSize(Alignment.Center),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(MR.images.ic_empty),
                                        contentDescription = "empty",
                                        modifier = Modifier
                                            .width(150.dp)
                                            .height(120.dp)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = stringResource(MR.strings.empty_list),
                                        style = TextStyle(fontSize = 16.sp,    fontWeight = FontWeight.Bold ),
                                        modifier = Modifier.wrapContentSize()
                                    )
                                }
                            }

                            is ViewStates.Error -> {
                                val errorMessage =
                                    stringResource((state as ViewStates.Error).message)

                                Napier.log(LogLevel.INFO, tag = "fkpekfpw", message = errorMessage)
                                LaunchedEffect(Unit) {
                                    scaffoldState.snackbarHostState.showSnackbar(message = errorMessage)

                                    viewModel.updateState(ViewStates.Default)

                                }
                            }

                           is ViewStates.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.White.copy(alpha = 0.3f))
                                        .pointerInput(Unit) {
                                            awaitPointerEventScope {
                                                while (true) {
                                                    awaitPointerEvent()
                                                }
                                            }
                                        }
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                }
                            }


                            is ViewStates.Success -> {
                                viewModel.updateState(ViewStates.Default)
                            }

                            ViewStates.Reload -> {

                            }

                            is ViewStates.UnAuthorized -> {
                                val key = navigator.items[navigator.items.lastIndex].key
                                if (key != loginScreen.key && key != verifyScreen.key && key != splashScreen.key) {
                                    val message =
                                        stringResource((state as ViewStates.UnAuthorized).message)

                                    LaunchedEffect(Unit) {
                                        scaffoldState.snackbarHostState.showSnackbar(message = message)

                                        delay(200)
                                        navigator.popAll()
                                        navigator.push(loginScreen)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            BottomSheetScaffold(modifier = Modifier.background(color = backgroundBackground3),
                scaffoldState = scaffoldState,
                topBar = {
                    topBar()
                },
                sheetPeekHeight = 0.dp,
                sheetGesturesEnabled = false,
                sheetShape = RoundedCornerShape(topEnd = radius, topStart = radius),
                sheetContent = {
                    CustomBottomSheet(
                        scaffoldState.bottomSheetState,
                        hasHeader = bottomSheetHasHeader,
                        title = bottomSheetTitle,
                        content = {
                            bottomSheetContent(scaffoldState.bottomSheetState)
                        },
                        onClose = {
                            onCloseBottomSheet()
                        },
                        bottomBar = {
                            bottomBarBottomSheetContent(scaffoldState.bottomSheetState)
                        })
                }
            ) {


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(backgroundBackground3)


                ) {
                    content(scaffoldState.snackbarHostState)

                    BackButtonHandler.backPress(onBackPressed = {
                        println("checkkkkvalueeee")
                        onBackPressed()
                    })


                    when (gpsState) {
                        GpsState.Default -> {

                        }

                        GpsState.Disabled -> {
                            GPS.enableGpsDialog(provideAppContext())
                        }

                        GpsState.Enabled -> {

                        }
                    }

                    when (state) {
                        ViewStates.Default -> {

                        }

                        ViewStates.EMPTY -> {


                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentSize(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(MR.images.ic_empty),
                                    contentDescription = "empty",
                                    modifier = Modifier
                                        .width(150.dp)
                                        .height(120.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = stringResource(MR.strings.empty_list),
                                    style = TextStyle(fontSize = 16.sp , fontWeight = FontWeight.Bold),
                                    modifier = Modifier.wrapContentSize()
                                )
                            }
                        }

                        is ViewStates.Error -> {
                            val errorMessage = stringResource((state as ViewStates.Error).message)
                            LaunchedEffect(Unit) {
                                scaffoldState.snackbarHostState.showSnackbar(message = "$errorMessage")
                                viewModel.updateState(ViewStates.Default)

                            }
                            Napier.log(
                                LogLevel.INFO,
                                tag = "fkpekfpw",
                                message = errorMessage.toString()
                            )
                        }

                       is ViewStates.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.White.copy(alpha = 0.3f))
                                    .pointerInput(Unit) {
                                        awaitPointerEventScope {
                                            while (true) {
                                                awaitPointerEvent()
                                            }
                                        }
                                    }
                            ) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                        }


                        is ViewStates.Success -> {
                            viewModel.updateState(ViewStates.Default)
                        }

                        ViewStates.Reload -> {

                        }

                        is ViewStates.UnAuthorized -> {
                            val key = navigator.items[navigator.items.lastIndex].key

                            if (key != loginScreen.key && key != verifyScreen.key && key != splashScreen.key) {
                                val message =
                                    stringResource((state as ViewStates.UnAuthorized).message)


                                LaunchedEffect(Unit) {
                                    scaffoldState.snackbarHostState.showSnackbar(message = message)

                                    delay(200)
                                    if (navigator.items[navigator.items.lastIndex].key != loginScreen.key) {
                                        navigator.popAll()
                                        navigator.push(loginScreen)
                                    }
                                }

                            }
                        }
                    }


                }
            }
        }


    }

}

