package presentation.screens.main.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
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
import irancell.nwg.wfm.BackgroundServiceApp
import irancell.nwg.wfm.LifecycleEvent
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.OnLifecycleEvent
import irancell.nwg.wfm.openInternetSettings
import irancell.nwg.wfm.openVpnSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import presentation.model.BottomSheetActionModel
import presentation.theme.surfaceBrandDefault
import utils.BottomSheetTypes
import utils.GpsState
import utils.NetworkStates
import utils.OrientationState
import utils.ServiceState
import utils.VpnDetectionStates
import androidx.compose.material.Card
import presentation.theme.textInverse
import presentation.theme.textInverseDisabled
import utils.TicketListStatus

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
    val gpsScaffoldState = rememberBottomSheetScaffoldState()
    val networkState by viewModel.networkState.collectAsState()
    val serviceState by viewModel.serviceState.collectAsState()
    val ticketListStatus by viewModel.ticketListStatus.collectAsState()
    val orientationState by viewModel.orientationState.collectAsState()
    val isCloseMenuForOrientation = remember { mutableStateOf(false) }
    var previousOrientation by remember { mutableStateOf<OrientationState>(OrientationState.Default) }


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

                else -> {}
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
                    scaffoldState = when {
                        vpnDetectionStates == VpnDetectionStates.ShowBottomSheet -> {
                            vpnScaffoldState
                        }

                        gpsState == GpsState.Disabled -> {
                            gpsScaffoldState
                        }

                        else -> {
                            scaffoldState
                        }
                    },
                    topBar = { topBar() },
                    sheetPeekHeight = 0.dp,
                    sheetGesturesEnabled = false,
                    sheetShape = RoundedCornerShape(topEnd = radius, topStart = radius),
                    sheetContent = {
                        when {
                            vpnDetectionStates is VpnDetectionStates.ShowBottomSheet -> {
                                showVpnBottomSheetHandler()
                            }

                            gpsState is GpsState.Disabled -> {
                                bottomSheetDoubleActionWithMessage(
                                    BottomSheetActionModel(
                                        stringResource(MR.strings.cancel),
                                        Color.Transparent,
                                        textInverseDisabled,
                                        stringResource(MR.strings.enable),
                                        surfaceBrandDefault,
                                        textInverse
                                    ),
                                    MR.strings.GPS_Permission,
                                    MR.strings.disc_gps_permission,

                                    onFirstButtonClick = {
                                        scope.launch {
                                            gpsScaffoldState.bottomSheetState.collapse()
                                        }
                                    },
                                    onSecondButtonClick = {
                                        GPS.enableGpsDialog(provideAppContext())
                                    }
                                )

                                LaunchedEffect(gpsScaffoldState.bottomSheetState.isCollapsed) {
                                    if (gpsState is GpsState.Disabled && gpsScaffoldState.bottomSheetState.isCollapsed) {
                                        delay(300)
                                        gpsScaffoldState.bottomSheetState.expand()
                                    }
                                }

                            }

                            else -> {
                                CustomBottomSheet(
                                    scaffoldState.bottomSheetState,
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
                                    }
                                )
                            }
                        }
                    }
                ) {
                    Column(
                        modifier = if (scaffoldState.bottomSheetState.isCollapsed && gpsScaffoldState.bottomSheetState.isCollapsed)
                            Modifier.fillMaxSize()
                        else
                            Modifier.fillMaxSize().blur(7.dp).clickable(enabled = false) { }
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


                            when (orientationState) {
                                OrientationState.Default -> {

                                }
                                OrientationState.Landscape -> {


                                    if (previousOrientation == OrientationState.Portrait) {

                                        isCloseMenuForOrientation.value = true
                                        scope.launch {
                                            drawerState.close()
                                            delay(200)
                                            isCloseMenuForOrientation.value = false
                                        }
                                    }
                                    previousOrientation = OrientationState.Landscape
                                }

                                OrientationState.Portrait -> {

                                    if (previousOrientation == OrientationState.Landscape) {

                                        isCloseMenuForOrientation.value = true
                                        scope.launch {
                                            drawerState.close()
                                            delay(200)
                                            isCloseMenuForOrientation.value = false
                                        }
                                    }

                                    previousOrientation = OrientationState.Portrait
                                }
                            }



                            when (gpsState) {
                                GpsState.Default -> {
                                    scope.launch {
                                        gpsScaffoldState.bottomSheetState.collapse()
                                    }
                                }

                                GpsState.Enabled -> {
                                    scope.launch {
                                        gpsScaffoldState.bottomSheetState.collapse()
                                    }
                                }

                                else -> {}
                            }
                            when (state) {
                                ViewStates.EMPTY -> {
                                    if (ticketListStatus == TicketListStatus.Empty) {
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

                                else -> {}
                            }
                            when (serviceState) {
                                is ServiceState.Faulty -> {
                                    val key = navigator.items[navigator.items.lastIndex].key
                                    if (key != loginScreen.key && key != verifyScreen.key && key != splashScreen.key) {
                                        val message =
                                            stringResource((serviceState as ServiceState.Faulty).message)
                                        scope.launch {
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

                            if (!gpsScaffoldState.bottomSheetState.isCollapsed) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Transparent)
                                        .clickable(enabled = false) {}
                                )
                            }
                        }
                    }
                }
            }
        } else {
            BottomSheetScaffold(modifier = Modifier.background(color = backgroundBackground3),
                scaffoldState = when {
                    vpnDetectionStates == VpnDetectionStates.ShowBottomSheet -> {
                        vpnScaffoldState
                    }

                    gpsState == GpsState.Disabled -> {
                        gpsScaffoldState
                    }

                    else -> {
                        scaffoldState
                    }
                },
                topBar = {
                    topBar()
                },
                sheetPeekHeight = 0.dp,
                sheetGesturesEnabled = false,
                sheetShape = RoundedCornerShape(topEnd = radius, topStart = radius),
                sheetContent = {
                    when {
                        vpnDetectionStates is VpnDetectionStates.ShowBottomSheet -> {
                            showVpnBottomSheetHandler()
                        }

                        gpsState is GpsState.Disabled -> {


                            bottomSheetDoubleActionWithMessage(
                                BottomSheetActionModel(
                                    stringResource(MR.strings.cancel),
                                    Color.Transparent,
                                    textInverseDisabled,
                                    stringResource(MR.strings.enable),
                                    surfaceBrandDefault,
                                    textInverse
                                ),
                                MR.strings.GPS_Permission,
                                MR.strings.disc_gps_permission,

                                onFirstButtonClick = {
                                    scope.launch {
                                        gpsScaffoldState.bottomSheetState.collapse()
                                    }
                                },
                                onSecondButtonClick = {
                                    GPS.enableGpsDialog(provideAppContext())
                                }
                            )

                            LaunchedEffect(gpsScaffoldState.bottomSheetState.isCollapsed) {
                                if (gpsState is GpsState.Disabled && gpsScaffoldState.bottomSheetState.isCollapsed) {
                                    delay(300)
                                    gpsScaffoldState.bottomSheetState.expand()
                                }
                            }

                        }

                        else -> {
                            CustomBottomSheet(
                                scaffoldState.bottomSheetState,
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
                                }
                            )
                        }
                    }
                }) {

                Column(
                    modifier = if ((scaffoldState.bottomSheetState.isExpanded || gpsScaffoldState.bottomSheetState.isExpanded) && shouldBlurOnBottomSheetExpansion) Modifier.fillMaxSize()
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

                        when (orientationState) {
                            OrientationState.Default -> {

                            }
                            OrientationState.Landscape -> {

                                if (previousOrientation == OrientationState.Portrait) {
                                    isCloseMenuForOrientation.value = true
                                    scope.launch {
                                        drawerState.close()
                                        delay(200)
                                        isCloseMenuForOrientation.value = false
                                    }
                                }
                                previousOrientation = OrientationState.Landscape
                            }

                            OrientationState.Portrait -> {
                                if (previousOrientation == OrientationState.Landscape) {
                                    isCloseMenuForOrientation.value = true
                                    scope.launch {
                                        drawerState.close()
                                        delay(200)
                                        isCloseMenuForOrientation.value = false
                                    }
                                }

                                previousOrientation = OrientationState.Portrait
                            }
                        }


                        when (gpsState) {
                            GpsState.Enabled -> {
                                scope.launch {
                                    gpsScaffoldState.bottomSheetState.collapse()
                                }
                            }

                            else -> {}
                        }

                        when (state) {
//                            ViewStates.EMPTY -> {
//                                if (ticketListStatus == TicketListStatus.Empty) {
//                                    Column(
//                                        modifier = Modifier.fillMaxSize()
//                                            .wrapContentSize(Alignment.Center),
//                                        horizontalAlignment = Alignment.CenterHorizontally,
//                                        verticalArrangement = Arrangement.Center
//                                    ) {
//                                        Image(
//                                            painter = painterResource(MR.images.ic_empty),
//                                            contentDescription = "empty",
//                                            modifier = Modifier.width(150.dp).height(120.dp)
//                                        )
//
//                                        Spacer(modifier = Modifier.height(16.dp))
//                                        Text(
//                                            text = stringResource(MR.strings.empty_list),
//                                            style = TextStyle(
//                                                fontSize = 16.sp, fontWeight = FontWeight.Bold
//                                            ),
//                                            modifier = Modifier.wrapContentSize()
//                                        )
//                                    }
//                                } else {
//                                    Box(modifier = Modifier.fillMaxSize()
//                                        .background(Color.LightGray.copy(alpha = 0.5f))
//                                        .pointerInput(Unit) {
//                                            awaitPointerEventScope {
//                                                while (true) {
//                                                    awaitPointerEvent()
//                                                }
//                                            }
//                                        }) {
//                                        CircularProgressIndicator(
//                                            modifier = Modifier.align(Alignment.Center),
//                                            color = surfaceBrandDefault
//                                        )
//                                    }
//                                }
//                            }

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

                            else -> {}
                        }

                        when (serviceState) {
                            is ServiceState.Faulty -> {
                                val key = navigator.items[navigator.items.lastIndex].key
                                if (key != loginScreen.key && key != verifyScreen.key && key != splashScreen.key) {
                                    val message =
                                        stringResource((serviceState as ServiceState.Faulty).message)
                                    scope.launch {
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

        AnimatedVisibility(
            visible = networkState == NetworkStates.NetworkConnectionNONE || serviceState is ServiceState.NotRunning,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(durationMillis = 500)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(durationMillis = 500)
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                var showTooltip by remember { mutableStateOf(false) }
                var showServiceTooltip by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier
                        .height(60.dp),
                    shape = RoundedCornerShape(topStart = 30.dp, bottomStart = 30.dp),
                    elevation = 22.dp,
                    backgroundColor = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(end = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (networkState == NetworkStates.NetworkConnectionNONE) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(MR.images.circle_red_warning),
                                    contentDescription = "No Connection",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray)
                                        .clickable {
                                            showServiceTooltip = false
                                            showTooltip = true
                                        }
                                )
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(46.dp),
                                    color = Color(0xFFE50000),
                                    strokeWidth = 3.dp
                                )
                            }
                        }

                        if (serviceState is ServiceState.NotRunning) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(MR.images.circle_orange_warning),
                                    contentDescription = "Service Status",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray)
                                        .clickable {
                                            showTooltip = false
                                            showServiceTooltip = true
                                        }
                                )
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(46.dp),
                                    color = Color(0xFFFFA500),
                                    strokeWidth = 3.dp
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = showTooltip) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(y = (-60).dp, x = (-10).dp)
                            .background(Color.Black, shape = CircleShape)
                            .padding(18.dp)
                    ) {
                        Text(
                            text = stringResource(MR.strings.internet_unavailable),
                            color = Color.White,
                            style = MaterialTheme.typography.body2
                        )
                    }
                }

                AnimatedVisibility(visible = showServiceTooltip) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(y = (-60).dp, x = (-10).dp)
                            .background(Color.Black, shape = CircleShape)
                            .padding(18.dp)
                    ) {
                        Text(
                            text = stringResource(MR.strings.service_unavailable),
                            color = Color.White,
                            style = MaterialTheme.typography.body2
                        )
                    }
                }

                LaunchedEffect(showTooltip) {
                    if (showTooltip) {
                        delay(1000)
                        showTooltip = false
                        openInternetSettings()
                    }
                }

                LaunchedEffect(showServiceTooltip) {
                    if (showServiceTooltip) {
                        delay(1000)
                        showServiceTooltip = false
                        BackgroundServiceApp.startBackgroundService()
                    }
                }
            }
        }
    }
}

