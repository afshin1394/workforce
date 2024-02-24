package presentation.screens.auth.compose


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
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.MR.strings.email
import irancell.nwg.wfm.MR.strings.update
import irancell.nwg.wfm.getSharedPref
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.screens.auth.components.AuthAlertText
import presentation.screens.auth.components.AuthAlertTextItem
import presentation.screens.auth.components.AuthTextField
import presentation.screens.auth.components.AuthTextFieldItem
import presentation.screens.auth.viewmodel.LoginScreenVM
import presentation.theme.backgroundBackground3

import presentation.theme.body_large
import presentation.theme.error_5
import utils.PhoneNumber
import utils.ViewStates

class LoginScreen : Screen {


    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: LoginScreenVM = koinInject()
        val state by viewModel.state.collectAsState()
        val email by viewModel.email.collectAsState()
        val password by viewModel.password.collectAsState()


        val verifyScreen =
            rememberScreen(
                com.irancell.nwg.wfm.presentation.nav.Screen.Auth.Verify(
                    phoneNumber = getSharedPref().getString(
                        PhoneNumber
                    ) ?: ""
                )
            )

        var noPassword by remember {
            mutableStateOf(false)
        }
        var noEmail by remember {
            mutableStateOf(false)
        }
        var notEnoughChar by remember {
            mutableStateOf(false)
        }


        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }) {
            val success = stringResource(MR.strings.success)
            val error = stringResource(MR.strings.login_error)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = backgroundBackground3),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (state) {
                    is ViewStates.Error -> {

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "${error}!",
                                duration = SnackbarDuration.Short,
                            )
                            viewModel.updateState(ViewStates.Default)
                        }

                    }

                    ViewStates.Loading -> {
//                        CircularProgressIndicator()
                    }

                    ViewStates.Success -> {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "${success}!",
                                duration = SnackbarDuration.Short,
                            )
                            viewModel.updateState(ViewStates.Default)
                        }
                    }

                    ViewStates.Default -> {

                    }

                    ViewStates.NoGps -> {

                    }
                }
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
                    Text(text = stringResource(MR.strings.login_mtn_account), style = body_large)
                    Spacer(modifier = Modifier.height(spacing3X))

                    AuthTextField(defaultText = email, authTextFieldItem = AuthTextFieldItem(
                        stringResource(MR.strings.company_email),
                        imageResource = MR.images.mail1,
                        stringResource(MR.strings.company_email),
                        hasPassword = false,
                        passwordVisibility = true
                    ), updateText = {
                        viewModel.updateEmail(it.text)
                    })

                    if (noEmail) {
                        Spacer(modifier = Modifier.height(spacing1X))
                        AuthAlertText(
                            alertTextItem = AuthAlertTextItem(
                                true,
                                text = stringResource(MR.strings.email_required),
                                textColor = error_5,
                                contentDescriptor = stringResource(MR.strings.email_required)
                            )
                        ) {
                            //onClick
                        }
                    }
                    Spacer(modifier = Modifier.height(spacing3X))

                    AuthTextField(defaultText = password, authTextFieldItem = AuthTextFieldItem(
                        stringResource(MR.strings.password),
                        imageResource = MR.images.password,
                        stringResource(MR.strings.password),
                        hasPassword = true,
                        passwordVisibility = false
                    ), updateText = {
                        viewModel.updatePassword(it.text)
                    })
                    if (noPassword) {
                        Spacer(modifier = Modifier.height(spacing1X))

                        AuthAlertText(
                            alertTextItem = AuthAlertTextItem(
                                true,
                                text = stringResource(MR.strings.password_required),
                                textColor = error_5,
                                contentDescriptor = stringResource(MR.strings.password_required)
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
                                text = stringResource(MR.strings.password_should_least_8_characters),
                                textColor = error_5,
                                contentDescriptor = stringResource(MR.strings.password_should_least_8_characters)
                            )
                        ) {
                            //onClick
                        }
                    }
                    Spacer(modifier = Modifier.height(spacing5X))
                    AuthButton(authButtonItem = AuthButtonItem(stringResource(MR.strings.sign_in))) {
                        //onClick
                        noEmail = email.isEmpty()
                        noPassword = password.isEmpty()
                        notEnoughChar = password.length < 8 && password.isNotEmpty()

                        if (!noEmail && !noPassword && !notEnoughChar) {
                            Napier.log(
                                LogLevel.ASSERT,
                                "email & password",
                                message = "email ${email} password ${password}"
                            )
                            viewModel.login(email, password) {
                                navigator.push(verifyScreen)
                            }
                        }
//                    navHostController.navigate(Screen.Auth.Verify.route+"/$email")
                    }
                }
            }
        }
    }
}




