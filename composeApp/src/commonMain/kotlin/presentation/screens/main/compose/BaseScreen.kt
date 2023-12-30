package presentation.screens.main.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.irancell.nwg.wfm.presentation.components.*
import presentation.theme.backgroundBackground3
import com.irancell.nwg.wfm.presentation.theme.radius

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BaseScreen(
    scaffoldState : BottomSheetScaffoldState,
    title: String  ,
    hasDrawer : Boolean = false,
    content: @Composable () -> Unit ={},
    topBar : @Composable () -> Unit ={},
    drawerContent : @Composable () ->  Unit = {},
    bottomSheetHasHeader : Boolean = true,
    bottomSheetTitle  : String = ""  ,
    bottomSheetContent : @Composable (bottomSheetState: BottomSheetState) -> Unit = {},
    bottomBarBottomSheetContent : @Composable (bottomSheetState: BottomSheetState) -> Unit = {},
    onCloseBottomSheet : () -> Unit = {}

) {
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
                    CustomBottomSheet(scaffoldState.bottomSheetState,hasHeader =  bottomSheetHasHeader,title = bottomSheetTitle, content = {
                        bottomSheetContent(scaffoldState.bottomSheetState)
                    }, onClose = {
                        onCloseBottomSheet()
                    }, bottomBar = {
                        bottomBarBottomSheetContent(scaffoldState.bottomSheetState)
                    })
                }
            ) {
                content()
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
            content()
        }
    }
    }
}