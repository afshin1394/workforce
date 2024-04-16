package presentation.screens.main.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.irancell.nwg.wfm.presentation.components.*
import presentation.theme.backgroundBackground3
import com.irancell.nwg.wfm.presentation.theme.radius
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.GPS
import irancell.nwg.wfm.provideAppContext

import utils.BaseViewModel
import utils.ViewStates
import androidx.compose.material3.CircularProgressIndicator
import cafe.adriel.voyager.navigator.OnBackPressed
import irancell.nwg.wfm.BackButtonHandler

import utils.GpsState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T : BaseViewModel> BaseScreen(
    viewModel: T,
    scaffoldState: BottomSheetScaffoldState,
    title: String,
    hasDrawer: Boolean = false,
    content: @Composable (snackBarHost: SnackbarHostState) -> Unit = {},
    topBar: @Composable () -> Unit = {},
    drawerContent: @Composable () -> Unit = {},
    bottomSheetHasHeader: Boolean = true,
    bottomSheetTitle: String = "",
    bottomSheetContent: @Composable (bottomSheetState: BottomSheetState) -> Unit = {},
    bottomBarBottomSheetContent: @Composable (bottomSheetState: BottomSheetState) -> Unit = {},
    onCloseBottomSheet: () -> Unit = {},
    onBackPressed: () -> Unit={}

) {

    val state by viewModel.state.collectAsState()
    val gpsState by viewModel.gpsState.collectAsState()
    Napier.log(LogLevel.ASSERT, "BaseScreen", message = state.toString())




    Box(
        modifier = Modifier
            .background(color = backgroundBackground3)
            .fillMaxSize()
    ) {
        if (hasDrawer) {
            BottomSheetScaffold(modifier = Modifier.background(color = backgroundBackground3),
                scaffoldState = scaffoldState,
                topBar = {
                    topBar()
                },

                drawerContent = {
                    drawerContent()
                },


                sheetPeekHeight = 0.dp,
                sheetGesturesEnabled = false,
                sheetShape = RoundedCornerShape(topEnd = radius, topStart = radius),
                sheetContent = {
                    CustomBottomSheet(
                        scaffoldState.bottomSheetState,
                        hasHeader = bottomSheetHasHeader,
                        title = bottomSheetTitle,
                        content = {
                            bottomSheetContent(scaffoldState.bottomSheetState)
                        },
                        onClose = {
                            onCloseBottomSheet()
                        },
                        bottomBar = {
                            bottomBarBottomSheetContent(scaffoldState.bottomSheetState)
                        })
                }
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background( backgroundBackground3)

                ) {
                    content(scaffoldState.snackbarHostState)
                    BackButtonHandler.backPress( onBackPressed = {
                        println("checkkkkvalueeee")
                        onBackPressed()


                    })
                    when(gpsState){
                        GpsState.Default -> {

                        }
                        GpsState.Disabled -> {
                            GPS.enableGpsDialog(provideAppContext())
                        }
                        GpsState.Enabled -> {

                        }
                    }
                    when (state) {
                        ViewStates.Default -> {

                        }

                        is ViewStates.Error -> {
                            val errorMessage = stringResource((state as ViewStates.Error).message)

                            Napier.log(LogLevel.INFO, tag = "fkpekfpw", message = errorMessage)
                            LaunchedEffect(Unit) {
                                scaffoldState.snackbarHostState.showSnackbar(message = errorMessage)

                                viewModel.updateState(ViewStates.Default)

                            }
                        }

                        ViewStates.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }


                        is ViewStates.Success -> {
                            viewModel.updateState(ViewStates.Default)
                        }

                        ViewStates.Reload -> {

                        }
                    }
                }
            }
        } else {
            BottomSheetScaffold(modifier = Modifier.background(color = backgroundBackground3),
                scaffoldState = scaffoldState,
                topBar = {
                    topBar()
                },
                sheetPeekHeight = 0.dp,
                sheetGesturesEnabled = false,
                sheetShape = RoundedCornerShape(topEnd = radius, topStart = radius),
                sheetContent = {
                    CustomBottomSheet(
                        scaffoldState.bottomSheetState,
                        hasHeader = bottomSheetHasHeader,
                        title = bottomSheetTitle,
                        content = {
                            bottomSheetContent(scaffoldState.bottomSheetState)
                        },
                        onClose = {
                            onCloseBottomSheet()
                        },
                        bottomBar = {
                            bottomBarBottomSheetContent(scaffoldState.bottomSheetState)
                        })
                }
            ) {


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background( backgroundBackground3)



                ) {
                    content(scaffoldState.snackbarHostState)

                    BackButtonHandler.backPress( onBackPressed = {
                        println("checkkkkvalueeee")
                        onBackPressed()


                    })


                    when(gpsState){
                        GpsState.Default -> {

                        }
                        GpsState.Disabled -> {
                            GPS.enableGpsDialog(provideAppContext())
                        }
                        GpsState.Enabled -> {

                        }
                    }

                    when (state) {
                        ViewStates.Default -> {

                        }

                        is ViewStates.Error -> {
                            val errorMessage = stringResource((state as ViewStates.Error).message)
                            LaunchedEffect(Unit) {
                                scaffoldState.snackbarHostState.showSnackbar(message = "$errorMessage")
                                viewModel.updateState(ViewStates.Default)

                            }
                            Napier.log(
                                LogLevel.INFO,
                                tag = "fkpekfpw",
                                message = errorMessage.toString()
                            )
                        }

                        ViewStates.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }



                        is ViewStates.Success -> {
                            viewModel.updateState(ViewStates.Default)
                        }

                        ViewStates.Reload -> {

                        }
                    }


                }
            }
        }

    }

}