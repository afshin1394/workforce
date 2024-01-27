package presentation.screens.main.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.components.*
import presentation.model.ItemComponentModel
import com.irancell.nwg.wfm.presentation.model.SelectableItem
import com.irancell.nwg.wfm.presentation.screens.main.components.SwitchItem
import presentation.screens.main.events.SettingEvent
import com.irancell.nwg.wfm.presentation.screens.main.viewmodel.SettingScreenVM
import presentation.theme.subtleDefault
import presentation.theme.surfaceDefault
import presentation.theme.textBrand
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterialApi::class)
class SettingsScreen (
  private val  title: String
) : Screen {
    @Composable
    override fun Content() {
        val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()
        val viewModel = remember { SettingScreenVM() }
        val events by viewModel.events
        val navigator = LocalNavigator.currentOrThrow


        val scope = rememberCoroutineScope()

        val bottomSheetTitle: String =
            when (events) {
                SettingEvent.Default -> {
                    ""
                }

                SettingEvent.ChangeLanguage -> {
                    "Language"
                }

            }

        BaseScreen(title = "Settings", topBar = {
            MenuItemsTopBar("Settings") {
                navigator.pop()
            }

        },
            bottomSheetTitle = bottomSheetTitle,

            bottomSheetContent = {
                when (events) {
                    SettingEvent.ChangeLanguage -> {
                        ChangeLanguageBottomSheetComponent(viewModel.mutableChangeLanguageOptions) { index, selectableItem ->
                            {

                            }
                        }
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }

                    SettingEvent.Default -> {
                        scope.launch {
                            scaffoldState.bottomSheetState.collapse()
                        }
                    }
                }
            }, onCloseBottomSheet = {
                viewModel.events.value = SettingEvent.Default
            }, bottomBarBottomSheetContent = {
                when (events) {

                    SettingEvent.Default -> {

                    }

                    SettingEvent.ChangeLanguage -> {

                    }
                }

            }, content = {
                Column {
                    ItemComponent(itemComponentModel = ItemComponentModel(
                        text = "Language",
                        hasTag = true,
                        color = surfaceDefault,
                        textTag = "English",
                        textTagColor = textBrand,
                        tagColor = subtleDefault
                    ), modifier = Modifier.clickable {
                        viewModel.events.value = SettingEvent.ChangeLanguage
                    })
                    ItemComponent(itemComponentModel = ItemComponentModel(
                        text = "Help and Support", hasTag = false,
                        color = surfaceDefault
                    ), modifier = Modifier.clickable {

                    })
                    SwitchItem(text = "Dark mode", false) {

                    }
                }

            }, scaffoldState = scaffoldState
        )

    }

}





@Composable
fun ChangeLanguageBottomSheetComponent(
    list: MutableList<SelectableItem>,
    onItemSelected: (Int, SelectableItem) -> Unit
) {
    SelectableComponent(
        selectableItems = list,
        hasSearch = false,
        onOptionSelected = { index, selectableItem ->
            onItemSelected(index, selectableItem)
        })
}