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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.painterResource
import irancell.nwg.wfm.BackButtonHandler
import irancell.nwg.wfm.LifecycleEvent
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.OnLifecycleEvent
import irancell.nwg.wfm.openVpnSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import presentation.model.BottomSheetActionModel
import presentation.theme.surfaceBrandDefault
import utils.BottomSheetTypes
import utils.GpsState
import utils.VpnDetectionStates

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
    isShwCloseBtnBottomSheet: Boolean = true,
    shouldBlurOnBottomSheetExpansion: Boolean = true,
    typeBottomSheet: String = BottomSheetTypes.Default

) {
    val navigator = LocalNavigator.currentOrThrow
    val loginScreen = rememberScreen(presentation.nav.Screen.Auth.Login)
    val verifyScreen = rememberScreen(presentation.nav.Screen.Auth.Verify)
    val splashScreen = rememberScreen(presentation.nav.Screen.Splash)
    val state by viewModel.state.collectAsState()
    val gpsState by viewModel.gpsState.collectAsState()
    val vpnDetectionStates by viewModel.vpnDetectionStates.collectAsState()
    val isDrawerInitialized = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val vpnScaffoldState = rememberBottomSheetScaffoldState()

    OnLifecycleEvent { _, event ->
        when (event) {
            LifecycleEvent.ON_RESUME -> {
                viewModel.updateLifeCycleEventState(LifecycleEvent.ON_ANY)
                viewModel.restrictForeignIp()
                Napier.log(
                    LogLevel.INFO,
                    tag = "onResumeBaseScreen",
                    message = viewModel.vpnDetectionStates.value.toString()
                )
            }

            LifecycleEvent.ON_STOP -> {
                viewModel.updateVpnDetectionState(VpnDetectionStates.HideBottomSheet)
            }
        }
    }

    OnLifecycleEvent { _, event ->
        viewModel.updateLifeCycleEventState(event as LifecycleEvent)
    }

    Box(
        modifier = Modifier.background(color = backgroundBackground3).fillMaxSize()
    ) {
        @Composable
        fun showVpnBottomSheetHandler() {
            when (vpnDetectionStates) {
                VpnDetectionStates.ShowBottomSheet -> {
                    customBottomSheetWithImage(
                        BottomSheetActionModel(
                            stringResource(MR.strings.go_to_setting),
                            surfaceBrandDefault,
                            surfaceBrandDefault,
                        ),
                        description = stringResource(MR.strings.vpn_detected_description),
                        imageResource = MR.images.disconnected,
                        onButtonClick = {
                            openVpnSettings()
                            viewModel.updateState(ViewStates.Default)
                        },
                    )
                    scope.launch {
                        vpnScaffoldState.bottomSheetState.expand()
                    }
                }

                VpnDetectionStates.HideBottomSheet -> {
                    scope.launch {
                        vpnScaffoldState.bottomSheetState.collapse()
                    }
                }

                else -> {

                }
            }
        }


        if (hasDrawer) {
            ModalDrawer(modifier = Modifier.background(color = backgroundBackground3),
                gesturesEnabled = hasSwipeDrawer,
                drawerState = drawerState,
                drawerContent = {
                    drawerContent()
                }) {
                BottomSheetScaffold(modifier = Modifier.onGloballyPositioned {
                    if (!isDrawerInitialized.value) {
                        isDrawerInitialized.value = true
                        scope.launch {
                            drawerState.close()
                        }
                    }
                },
                    scaffoldState = if (vpnDetectionStates == VpnDetectionStates.ShowBottomSheet) vpnScaffoldState else scaffoldState,
                    topBar = { topBar() },
                    sheetPeekHeight = 0.dp,
                    sheetGesturesEnabled = false,
                    sheetShape = RoundedCornerShape(topEnd = radius, topStart = radius),
                    sheetContent = {
                        if (vpnDetectionStates is VpnDetectionStates.ShowBottomSheet) {
                            showVpnBottomSheetHandler()
                        } else {
                            CustomBottomSheet(scaffoldState.bottomSheetState,
                                hasHeader = bottomSheetHasHeader,
                                title = bottomSheetTitle,
                                showClose = isShwCloseBtnBottomSheet,
                                type = typeBottomSheet,
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
                    }) {
                    Column(
                        modifier = if (scaffoldState.bottomSheetState.isCollapsed) Modifier.fillMaxSize()
                        else Modifier.fillMaxSize().blur(7.dp)
                    ) {

                        Box(
                            modifier = Modifier.fillMaxWidth().fillMaxHeight()
                                .background(backgroundBackground3)
                        ) {
                            content(scaffoldState.snackbarHostState)
                            BackButtonHandler.backPress(onBackPressed = {
                                println("checkkkkvalueeee")
                                if (vpnDetectionStates !is VpnDetectionStates.ShowBottomSheet) {
                                    onBackPressed()
                                }
                            })
                            when (gpsState) {
                                GpsState.Default -> {}

                                GpsState.Disabled -> {
                                    GPS.enableGpsDialog(provideAppContext())
                                }

                                GpsState.Enabled -> {}
                                else -> {}
                            }
                            when (state) {
                                ViewStates.EMPTY -> {
                                    Column(
                                        modifier = Modifier.fillMaxSize()
                                            .wrapContentSize(Alignment.Center),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Image(
                                            painter = painterResource(MR.images.ic_empty),
                                            contentDescription = "empty",
                                            modifier = Modifier.width(150.dp).height(120.dp)
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = stringResource(MR.strings.empty_list),
                                            style = TextStyle(
                                                fontSize = 16.sp, fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.wrapContentSize()
                                        )
                                    }
                                }

                                is ViewStates.Error -> {
                                    val errorMessage =
                                        stringResource((state as ViewStates.Error).message)

                                    Napier.log(
                                        LogLevel.INFO, tag = "fkpekfpw", message = errorMessage
                                    )
                                    LaunchedEffect(Unit) {
                                        scaffoldState.snackbarHostState.showSnackbar(message = errorMessage)
                                        viewModel.updateState(ViewStates.Default)
                                    }
                                }

                                is ViewStates.Loading -> {
                                    Box(modifier = Modifier.fillMaxSize()
                                        .background(Color.LightGray.copy(alpha = 0.5f))
                                        .pointerInput(Unit) {
                                            awaitPointerEventScope {
                                                while (true) {
                                                    awaitPointerEvent()
                                                }
                                            }
                                        }) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.align(Alignment.Center),
                                            color = surfaceBrandDefault
                                        )
                                    }
                                }

                                is ViewStates.Success -> {
                                    viewModel.updateState(ViewStates.Default)
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

                                else -> {

                                }
                            }
                        }

                    }
                }
            }
        } else {
            BottomSheetScaffold(modifier = Modifier.background(color = backgroundBackground3),
                scaffoldState = if (vpnDetectionStates == VpnDetectionStates.ShowBottomSheet) vpnScaffoldState else scaffoldState,
                topBar = {
                    topBar()
                },
                sheetPeekHeight = 0.dp,
                sheetGesturesEnabled = false,
                sheetShape = RoundedCornerShape(topEnd = radius, topStart = radius),
                sheetContent = {
                    if (vpnDetectionStates is VpnDetectionStates.ShowBottomSheet) {
                        showVpnBottomSheetHandler()
                    } else {
                        CustomBottomSheet(scaffoldState.bottomSheetState,
                            hasHeader = bottomSheetHasHeader,
                            title = bottomSheetTitle,
                            showClose = isShwCloseBtnBottomSheet,
                            type = typeBottomSheet,
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
                }) {

                Column(
                    modifier = if (scaffoldState.bottomSheetState.isExpanded && shouldBlurOnBottomSheetExpansion) Modifier.fillMaxSize()
                        .blur(7.dp) else Modifier.fillMaxSize()
                ) {

                    Box(
                        modifier = Modifier.fillMaxWidth().fillMaxHeight().background(
                            backgroundBackground3
                        )

                    ) {

                        content(scaffoldState.snackbarHostState)

                        BackButtonHandler.backPress(onBackPressed = {
                            println("checkkkkvalueeee")
                            if (vpnDetectionStates !is VpnDetectionStates.ShowBottomSheet) {
                                onBackPressed()
                            }
                        })

                        when (gpsState) {
                            GpsState.Default -> {}
                            GpsState.Disabled -> {
                                GPS.enableGpsDialog(provideAppContext())
                            }

                            GpsState.Enabled -> {}
                            else -> {}
                        }

                        when (state) {
                            ViewStates.EMPTY -> {
                                Column(
                                    modifier = Modifier.fillMaxSize()
                                        .wrapContentSize(Alignment.Center),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(MR.images.ic_empty),
                                        contentDescription = "empty",
                                        modifier = Modifier.width(150.dp).height(120.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = stringResource(MR.strings.empty_list),
                                        style = TextStyle(
                                            fontSize = 16.sp, fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.wrapContentSize()
                                    )
                                }
                            }

                            is ViewStates.Error -> {
                                val errorMessage =
                                    stringResource((state as ViewStates.Error).message)
                                LaunchedEffect(Unit) {
                                    scaffoldState.snackbarHostState.showSnackbar(message = errorMessage)
                                    viewModel.updateState(ViewStates.Default)
                                }
                                Napier.log(
                                    LogLevel.INFO,
                                    tag = "fkpekfpw",
                                    message = errorMessage.toString()
                                )
                            }

                            is ViewStates.Loading -> {
                                Box(modifier = Modifier.fillMaxSize()
                                    .background(Color.LightGray.copy(alpha = 0.5f))
                                    .pointerInput(Unit) {
                                        awaitPointerEventScope {
                                            while (true) {
                                                awaitPointerEvent()
                                            }
                                        }
                                    }) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center),
                                        color = surfaceBrandDefault
                                    )
                                }
                            }

                            is ViewStates.Success -> {
                                viewModel.updateState(ViewStates.Default)
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

                            else -> {}
                        }
                    }

                }
            }
        }
    }
}

