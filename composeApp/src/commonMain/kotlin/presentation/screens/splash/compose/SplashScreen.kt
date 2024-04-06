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
import androidx.compose.ui.layout.ContentScale

import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow


import com.irancell.nwg.wfm.presentation.theme.spacing2X
import dev.icerock.moko.resources.StringResource

import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

import irancell.nwg.wfm.LifecycleEvent
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.OnLifecycleEvent
import irancell.nwg.wfm.checkPermission
import irancell.nwg.wfm.getSharedPref
import irancell.nwg.wfm.openAppSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import presentation.screens.main.compose.BaseScreen
import presentation.screens.splash.events.PermissionEvent
import presentation.screens.splash.viewmodel.SplashScreenVM
import presentation.theme.body_small

import utils.Token
import utils.ViewStates


class SplashScreen() : Screen, KoinComponent {


    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {


        val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()
        val scope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow
        val loginScreen = rememberScreen(presentation.nav.Screen.Auth.Login)
        val mainScreen = rememberScreen(presentation.nav.Screen.Main.Menu.MyTickets)

        val viewModel = remember { SplashScreenVM() }

        val permissionState by viewModel.permissionState.collectAsState()
        val lifecycleEvent by viewModel.lifeCycleEvent.collectAsState()
        val state by viewModel.state.collectAsState()

        if (lifecycleEvent == LifecycleEvent.ON_RESUME) {
            viewModel.updateLifeCycleEventState(LifecycleEvent.ON_ANY)

            checkPermission({
                viewModel.updatePermissionState(PermissionEvent.IsGranted)
            }, {
                viewModel.updatePermissionState(PermissionEvent.DeniedPermission)
            })
        }

        OnLifecycleEvent { owner, event ->
            viewModel.updateLifeCycleEventState(event as LifecycleEvent)
        }


        BaseScreen(
            viewModel = viewModel,
            title = "stringResource(title)",
            scaffoldState = scaffoldState,
            content = {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(MR.images.bg_splash_screen),
                            contentScale = ContentScale.FillBounds,
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize()
                        )
                        Image(
                            painter = painterResource(MR.images.ic_sdm),
                            contentScale = ContentScale.FillBounds,
                            contentDescription = "",
                            modifier = Modifier
                                .wrapContentSize()
                                .align(Alignment.Center)
                        )
                        Text(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = spacing2X),
                            text = stringResource(MR.strings.work_force_management),
                            style = body_small
                        )
                    }


                    if (permissionState == PermissionEvent.DeniedPermission) {
                        val message = stringResource(MR.strings.please_authorize_permissions)
                        val approve = stringResource(MR.strings.approve)
                        scope.launch {

                            val userAction =it.showSnackbar(
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

                    if (permissionState == PermissionEvent.IsGranted) {

                        scope.launch {
                            delay(1000)
                            if(state != ViewStates.NoGps) {
                                if (getSharedPref().getString(Token).toString().length > 6)
                                    navigator.push(mainScreen)
                                else
                                    navigator.push(loginScreen)
                            }
                        }
                    }

                }

            }

        )
    }
}

