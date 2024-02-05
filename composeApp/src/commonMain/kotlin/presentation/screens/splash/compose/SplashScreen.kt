package presentation.screens.splash.compose


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import data.GeneralLocationRepositoryImpl
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

import io.github.aakira.napier.Napier
import irancell.nwg.wfm.LifecycleEvent
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.OnLifecycleEvent
import irancell.nwg.wfm.getSharedPref
import irancell.nwg.wfm.openAppSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import presentation.screens.splash.events.PermissionEvent
import presentation.screens.splash.viewmodel.SplashScreenVM
import presentation.theme.body_small
import utils.SelectLanguage

class SplashScreen() : Screen, KoinComponent {


    @Composable
    override fun Content() {
        val dataSyncRepository: GeneralLocationRepositoryImpl by inject()


        Napier.e("dataSyncRepository" + dataSyncRepository)
        val scope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow
        val loginScreen = rememberScreen(com.irancell.nwg.wfm.presentation.nav.Screen.Auth.Login)
        val mainScreen = rememberScreen(com.irancell.nwg.wfm.presentation.nav.Screen.Main.Menu.MyTickets)
        val snackbarHostState = remember { SnackbarHostState() }
        val factory: PermissionsControllerFactory = rememberPermissionsControllerFactory()
        val viewModel = remember { SplashScreenVM(factory.createPermissionsController()) }
        BindEffect(viewModel.permissionsController)

        val permissionState by viewModel.permissionState.collectAsState()


        OnLifecycleEvent { owner, event ->

            when (event) {
                LifecycleEvent.ON_RESUME -> {
                    viewModel.checkPermissions {
                        scope.launch {
                            delay(2000)
                            navigator.push(loginScreen)
                        }

                    }
                }
            }
        }



        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                }) { contentPadding ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
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

                if ( getSharedPref().getBool(SelectLanguage, false)){
                    navigator.push(mainScreen)
                    getSharedPref().put(SelectLanguage, false)

                }else{
                    if (permissionState == PermissionEvent.DeniedException) {
                        val message=stringResource(MR.strings.please_authorize_permissions)
                        val approve =stringResource(MR.strings.approve)
                        scope.launch {
                            val userAction = snackbarHostState.showSnackbar(
                                message = message,
                                actionLabel = approve,
                                duration = SnackbarDuration.Short,
                                withDismissAction = true
                            )
                            when (userAction) {
                                SnackbarResult.ActionPerformed -> {
                                    openAppSettings()
                                }
                                SnackbarResult.Dismissed -> {
                                }
                            }
                        }


                    }

                    if (permissionState == PermissionEvent.DeniedAlwaysException) {
                        val message=stringResource(MR.strings.please_authorize_permissions)
                        val approve =stringResource(MR.strings.approve)
                        scope.launch {


                            val userAction = snackbarHostState.showSnackbar(
                                message = message,
                                actionLabel = approve,
                                duration = SnackbarDuration.Short,
                                withDismissAction = true
                            )
                            when (userAction) {
                                SnackbarResult.ActionPerformed -> {
                                    openAppSettings()
                                }

                                SnackbarResult.Dismissed -> {
                                }
                            }
                        }
                    }
                    if (permissionState == PermissionEvent.IsGranted) {
                        scope.launch {
                            delay(2000)
                            navigator.push(loginScreen)
                        }
                    }

                }

            }
        }


    }
}

