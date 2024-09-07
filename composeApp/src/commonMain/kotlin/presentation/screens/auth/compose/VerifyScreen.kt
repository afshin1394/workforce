package presentation.screens.auth.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.screens.auth.components.*
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.components.ButtonState

import presentation.components.CustomDialogDoubleActionWithLoading
import presentation.components.CustomDialogWithLoading
import presentation.screens.auth.components.AuthAlertText
import presentation.screens.auth.components.AuthAlertTextItem
import presentation.screens.auth.viewmodel.VerifyScreenVM
import presentation.screens.main.compose.BaseScreen
import presentation.screens.splash.compose.SplashScreen
import presentation.screens.splash.events.CheckVersionEvent
import presentation.theme.backgroundBackground3
import presentation.theme.textBrand
import utils.ViewStates
import utils.startDownloadFileApk

class VerifyScreen(private val phoneNumber: String = "") : Screen {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: VerifyScreenVM = koinInject()
        val state by viewModel.state.collectAsState()
        val scaffoldState = rememberBottomSheetScaffoldState()
        val remainTime by viewModel.remainTime.collectAsState()
        val finishTimer by viewModel.finishTimer.collectAsState()
        val smsCode by viewModel.otpCode.collectAsState()
        val mainScreen = rememberScreen(presentation.nav.Screen.Main.Menu.MyTickets)
        val phoneNumberState by remember { mutableStateOf(phoneNumber) }
        val events by viewModel.eventsVersion
        var showVersionDialog by remember { mutableStateOf(false) }
        var buttonState by remember { mutableStateOf(ButtonState.IDLE) }
        val scope = rememberCoroutineScope()



        BaseScreen(
            onBackPressed = {
                navigator.pop()
            },
            viewModel = viewModel,
            scaffoldState = scaffoldState,
            title = stringResource(MR.strings.verify),
            content = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = backgroundBackground3),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(MR.images.ic_i_ticket),
                            contentDescription = "ic_wfm",
                            modifier = Modifier
                                .width(82.dp)
                                .height(82.dp)
                                .weight(2f)
                                .wrapContentSize()
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(4f)
                                .padding(spacing2X)
                        ) {


                            if (showVersionDialog) {
                                val errorMessage = stringResource(MR.strings.download_failed)
                                if (events == CheckVersionEvent.NormalUpdate) {
                                    CustomDialogDoubleActionWithLoading(
                                        showDialog = showVersionDialog,
                                        message = viewModel.versionData.value?.description ?: "",
                                        title = viewModel.versionData.value?.title ?: "",
                                        titleButton = MR.strings.download,
                                        buttonState = buttonState,
                                        onDismiss = {
                                            showVersionDialog = false
                                            navigator.popAll()
                                            navigator.push(mainScreen)
                                        },
                                        onConfirm = {
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
                                        }
                                    )
                                } else {
                                    CustomDialogWithLoading(
                                        showDialog = showVersionDialog,
                                        message = viewModel.versionData.value?.description ?: "",
                                        title = viewModel.versionData.value?.title ?: "",
                                        titleButton = MR.strings.download,
                                        buttonState = buttonState,
                                        onDismiss = { },
                                        onConfirm = {
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
                                        }
                                    )
                                }
                            }



                            when (state) {
                                is ViewStates.Error -> {
                                }

                                ViewStates.Loading -> {
                                }
                                ViewStates.EMPTY->{

                                }



                                ViewStates.Default -> {
                                }

                                ViewStates.Reload -> {

                                }
                                is ViewStates.Success -> {

                                    scope.launch {

                                        delay(1000)

                                        when (events) {
                                            CheckVersionEvent.Default -> {


                                            }

                                            CheckVersionEvent.ForceUpdate -> {
                                                showVersionDialog = true

                                            }

                                            CheckVersionEvent.NormalUpdate -> {
                                                showVersionDialog = true
                                            }

                                            CheckVersionEvent.InvalidToken -> {

                                            }
                                            CheckVersionEvent.OkVersion -> {
                                                viewModel.disableSMSListener()
                                                navigator.popAll()
                                                navigator.push(mainScreen)


                                            }

                                        }

                                        println("eventttttii ${events}")
                                    }




                                }

                                is ViewStates.UnAuthorized -> {

                                }
                            }

                            Text(text =
                            buildAnnotatedString {
                                val messageEnterCode = stringResource(MR.strings.enter_verification_code) + " " + phoneNumberState

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

                                    }
                                }

                            }
                            )

                            Spacer(modifier = Modifier.height(spacing3X))
                            AuthVerificationCodeRow(otpText = smsCode, onOtpTextChange = { otp, boolean ->
                                viewModel.updateOtp(otp)
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
                                if (smsCode.length == 6) {
                                    Napier.log(LogLevel.ASSERT,"dadsd",message= smsCode)
                                    viewModel.verify(smsCode)
                                }
                            }
                        }
                    }
            }
        )
    }
}
