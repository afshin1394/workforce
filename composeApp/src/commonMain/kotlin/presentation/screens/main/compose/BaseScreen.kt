package presentation.screens.main.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import utils.BaseViewModel
import utils.ViewStates
import androidx.compose.material3.CircularProgressIndicator

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

    ) {

    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()
    Napier.log(LogLevel.ASSERT,"BaseScreen", message = "enableGPS")
    val snackbarHostState = remember { SnackbarHostState() }

    GPS.enableGps(provideAppContext(), disable =  {
        viewModel.updateState(ViewStates.NoGps)
    }, enabled =  {
        viewModel.updateState(ViewStates.Default)
    })

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
                ) {
                    content(snackbarHostState)

                    when(state){
                        ViewStates.Default -> {

                        }
                        is ViewStates.Error -> {
                            val errorMessage =  stringResource((state as ViewStates.Error).message)

                            Napier.log(LogLevel.INFO, tag = "fkpekfpw", message = errorMessage)
                            LaunchedEffect(Unit) {
                                scaffoldState.snackbarHostState.showSnackbar(message = errorMessage)

                                viewModel.updateState(ViewStates.Default)

                            }
                        }
                        ViewStates.Loading -> {

                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))


                        }
                        ViewStates.NoGps -> {
                            GPS.enableGps(provideAppContext(), disable =  {
                                scope.launch {
                                    viewModel.updateState(ViewStates.Loading)
                                    delay(100)
                                    viewModel.updateState(ViewStates.NoGps)
                                }
                            }, enabled = {
                                viewModel.updateState(ViewStates.Default)
                            })


                        }
                        ViewStates.Success -> {
                                viewModel.updateState(ViewStates.Default)


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

                ) {
                    content(snackbarHostState)

                    when(state){
                        ViewStates.Default -> {
//                            LaunchedEffect(Unit) {
//                                scaffoldState.snackbarHostState.showSnackbar(message = "sdasdad")
//
//                            }
                        }
                        is ViewStates.Error -> {
                            val errorMessage =  stringResource((state as ViewStates.Error).message)
                            LaunchedEffect(Unit) {
                                scaffoldState.snackbarHostState.showSnackbar(message = "$errorMessage")
                                viewModel.updateState(ViewStates.Default)

                            }
                            Napier.log(LogLevel.INFO, tag = "fkpekfpw", message = errorMessage.toString())

                        }
                        ViewStates.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                        }
                        ViewStates.NoGps -> {
                            GPS.enableGps(provideAppContext(), disable =  {
                                scope.launch {
                                    viewModel.updateState(ViewStates.Loading)
                                    delay(100)
                                    viewModel.updateState(ViewStates.NoGps)
                                }

                            }, enabled = {
                                viewModel.updateState(ViewStates.Default)
                            })

                        }
                        ViewStates.Success -> {
                                viewModel.updateState(ViewStates.Default)


                        }
                    }


                }
            }
        }

    }

}