package presentation.screens.splash.compose


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.components.bottomSheetSingleActionBottomBar
import com.irancell.nwg.wfm.presentation.components.customBottomSheetWithImage


import com.irancell.nwg.wfm.presentation.theme.spacing2X

import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier

import irancell.nwg.wfm.LifecycleEvent
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.OnLifecycleEvent
import irancell.nwg.wfm.checkPermission
import irancell.nwg.wfm.openAppSettings
import irancell.nwg.wfm.openVpnSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.components.ButtonState
import presentation.components.CustomDialogDoubleActionWithLoading

import presentation.components.CustomDialogWithLoading
import presentation.model.BottomSheetActionModel
import presentation.screens.main.compose.BaseScreen
import presentation.screens.splash.events.CheckVersionEvent
import presentation.screens.splash.events.PermissionEvent
import presentation.screens.splash.viewmodel.SplashScreenVM
import presentation.theme.body_small
import presentation.theme.surfaceBrandDefault
import presentation.theme.surfaceDefault
import presentation.theme.textInverse
import presentation.theme.textPrimary

import utils.startDownloadFileApk


class SplashScreen : Screen {


    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {


        val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()
        val scope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow
        val loginScreen = rememberScreen(presentation.nav.Screen.Auth.Login)
        val mainScreen = rememberScreen(presentation.nav.Screen.Main.Menu.MyTickets)
        val viewModel: SplashScreenVM = koinInject()
        var showVersionDialog by remember { mutableStateOf(false) }
        var buttonState by remember { mutableStateOf(ButtonState.IDLE) }
        val permissionState by viewModel.permissionState.collectAsState()
        val lifecycleEvent by viewModel.lifeCycleEvent.collectAsState()
        val state by viewModel.state.collectAsState()
        var events by viewModel.eventsVersion
        val showVpnBottomSheet by viewModel.showVpnBottomSheet.collectAsState()


        if (lifecycleEvent == LifecycleEvent.ON_RESUME) {
            viewModel.updateLifeCycleEventState(LifecycleEvent.ON_ANY)
            viewModel.restrictForeignIp()

            checkPermission({
                viewModel.updatePermissionState(PermissionEvent.IsGranted)
            }, {
                viewModel.updatePermissionState(PermissionEvent.DeniedPermission)
            })
        }

        OnLifecycleEvent { owner, event ->
            viewModel.updateLifeCycleEventState(event as LifecycleEvent)
        }


        BaseScreen(viewModel = viewModel,
            title = "notStartService",
            scaffoldState = scaffoldState,
            bottomSheetTitle = when {
                showVpnBottomSheet -> stringResource(MR.strings.vpn_detected)
                else -> ""
            },
            bottomSheetHasHeader = false,
            bottomSheetContent = {
                Napier.log(
                    LogLevel.ASSERT,
                    tag = "showVpnBottomSheet",
                    message = "$showVpnBottomSheet"
                )
                if (showVpnBottomSheet) {

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
                        },
                    )
                    scope.launch {
                        scaffoldState.bottomSheetState.expand()
                    }
                } else {
                    scope.launch {
                        scaffoldState.bottomSheetState.collapse()
                    }
                }
            },
            content = {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {

                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(MR.images.bg_splash_screen),
                            contentScale = ContentScale.FillBounds,
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(color = Color.LightGray),
                            modifier = Modifier.fillMaxSize()
                        )
                        Image(
                            painter = painterResource(MR.images.ic_i_ticket),
                            contentScale = ContentScale.FillBounds,
                            contentDescription = "",
                            modifier = Modifier.width(72.dp).height(72.dp).wrapContentSize()
                                .align(Alignment.Center)
                        )
                        Text(
                            modifier = Modifier.align(Alignment.BottomCenter)
                                .padding(bottom = spacing2X),
                            text = stringResource(MR.strings.i_ticket),
                            style = body_small
                        )
                    }

                    if (showVersionDialog) {
                        val errorMessage = stringResource(MR.strings.download_failed)
                        if (events == CheckVersionEvent.NormalUpdate) {
                            CustomDialogDoubleActionWithLoading(showDialog = showVersionDialog,
                                message = viewModel.versionData.value?.description ?: "",
                                title = viewModel.versionData.value?.title ?: "",
                                titleButton = MR.strings.download,
                                buttonState = buttonState,
                                onDismiss = {
                                    showVersionDialog = false
                                    events = CheckVersionEvent.Default
                                    navigator.popAll()
                                    navigator.push(mainScreen)

                                },
                                onConfirm = {
                                    buttonState = ButtonState.LOADING
                                    scope.launch {
                                        val isSuccess = startDownloadFileApk(
                                            viewModel.versionData.value?.apk_file ?: ""
                                        )
                                        if (isSuccess) {
                                            buttonState = ButtonState.COMPLETED
                                        } else {
                                            buttonState = ButtonState.IDLE
                                            scaffoldState.snackbarHostState.showSnackbar(message = errorMessage)
                                        }
                                    }
                                })
                        } else {
                            CustomDialogWithLoading(showDialog = showVersionDialog,
                                message = viewModel.versionData.value?.description ?: "",
                                title = viewModel.versionData.value?.title ?: "",
                                titleButton = MR.strings.download,
                                buttonState = buttonState,
                                onDismiss = { },
                                onConfirm = {
                                    buttonState = ButtonState.LOADING
                                    scope.launch {
                                        val isSuccess = startDownloadFileApk(
                                            viewModel.versionData.value?.apk_file ?: ""
                                        )
                                        if (isSuccess) {
                                            buttonState = ButtonState.COMPLETED
                                        } else {
                                            buttonState = ButtonState.IDLE
                                            scaffoldState.snackbarHostState.showSnackbar(message = errorMessage)
                                        }
                                    }
                                })
                        }
                    }

                    when (events) {
                        CheckVersionEvent.Default -> {


                        }

                        CheckVersionEvent.ForceUpdate -> {
                            if (permissionState == PermissionEvent.IsGranted) {
                                showVersionDialog = true

                            } else {
                                if (permissionState == PermissionEvent.DeniedPermission) {
                                    val message =
                                        stringResource(MR.strings.please_authorize_permissions)
                                    val approve = stringResource(MR.strings.approve)
                                    scope.launch {

                                        val userAction = it.showSnackbar(
                                            message = message,
                                            actionLabel = approve,
                                            duration = SnackbarDuration.Long,

                                            )

                                        viewModel.updatePermissionState(PermissionEvent.CheckPermission)
                                        when (userAction) {
                                            SnackbarResult.ActionPerformed -> {
                                                openAppSettings()
                                                delay(2000)
                                                viewModel.changeStateDenied()
                                            }

                                            SnackbarResult.Dismissed -> {


                                            }
                                        }
                                    }
                                }
                            }

                        }

                        CheckVersionEvent.NormalUpdate -> {
                            if (permissionState == PermissionEvent.IsGranted) {
                                showVersionDialog = true

                            } else {
                                if (permissionState == PermissionEvent.DeniedPermission) {
                                    val message =
                                        stringResource(MR.strings.please_authorize_permissions)
                                    val approve = stringResource(MR.strings.approve)
                                    scope.launch {

                                        val userAction = it.showSnackbar(
                                            message = message,
                                            actionLabel = approve,
                                            duration = SnackbarDuration.Long,

                                            )

                                        viewModel.updatePermissionState(PermissionEvent.CheckPermission)
                                        when (userAction) {
                                            SnackbarResult.ActionPerformed -> {
                                                openAppSettings()
                                                delay(2000)
                                                viewModel.changeStateDenied()
                                            }

                                            SnackbarResult.Dismissed -> {


                                            }
                                        }
                                    }
                                }
                            }

                        }

                        CheckVersionEvent.InvalidToken -> {
                            if (permissionState == PermissionEvent.IsGranted) {
                                navigator.popAll()
                                navigator.push(loginScreen)

                            } else {
                                if (permissionState == PermissionEvent.DeniedPermission) {
                                    val message =
                                        stringResource(MR.strings.please_authorize_permissions)
                                    val approve = stringResource(MR.strings.approve)
                                    scope.launch {

                                        val userAction = it.showSnackbar(
                                            message = message,
                                            actionLabel = approve,
                                            duration = SnackbarDuration.Long,

                                            )

                                        viewModel.updatePermissionState(PermissionEvent.CheckPermission)
                                        when (userAction) {
                                            SnackbarResult.ActionPerformed -> {
                                                openAppSettings()
                                                delay(2000)
                                                viewModel.changeStateDenied()
                                            }

                                            SnackbarResult.Dismissed -> {


                                            }
                                        }
                                    }
                                }
                            }


                        }

                        CheckVersionEvent.OkVersion -> {
                            if (permissionState == PermissionEvent.IsGranted) {
                                navigator.popAll()
                                navigator.push(mainScreen)

                            } else {
                                if (permissionState == PermissionEvent.DeniedPermission) {
                                    val message =
                                        stringResource(MR.strings.please_authorize_permissions)
                                    val approve = stringResource(MR.strings.approve)
                                    scope.launch {

                                        val userAction = it.showSnackbar(
                                            message = message,
                                            actionLabel = approve,
                                            duration = SnackbarDuration.Long,

                                            )

                                        viewModel.updatePermissionState(PermissionEvent.CheckPermission)
                                        when (userAction) {
                                            SnackbarResult.ActionPerformed -> {
                                                openAppSettings()
                                                delay(2000)
                                                viewModel.changeStateDenied()
                                            }

                                            SnackbarResult.Dismissed -> {


                                            }
                                        }
                                    }
                                }
                            }


                        }

                    }

                }


            }

        )
    }
}

