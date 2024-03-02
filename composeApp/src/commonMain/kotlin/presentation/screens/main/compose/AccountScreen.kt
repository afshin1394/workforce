package presentation.screens.main.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import presentation.components.MenuItemsTopBar
import com.irancell.nwg.wfm.presentation.screens.main.components.ProfileInfoComponent
import com.irancell.nwg.wfm.presentation.theme.*
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.MR
import org.koin.compose.koinInject
import presentation.screens.main.viewmodel.AccountScreenVM

import presentation.theme.body_large
import presentation.theme.surfaceInputReadOnly

class AccountScreen(private val title: String) : Screen {

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AccountScreenVM = koinInject()
        val profileDomain = viewModel.profileDomain.collectAsState()
        val roleDomain = viewModel.roles.collectAsState()

        BaseScreen(
            viewModel = viewModel,
            scaffoldState = scaffoldState,
            title = stringResource(MR.strings.account_info),
            topBar = {
                MenuItemsTopBar(title) {
                    navigator.pop()

                }
            },
            content = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.padding(vertical = spacing2X))
                    Image(
                        painter = painterResource(MR.images.ic_avatar),
                        contentDescription = "",
                        modifier = Modifier
                            .width(120.dp)
                            .height(120.dp)
                    )
                    Spacer(modifier = Modifier.padding(vertical = spacing2X))

                    Column(
                        Modifier.padding(horizontal = spacing2X),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = stringResource(MR.strings.name), style = body_large)
                        Spacer(modifier = Modifier.padding(vertical = spacing05X))
                        ProfileInfoComponent(
                            "${profileDomain.value.firstName}  ${profileDomain.value.lastName}" ,
                            MR.images.user_account,
                            backgroundColor = surfaceInputReadOnly
                        )
                        Spacer(modifier = Modifier.padding(vertical = spacing1X))

                        Text(text = stringResource(MR.strings.expertise), style = body_large)
                        Spacer(modifier = Modifier.padding(vertical = spacing05X))

                        ProfileInfoComponent(
                            roleDomain.value,
                            MR.images.expertise,
                            backgroundColor = surfaceInputReadOnly
                        )
                        Spacer(modifier = Modifier.padding(vertical = spacing1X))

                        Text(text = stringResource(MR.strings.phone_number), style = body_large)
                        Spacer(modifier = Modifier.padding(vertical = spacing05X))
                        ProfileInfoComponent(
                            profileDomain.value.phoneNumber.toString(),
                            MR.images.phone,
                            backgroundColor = surfaceInputReadOnly
                        )
                        Spacer(modifier = Modifier.padding(vertical = spacing1X))

                        Text(text = stringResource(MR.strings.email), style = body_large)
                        Spacer(modifier = Modifier.padding(vertical = spacing05X))
                        ProfileInfoComponent(
                            profileDomain.value.user?.email ?: "",
                            MR.images.mail1,
                            backgroundColor = surfaceInputReadOnly
                        )
                        Spacer(modifier = Modifier.padding(vertical = spacing1X))
                    }

                }
            }
        )
    }
}