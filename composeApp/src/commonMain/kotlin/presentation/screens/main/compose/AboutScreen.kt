package presentation.screens.main.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

import com.irancell.nwg.wfm.presentation.components.*
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.MR
import org.koin.compose.koinInject
import presentation.components.CustomButton
import presentation.components.CustomButtonData
import presentation.components.MenuItemsTopBar
import presentation.screens.main.viewmodel.AboutScreenVM

import presentation.theme.body_large
import presentation.theme.subtleDefault
import presentation.theme.surfaceBrandDefault
import presentation.theme.textInverse
import presentation.theme.textPrimary

@OptIn(ExperimentalMaterialApi::class)
class AboutScreen(
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()
        val viewModel: AboutScreenVM = koinInject()

        BaseScreen(
            viewModel = viewModel,
            title = stringResource(MR.strings.about_application),
            scaffoldState = scaffoldState,
            topBar = {
                MenuItemsTopBar(stringResource(MR.strings.about_application)) {
                    navigator.pop()
                }
            },
            content = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    Spacer(modifier = Modifier.padding(30.dp))
                    Image(painter = painterResource(MR.images.ic_i_ticket), contentDescription = "wfm", modifier = Modifier
                        .width(72.dp)
                        .height(72.dp))
                    Spacer(modifier = Modifier.padding(spacing25X))

                    Column(
                        modifier = Modifier.padding(horizontal = spacing3X),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "${stringResource(MR.strings.current_version)}:",
                                style = body_large,
                                modifier = Modifier.weight(.8f),
                            )
                            ChipsView(
                                chipsItem = ChipsItem(
                                    hasBorder = false,
                                    text = viewModel.currentVersion.value,
                                    chipsColor = subtleDefault,
                                    borderWidth = 0.dp,
                                    chipsRadius = radius2XLarge,
                                    textColor = textPrimary,
                                    textStyle = body_large
                                ), modifier = Modifier.weight(.2f)
                            )
                        }
                        Spacer(modifier = Modifier.padding(spacing15X))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "${stringResource(MR.strings.latest_version)}:",
                                style = body_large,
                                modifier = Modifier.weight(.8f),

                                )
                            ChipsView(
                                chipsItem = ChipsItem(
                                    hasBorder = false,
                                    text = viewModel.updateVersion.value,
                                    chipsColor = subtleDefault,
                                    borderWidth = 0.dp,
                                    chipsRadius = radius2XLarge,
                                    textColor = textPrimary,
                                    textStyle = body_large
                                ), modifier = Modifier.weight(.2f)
                            )
                        }

                        Spacer(modifier = Modifier.padding(spacing25X))

                        Text(
                            text = stringResource(MR.strings.description_for_update),
                            style = body_large
                        )
                        Spacer(modifier = Modifier.padding(spacing25X))
                        CustomButton(
                            customButtonData = CustomButtonData(
                                title = stringResource(MR.strings.update),
                                textColor = textInverse,
                                backgroundColor = surfaceBrandDefault
                            )
                        )
                    }
                    Spacer(modifier = Modifier.padding(vertical = 30.dp))

                }
            }
            ,
            onBackPressed = {
                navigator.pop()
            }
        )

    }
}


