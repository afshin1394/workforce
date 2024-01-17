package presentation.screens.auth.compose


import Location
import Platform
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

import com.irancell.nwg.wfm.presentation.screens.auth.components.*
import com.irancell.nwg.wfm.presentation.theme.*
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import presentation.screens.auth.components.AuthAlertText
import presentation.screens.auth.components.AuthAlertTextItem
import presentation.theme.backgroundBackground3

import presentation.theme.body_large
import presentation.theme.error_5

class LoginScreen : Screen {



    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val verifyScreen =
            rememberScreen(com.irancell.nwg.wfm.presentation.nav.Screen.Auth.Verify(phoneNumber = "0912087866563"))

        var noPassword by remember {
            mutableStateOf(false)
        }
        var noEmail by remember {
            mutableStateOf(false)
        }
        var notEnoughChar by remember {
            mutableStateOf(false)
        }

        var email by remember {
            mutableStateOf("")
        }

        var password by remember {
            mutableStateOf("")
        }
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }) {




            Column(

                modifier = Modifier
                    .fillMaxSize()
                    .background(color = backgroundBackground3),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = dev.icerock.moko.resources.compose.painterResource(MR.images.ic_sdm),
                    contentDescription = "ic_wfm",
                    modifier = Modifier
                        .weight(2f)
                        .wrapContentSize()
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(4f)
                        .padding(spacing2X)
                ) {
                    Text(text = "Login with your MTN account", style = body_large)
                    Spacer(modifier = Modifier.height(spacing3X))
                    AuthTextField(authTextFieldItem = AuthTextFieldItem(
                        "Company email", imageResource = MR.images.mail1, "company email",
                        hasPassword = false,
                        passwordVisibility = true
                    ), updateText = {
                        email = it.text
                    })

                    if (noEmail) {
                        Spacer(modifier = Modifier.height(spacing1X))
                        AuthAlertText(
                            alertTextItem = AuthAlertTextItem(
                                true, text = "Email is required",
                                textColor = error_5, contentDescriptor = "Email is required"
                            )
                        ) {
                            //onClick
                        }
                    }
                    Spacer(modifier = Modifier.height(spacing3X))

                    AuthTextField(authTextFieldItem = AuthTextFieldItem(
                        "Password", imageResource = MR.images.password, "password",
                        hasPassword = true,
                        passwordVisibility = false
                    ), updateText = {
                        password = it.text
                    })
                    if (noPassword) {
                        Spacer(modifier = Modifier.height(spacing1X))

                        AuthAlertText(
                            alertTextItem = AuthAlertTextItem(
                                true, text = "Password is required",
                                textColor = error_5, contentDescriptor = "Email is required"
                            )
                        ) {
                            //onClick
                        }
                    }
                    if (notEnoughChar) {
                        Spacer(modifier = Modifier.height(spacing1X))

                        AuthAlertText(
                            alertTextItem = AuthAlertTextItem(

                                true,
                                text = "Password should be at least 8 characters",
                                textColor = error_5,
                                contentDescriptor = "Password should be at least 8 characters"
                            )
                        ) {
                            //onClick
                        }
                    }
                    Spacer(modifier = Modifier.height(spacing5X))
                    AuthButton(authButtonItem = AuthButtonItem("Sign In")) {
                        //onClick
                        noEmail = email.isEmpty()
                        noPassword = password.isEmpty()
                        notEnoughChar = password.length < 8 && password.isNotEmpty()

                        if (!noEmail && !noPassword && !notEnoughChar)
                            navigator.push(verifyScreen)
//                    navHostController.navigate(Screen.Auth.Verify.route+"/$email")
                    }
                }
            }
        }
    }
}




