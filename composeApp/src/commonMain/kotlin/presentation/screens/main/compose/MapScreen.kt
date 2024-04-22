package presentation.screens.main.compose
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.koinInject
import presentation.components.MenuItemsTopBar
import presentation.screens.main.viewmodel.MapVM



@OptIn(ExperimentalMaterialApi::class)
class MapScreen (private  val title:String) : Screen {


    @Composable
    override fun Content() {

        val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: MapVM = koinInject()
        val scope = rememberCoroutineScope()

        BaseScreen(
            viewModel = viewModel,

            title = "MapViw",
            scaffoldState = scaffoldState,
            topBar = {
                MenuItemsTopBar("MapView") {
                    navigator.pop()
                }
            },

            content = {

                Column(
                    modifier = Modifier.fillMaxHeight().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {



                }
            }

        )

    }
}