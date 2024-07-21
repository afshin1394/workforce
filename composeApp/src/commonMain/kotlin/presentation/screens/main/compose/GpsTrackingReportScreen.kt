package presentation.screens.main.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import presentation.components.MenuItemsTopBar
import com.irancell.nwg.wfm.presentation.theme.spacing15X

import org.koin.compose.koinInject
import presentation.screens.main.components.LocationItem
import presentation.screens.main.viewmodel.GpsTrackingReportScreenVM
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.irancell.nwg.wfm.presentation.components.ItemComponent
import database.entity.GeneralLocationEntity
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR

import irancell.nwg.wfm.mapView
import kotlinx.coroutines.launch
import presentation.model.ItemComponentModel
import presentation.theme.mediumDivider
import presentation.theme.subtleDefault
import presentation.theme.surfaceDefault
import presentation.theme.textBrand
import utils.ViewStates

class GpsTrackingReportScreen(

) : Screen {
    companion object {
        lateinit var gneralLocs: List<GeneralLocationEntity>
    }

    @ExperimentalMaterial3Api
    @Composable
    override fun Content() {
        var lastshowmap by remember { mutableStateOf(0) }
        var showMap by remember { mutableStateOf(false) }
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: GpsTrackingReportScreenVM = koinInject()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val generalLocations by rememberUpdatedState(viewModel.generalLocationList)
        val scope = rememberCoroutineScope()
        Scaffold(topBar = {
            MenuItemsTopBar(stringResource(MR.strings.gps_tracker)) {

                navigator.pop()
//            navHostController.navigate(Screen.Main.route) {
//                popUpTo(Screen.Main.route) {
//
//                }
//            }
            }

        }, snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) {
            gneralLocs = generalLocations

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                ItemComponent(itemComponentModel = ItemComponentModel(
                    text = stringResource(MR.strings.map),
                    hasTag = false,
                    hasImage = true,
                    imageResource = MR.images.map,
                    color = surfaceDefault,
                    textTag = "English",
                    textTagColor = textBrand,
                    tagColor = subtleDefault
                ), modifier = Modifier.clickable {
                    showMap = true

                })
                Napier.log(
                    LogLevel.ASSERT,
                    tag = "checkcki",
                    message = "current $showMap  last $lastshowmap"
                )
                if (showMap) {
                    mapView(generalLocations)
                    showMap = false
                }
                when (state) {
                   is ViewStates.Error -> {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "${MR.strings.error_gps_tracker}!",
                                duration = SnackbarDuration.Short,
                            )
                        }
                    }

                    ViewStates.Loading -> {
                        CircularProgressIndicator()
                    }

                    is ViewStates.Success -> {
                        val message = stringResource(MR.strings.success)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = message,
                                duration = SnackbarDuration.Short,
                            )
                        }
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(spacing15X),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items((generalLocations)) { item ->
                                LocationItem(
                                    generalLocation = item,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                Divider(
                                    thickness = 1.dp,
                                    color = mediumDivider,
                                    modifier = Modifier.fillMaxWidth(.95f)
                                )
                            }
                        }
                    }

                    ViewStates.Default -> {

                    }

                    ViewStates.Reload -> {

                    }

                    is ViewStates.UnAuthorized -> {

                    }
                }
            }
        }
    }
}

