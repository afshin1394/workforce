package presentation.screens.splash.compose


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect

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
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import openAppSettings
import presentation.screens.splash.events.PermissionEvent
import presentation.screens.splash.viewmodel.SplashScreenVM
import presentation.theme.body_large
import presentation.theme.body_small
import presentation.theme.h4
import provideLifeCycleOwner

class SplashScreen() : Screen {


    @Composable
    override fun Content() {
        Napier.e("current" +DateTime.getFormattedDate(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) .toString()))
        val scope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow
        val loginScreen = rememberScreen(com.irancell.nwg.wfm.presentation.nav.Screen.Auth.Login)
        val snackbarHostState = remember { SnackbarHostState() }
        val factory: PermissionsControllerFactory = rememberPermissionsControllerFactory()
        val viewModel = remember { SplashScreenVM(factory.createPermissionsController()) }

        val permissionState by viewModel.permissionState.collectAsState()
        LifecycleEffect(onStarted = {
            Napier.log(LogLevel.ASSERT, "lifecycleRegistry: ", message = "doOnResume")

            viewModel.checkPermissions {
                scope.launch {
                    delay(2000)
                    navigator.push(loginScreen)
                }
            }
        })
        BindEffect(viewModel.permissionsController)






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
                        text = "Work Force Management",
                        style = body_small
                    )
                }
                if (permissionState == PermissionEvent.ShowRational) {
                    scope.launch {
                        val userAction = snackbarHostState.showSnackbar(
                            message = "Please authorize needed permissions",
                            actionLabel = "Approve",
                            duration = SnackbarDuration.Indefinite,
                            withDismissAction = true
                        )
                        when (userAction) {
                            SnackbarResult.ActionPerformed -> {
                                viewModel.checkPermissions {
                                    scope.launch {
                                        delay(2000)
                                        navigator.push(loginScreen)
                                    }
                                }
                            }

                            SnackbarResult.Dismissed -> {
                            }
                        }
                    }


                }

                if (permissionState == PermissionEvent.OpenAppSettings) {
                    scope.launch {
                        val userAction = snackbarHostState.showSnackbar(
                            message = "Please authorize needed permissions",
                            actionLabel = "Approve",
                            duration = SnackbarDuration.Indefinite,
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

