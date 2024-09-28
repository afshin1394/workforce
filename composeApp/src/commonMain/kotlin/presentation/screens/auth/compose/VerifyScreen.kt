package presentation.screens.auth.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.components.bottomSheetDoubleActionBottomBarWithLoading
import com.irancell.nwg.wfm.presentation.components.bottomSingleActionComponentWithLoading
import com.irancell.nwg.wfm.presentation.screens.auth.components.*
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.HideKeyboard
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.openAppSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


import presentation.components.CustomDialogDoubleActionWithLoading
import presentation.components.CustomDialogWithLoading
import presentation.model.BottomSheetDoubleActionModel
import presentation.model.SingleButtonActionModel
import presentation.screens.auth.components.AuthAlertText
import presentation.screens.auth.components.AuthAlertTextItem
import presentation.screens.auth.viewmodel.VerifyScreenVM
import presentation.screens.main.compose.BaseScreen
import presentation.screens.splash.compose.SplashScreen
import presentation.screens.splash.events.CheckVersionEvent
import presentation.screens.splash.events.PermissionEvent
import presentation.theme.backgroundBackground3
import presentation.theme.body_large
import presentation.theme.surfaceBrandDefault
import presentation.theme.textBrand
import presentation.theme.textInverse
import presentation.theme.textInverseDisabled
import utils.ButtonState
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
        var events by viewModel.eventsVersion
        var buttonState by remember { mutableStateOf(ButtonState.IDLE) }
        val scope = rememberCoroutineScope()


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
            onBackPressed = {
                navigator.pop()
            },
            viewModel = viewModel,
            scaffoldState = scaffoldState,
            bottomSheetTitle = bottomSheetTitle,
            isShwCloseBtnBottomSheet = !(events == CheckVersionEvent.NormalUpdate ||events == CheckVersionEvent.ForceUpdate),
            title = stringResource(MR.strings.verify),

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

                    }

                    CheckVersionEvent.InvalidToken -> {

                    }
                }

            },


            bottomBarBottomSheetContent ={
                when (events) {

                    CheckVersionEvent.ForceUpdate -> {


                        HideKeyboard()


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



                    }

                    CheckVersionEvent.NormalUpdate -> {
                        HideKeyboard()

                            bottomSheetDoubleActionBottomBarWithLoading(
                                buttonState=buttonState,
                                BottomSheetDoubleActionModel(
                                    stringResource(MR.strings.cancel),
                                    Color.Transparent,
                                    textInverseDisabled,
                                    stringResource( MR.strings.download),
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



                    }

                    CheckVersionEvent.OkVersion -> {

                        navigator.popAll()
                        navigator.push(mainScreen)


                    }

                    CheckVersionEvent.Default -> {

                    }

                    CheckVersionEvent.InvalidToken -> {

                        navigator.popAll()
                        navigator.push(mainScreen)


                    }
                }

            },

            content = {
                    Column(
                        modifier = if (events==CheckVersionEvent.NormalUpdate ||events==CheckVersionEvent.ForceUpdate) Modifier.blur(7.dp) else Modifier
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
