package presentation.screens.auth.compose


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.screens.auth.components.AuthAlertText
import presentation.screens.auth.components.AuthAlertTextItem
import presentation.screens.auth.viewmodel.LoginScreenVM
import presentation.screens.main.components.LocationItem
import presentation.theme.backgroundBackground3

import presentation.theme.body_large
import presentation.theme.error_5
import presentation.theme.mediumDivider
import utils.ViewStates

class LoginScreen : Screen {



    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel : LoginScreenVM = koinInject()
        val state by viewModel.state.collectAsState()

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
            val success =  stringResource(MR.strings.success)
            val error = stringResource(MR.strings.login_error)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = backgroundBackground3),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (state) {
                    ViewStates.Error -> {

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "${error}!",
                                duration = SnackbarDuration.Short,
                            )
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
                        }
                    }

                    ViewStates.Default -> {

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
                    AuthTextField(authTextFieldItem = AuthTextFieldItem(
                        stringResource(MR.strings.company_email), imageResource = MR.images.mail1, stringResource(MR.strings.company_email),
                        hasPassword = false,
                        passwordVisibility = true
                    ), updateText = {
                        email = it.text
                    })

                    if (noEmail) {
                        Spacer(modifier = Modifier.height(spacing1X))
                        AuthAlertText(
                            alertTextItem = AuthAlertTextItem(
                                true, text = stringResource(MR.strings.email_required),
                                textColor = error_5, contentDescriptor = stringResource(MR.strings.email_required)
                            )
                        ) {
                            //onClick
                        }
                    }
                    Spacer(modifier = Modifier.height(spacing3X))

                    AuthTextField(authTextFieldItem = AuthTextFieldItem(
                        stringResource(MR.strings.password), imageResource = MR.images.password, stringResource(MR.strings.password),
                        hasPassword = true,
                        passwordVisibility = false
                    ), updateText = {
                        password = it.text
                    })
                    if (noPassword) {
                        Spacer(modifier = Modifier.height(spacing1X))

                        AuthAlertText(
                            alertTextItem = AuthAlertTextItem(
                                true, text = stringResource(MR.strings.password_required),
                                textColor = error_5, contentDescriptor = stringResource(MR.strings.password_required)
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
                            Napier.log(LogLevel.ASSERT,"email & password",message = "email ${email} password ${password}")
                            viewModel.login(email,password){
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




