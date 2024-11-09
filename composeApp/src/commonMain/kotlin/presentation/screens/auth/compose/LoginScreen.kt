package presentation.screens.auth.compose


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.screens.auth.AuthValidation
import presentation.screens.auth.components.AuthAlertText
import presentation.screens.auth.components.AuthAlertTextItem
import presentation.screens.auth.components.AuthButton
import presentation.screens.auth.components.AuthButtonItem
import presentation.screens.auth.components.AuthTextField
import presentation.screens.auth.components.AuthTextFieldItem
import presentation.screens.auth.viewmodel.LoginScreenVM
import presentation.screens.main.compose.BaseScreen
import presentation.theme.backgroundBackground3

import presentation.theme.body_large
import presentation.theme.error_5
import utils.ViewStates

class LoginScreen() : Screen {

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: LoginScreenVM = koinInject()
        val state by viewModel.state.collectAsState()
        val scope = rememberCoroutineScope()
        val email by viewModel.email.collectAsState()
        val password by viewModel.password.collectAsState()
        val scaffoldState = rememberBottomSheetScaffoldState()
        val authValidationState by viewModel.authValidationState.collectAsState()
        val verifyScreen =
            rememberScreen(
                presentation.nav.Screen.Auth.Verify
            )


        BaseScreen(
            viewModel = viewModel,
            scaffoldState = scaffoldState,
            title = "notStartService",
            onBackPressed = { navigator.pop() },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = backgroundBackground3),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    when (state) {
                        is ViewStates.Success -> {
                            scope.launch {
                                navigator.push(verifyScreen)
                            }
                        }

                        else -> {}
                    }

                    Image(
                        painter = dev.icerock.moko.resources.compose.painterResource(MR.images.ic_i_ticket),
                        contentDescription = "ic_wfm",
                        modifier = Modifier
                            .width(72.dp)
                            .height(72.dp)
                            .weight(2f)
                            .wrapContentSize()
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .weight(4f)
                            .padding(spacing2X)
                    ) {
                        Text(
                            text = stringResource(MR.strings.login_mtn_account),
                            style = body_large
                        )
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

                        if (authValidationState == AuthValidation.NoEmail) {
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
                        if (authValidationState == AuthValidation.NoPassword) {
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
                        if (authValidationState == AuthValidation.NotEnoughChar) {
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
//                            if (viewModel.performLogin(email, password)) {
                            Napier.log(
                                LogLevel.ASSERT,
                                "email & password",
                                message = "email ${email} password ${password}"
                            )
                            viewModel.login(email, password)
//                            }
                        }
                    }
                }
            }
        )
    }
}




