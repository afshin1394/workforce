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
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.screens.auth.components.*
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import org.koin.compose.koinInject
import presentation.screens.auth.components.AuthAlertText
import presentation.screens.auth.components.AuthAlertTextItem
import presentation.screens.auth.viewmodel.VerifyScreenVM
import presentation.screens.main.compose.BaseScreen
import presentation.screens.splash.compose.SplashScreen
import presentation.theme.backgroundBackground3
import presentation.theme.textBrand
import utils.ViewStates

class VerifyScreen(private val phoneNumber : String = "") : Screen {
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





        BaseScreen(
            viewModel = viewModel,
            scaffoldState = scaffoldState,
            title = "",
            content = {
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

                            when (state) {
                                is ViewStates.Error -> {
                                }

                                ViewStates.Loading -> {
                                }

                                ViewStates.Success -> {
                                    viewModel.disableSMSListener()
                                    navigator.popUntil { it == SplashScreen() }
                                    navigator.push(mainScreen)
                                }

                                ViewStates.Default -> {
                                }
                                ViewStates.NoGps -> {

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
