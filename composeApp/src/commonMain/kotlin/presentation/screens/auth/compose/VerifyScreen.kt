package com.irancell.nwg.wfm.presentation.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.screens.auth.components.*
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.MR
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.screens.auth.components.AuthAlertText
import presentation.screens.auth.components.AuthAlertTextItem
import presentation.screens.auth.viewmodel.VerifyScreenVM
import presentation.screens.splash.compose.SplashScreen
import presentation.theme.backgroundBackground3
import presentation.theme.textBrand
import utils.ViewStates

class VerifyScreen(private val phoneNumber : String = "") : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: VerifyScreenVM = koinInject()
        val state by viewModel.state.collectAsState()
        val remainTime by viewModel.remainTime.collectAsState()
        val finishTimer by viewModel.finishTimer.collectAsState()

        val mainScreen =
            rememberScreen(com.irancell.nwg.wfm.presentation.nav.Screen.Main.Menu.MyTickets)

        val scope = rememberCoroutineScope()
        val phoneNumberState by remember {
            mutableStateOf(phoneNumber)
        }

        var otpCode by remember {
            mutableStateOf("")
        }
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
                    painter = painterResource(MR.images.ic_sdm),
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
                    val success =  stringResource(MR.strings.success)
                    val error = stringResource(MR.strings.verify_error)
                    when (state) {
                       is ViewStates.Error -> {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "$error",
                                    duration = SnackbarDuration.Short,
                                )
                            }
                        }

                        ViewStates.Loading -> {
//                            CircularProgressIndicator()
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

                    Text(text =
                    buildAnnotatedString {
                        val messageEnterCode = stringResource(MR.strings.enter_verification_code)

                        withStyle(style = ParagraphStyle()) {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp
                                ),
                            ) {
                                append(messageEnterCode)
                            }

                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                            ) {
                                //  append(phoneNumberState)
                            }
                        }

                    }
                    )

                    Spacer(modifier = Modifier.height(spacing3X))
                    AuthVerificationCodeRow(otpText = otpCode, onOtpTextChange = { otp, boolean ->
                        otpCode = otp
                    })

                    Spacer(modifier = Modifier.height(spacing3X))
                    if (!finishTimer)
                        Text(text = "${stringResource(MR.strings.waiting_time_receive)} : $remainTime")
                    else
                        AuthAlertText(
                            alertTextItem = AuthAlertTextItem(
                                false,
                                text = stringResource(MR.strings.resend_code),
                                textColor = textBrand
                            )
                        ) {
                            //onClick
                            viewModel.resendCode(onError = {
                                navigator.pop()
                            })
                        }




                    Spacer(modifier = Modifier.height(spacing5X))
                    AuthButton(authButtonItem = AuthButtonItem(stringResource(MR.strings.verify))) {
                        //onClick
                        if (otpCode.length == 6) {
                            viewModel.verify(otpCode) {
                                navigator.popUntil { it == SplashScreen() }
                                navigator.push(mainScreen)
                            }


//                    navHostController.navigate(Screen.Main.route){
//                        popUpTo(Screen.Auth.route){
//                            inclusive = true
//                        }
//                    }
                        }
                    }
                }
            }
        }
    }


}
