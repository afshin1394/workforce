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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.components.bottomSheetDoubleActionBottomBarWithLoading
import com.irancell.nwg.wfm.presentation.components.bottomSingleActionComponentWithLoading
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.LifecycleEvent
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.OnLifecycleEvent
import irancell.nwg.wfm.checkPermission
import irancell.nwg.wfm.openAppSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.model.BottomSheetActionModel
import presentation.model.SingleButtonActionModel
import presentation.screens.main.compose.BaseScreen
import presentation.screens.splash.events.CheckVersionEvent
import presentation.screens.splash.events.PermissionEvent
import presentation.screens.splash.viewmodel.SplashScreenVM
import presentation.theme.body_large
import presentation.theme.body_small
import presentation.theme.surfaceBrandDefault
import presentation.theme.textInverse
import presentation.theme.textInverseDisabled
import utils.ButtonState
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
        var buttonState by remember { mutableStateOf(ButtonState.IDLE) }
        val permissionState by viewModel.permissionState.collectAsState()
        val lifecycleEvent by viewModel.lifeCycleEvent.collectAsState()
        val events by viewModel.eventsVersion

        if (lifecycleEvent == LifecycleEvent.ON_RESUME) {
            viewModel.updateLifeCycleEventState(LifecycleEvent.ON_ANY)
            checkPermission({
                viewModel.updatePermissionState(PermissionEvent.IsGranted)
            }, {
                viewModel.updatePermissionState(PermissionEvent.DeniedPermission)
            })
        }

        OnLifecycleEvent { _, event ->
            viewModel.updateLifeCycleEventState(event as LifecycleEvent)
        }

        val errorMessage = stringResource(MR.strings.download_failed)
        val bottomSheetTitle: String =
            when (events) {
                CheckVersionEvent.ForceUpdate->{
                    viewModel.versionData.value?.title ?: ""
                }
                CheckVersionEvent.NormalUpdate->{
                    viewModel.versionData.value?.title ?: ""
                }
                CheckVersionEvent.OkVersion->{
                    ""
                }
                CheckVersionEvent.Default->{
                    ""
                }
                CheckVersionEvent.InvalidToken->{
                    ""
                }

            }

        BaseScreen(
            viewModel = viewModel,
            title = "notStartService",
            scaffoldState = scaffoldState,
            bottomSheetTitle = bottomSheetTitle,
            isShwCloseBtnBottomSheet = !(events == CheckVersionEvent.NormalUpdate ||events == CheckVersionEvent.ForceUpdate),

            bottomSheetContent = {
                when (events) {
                    CheckVersionEvent.ForceUpdate -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = spacing2X),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material.Text(
                                text = viewModel.versionData.value?.description ?: "",
                                style = body_large
                            )
                        }
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }


                    CheckVersionEvent.NormalUpdate -> {
                        androidx.compose.material.Text(
                            text = viewModel.versionData.value?.description ?: "",
                            textAlign = TextAlign.Center,
                            style = body_large,
                            modifier = Modifier.padding(start = spacing2X)
                        )
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }

                    }

                    CheckVersionEvent.OkVersion -> {

                    }

                    CheckVersionEvent.Default -> {
                        scope.launch {
                            scaffoldState.bottomSheetState.collapse()
                        }

                    }

                    CheckVersionEvent.InvalidToken -> {

                    }
                }

            },
            bottomBarBottomSheetContent ={
                when (events) {

                    CheckVersionEvent.ForceUpdate -> {

                        if (permissionState == PermissionEvent.IsGranted) {


                            bottomSingleActionComponentWithLoading(
                                buttonState = buttonState,
                                SingleButtonActionModel(
                                    stringResource( MR.strings.download),
                                    surfaceBrandDefault,
                                    textInverse
                                ), onClick = {
                                    buttonState = ButtonState.LOADING
                                    scope.launch {
                                        val isSuccess = startDownloadFileApk(viewModel.versionData.value?.apk_file ?: "")
                                        if (isSuccess) {
                                            buttonState = ButtonState.COMPLETED
                                        } else {
                                            buttonState = ButtonState.IDLE
                                            scaffoldState.snackbarHostState.showSnackbar(message = errorMessage )
                                        }
                                    }


                                })

                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }

                        }else{
                            if (permissionState == PermissionEvent.DeniedPermission) {
                                val message = stringResource(MR.strings.please_authorize_permissions)
                                val approve = stringResource(MR.strings.approve)
                                scope.launch {

                                    val userAction = scaffoldState.snackbarHostState.showSnackbar(
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
                            bottomSheetDoubleActionBottomBarWithLoading(
                                buttonState = buttonState,
                                BottomSheetActionModel(
                                    stringResource(MR.strings.cancel),
                                    Color.Transparent,
                                    textInverseDisabled,
                                    stringResource(MR.strings.download),
                                    surfaceBrandDefault,
                                    textInverse
                                ), onFirstButtonClick = {
                                    navigator.popAll()
                                    navigator.push(mainScreen)
                                }, onSecondButtonClick = {
                                    buttonState = ButtonState.LOADING
                                    scope.launch {
                                        val isSuccess = startDownloadFileApk(viewModel.versionData.value?.apk_file ?: "")
                                        if (isSuccess) {
                                            buttonState = ButtonState.COMPLETED
                                        } else {
                                            buttonState = ButtonState.IDLE
                                            scaffoldState.snackbarHostState.showSnackbar(message = errorMessage )
                                        }
                                    }
                                })
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }

                        }else{
                            if (permissionState == PermissionEvent.DeniedPermission) {
                                val message = stringResource(MR.strings.please_authorize_permissions)
                                val approve = stringResource(MR.strings.approve)
                                scope.launch {

                                    val userAction = scaffoldState.snackbarHostState.showSnackbar(
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

                        }else{
                            if (permissionState == PermissionEvent.DeniedPermission) {
                                val message = stringResource(MR.strings.please_authorize_permissions)
                                val approve = stringResource(MR.strings.approve)
                                scope.launch {

                                    val userAction = scaffoldState.snackbarHostState.showSnackbar(
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

                    CheckVersionEvent.Default -> {
                        scope.launch {
                            scaffoldState.bottomSheetState.collapse()
                        }
                    }

                    CheckVersionEvent.InvalidToken -> {
                        if (permissionState == PermissionEvent.IsGranted) {
                            navigator.popAll()
                            navigator.push(loginScreen)

                        }else{
                            if (permissionState == PermissionEvent.DeniedPermission) {
                                val message = stringResource(MR.strings.please_authorize_permissions)
                                val approve = stringResource(MR.strings.approve)
                                scope.launch {

                                    val userAction = scaffoldState.snackbarHostState.showSnackbar(
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

            }, content = {
                Surface(
                    modifier = if (events==CheckVersionEvent.NormalUpdate ||events==CheckVersionEvent.ForceUpdate) Modifier.fillMaxSize().blur(7.dp) else Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
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
                            modifier = Modifier
                                .width(72.dp)
                                .height(72.dp)
                                .wrapContentSize()
                                .align(Alignment.Center)
                        )
                        Text(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = spacing2X),
                            text = stringResource(MR.strings.i_ticket),
                            style = body_small
                        )
                    }


                    }

            }

        )
    }
}

