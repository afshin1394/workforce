package presentation.screens.cr.compose

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import org.koin.compose.koinInject
import presentation.components.MenuItemsTopBar
import presentation.screens.cr.components.CRListScreen
import presentation.screens.cr.viewmodel.CRScreenVM
import presentation.screens.main.compose.BaseScreen
import utils.ViewStates
import kotlin.time.Duration

class CRScreen(private val title : String) : Screen {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val viewModel: CRScreenVM = koinInject()
        val scaffoldState = rememberBottomSheetScaffoldState()

        BaseScreen(
            viewModel = viewModel,
            scaffoldState = scaffoldState,

            onCloseBottomSheet = {

            },
            topBar = {
                MenuItemsTopBar(title) {
                   viewModel.getAllChangeRequest()
                }
            },
            title = title,

            content = {


                CRListScreen(crDomains =  ArrayList(viewModel.crDomainList.toList())){

                }

            },

        )

    }
}