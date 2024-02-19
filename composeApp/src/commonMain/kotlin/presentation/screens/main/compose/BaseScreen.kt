package presentation.screens.main.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
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
import com.irancell.nwg.wfm.presentation.theme.spacing2X
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import utils.BaseViewModel
import utils.ViewStates

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun< T : BaseViewModel> BaseScreen(
    viewModel: T,
    scaffoldState : BottomSheetScaffoldState,
    snackbarHostState: androidx.compose.material.SnackbarHostState =  remember { androidx.compose.material.SnackbarHostState() },
    title: String  ,
    hasDrawer : Boolean = false,
    content: @Composable (snackBarHost : androidx.compose.material.SnackbarHostState) -> Unit ={},
    topBar : @Composable () -> Unit ={},
    drawerContent : @Composable () ->  Unit = {},
    bottomSheetHasHeader : Boolean = true,
    bottomSheetTitle  : String = ""  ,
    bottomSheetContent : @Composable (bottomSheetState: BottomSheetState) -> Unit = {},
    bottomBarBottomSheetContent : @Composable (bottomSheetState: BottomSheetState) -> Unit = {},
    onCloseBottomSheet : () -> Unit = {},

) {
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()
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
                snackbarHost  = { SnackbarHost(hostState = snackbarHostState) },
                sheetPeekHeight = 0.dp,
                sheetGesturesEnabled = false,
                sheetShape = RoundedCornerShape(topEnd = radius, topStart = radius),
                sheetContent = {
                    CustomBottomSheet(scaffoldState.bottomSheetState,hasHeader =  bottomSheetHasHeader,title = bottomSheetTitle, content = {
                        bottomSheetContent(scaffoldState.bottomSheetState)
                    }, onClose = {
                        onCloseBottomSheet()
                    }, bottomBar = {
                        bottomBarBottomSheetContent(scaffoldState.bottomSheetState)
                    })
                }
            ) {
                Napier.log(LogLevel.ASSERT,"viewState",message = state.toString())
                if (state is ViewStates.Error) {
                    val errorMessage = (state as ViewStates.Error).message
                    val mess = stringResource((state as ViewStates.Error).message)
                    Napier.log(LogLevel.INFO, tag = "fkpekfpw", message = errorMessage.toString())
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "${mess}!",
                            duration = SnackbarDuration.Short,
                        )
                    }
                    viewModel.updateState(ViewStates.Default)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    if (state is ViewStates.Loading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    content(snackbarHostState)
                }
            }
        }
    else{
        BottomSheetScaffold(modifier = Modifier.background(color = backgroundBackground3),
            scaffoldState = scaffoldState,
            topBar = {
                topBar()
            },
            sheetPeekHeight = 0.dp,
            sheetGesturesEnabled = false,
            sheetShape = RoundedCornerShape(topEnd = radius, topStart = radius),
            sheetContent = {
                CustomBottomSheet(scaffoldState.bottomSheetState, hasHeader =  bottomSheetHasHeader, title =  bottomSheetTitle, content = {
                    bottomSheetContent(scaffoldState.bottomSheetState)
                }, onClose = {
                    onCloseBottomSheet()
                }, bottomBar = {
                    bottomBarBottomSheetContent(scaffoldState.bottomSheetState)
                })
            }
        ) {
            Napier.log(LogLevel.ASSERT,"viewState",message = state.toString())

            if (state is ViewStates.Error) {
                val errorMessage = (state as ViewStates.Error).message

                Napier.log(LogLevel.INFO, tag = "fkpekfpw", message = errorMessage.toString())
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "${errorMessage}!",
                        duration = SnackbarDuration.Short,
                    )
                }
                viewModel.updateState(ViewStates.Default)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()

            ) {
                if (state is ViewStates.Loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                content(snackbarHostState)
            }
        }
    }
    }
}