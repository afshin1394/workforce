package presentation.screens.main.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.irancell.nwg.wfm.presentation.components.*
import presentation.model.ItemComponentModel
import com.irancell.nwg.wfm.presentation.model.SelectableItem
import com.irancell.nwg.wfm.presentation.model.SelectableItemStringResource
import com.irancell.nwg.wfm.presentation.screens.main.components.SwitchItem
import presentation.screens.main.events.SettingEvent
import com.irancell.nwg.wfm.presentation.screens.main.viewmodel.SettingScreenVM
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import irancell.nwg.wfm.IntentHandler
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.getSharedPref
import irancell.nwg.wfm.provideAppContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import presentation.theme.subtleDefault
import presentation.theme.surfaceDefault
import presentation.theme.textBrand
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import presentation.components.MenuItemsTopBar

import presentation.screens.main.components.SelectableSingleLanguageItemComponentStringResource
import presentation.screens.main.components.SelectableSingleModeAppItemComponentStringResource
import presentation.screens.main.components.SelectableSingleUpdateTypeItemComponentStringResource
import utils.Language
import utils.ModeApp
import utils.SelectLanguage
import utils.UpdateTaskListTypes
import utils.UpdateType
import utils.isRunningGPS

@OptIn(ExperimentalMaterialApi::class)
class SettingsScreen(
) : Screen {
    @Composable
    override fun Content() {
        val scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()
        val viewModel: SettingScreenVM = koinInject()
        val events by viewModel.events
        val navigator = LocalNavigator.currentOrThrow


        val scope = rememberCoroutineScope()

        val bottomSheetTitle: String =
            when (events) {
                SettingEvent.Default -> {
                    ""
                }

                SettingEvent.ChangeLanguage -> {
                    stringResource(MR.strings.language)
                }

                SettingEvent.ChangeMode -> {
                    stringResource(MR.strings.connection_status)
                }
                SettingEvent.UpdateTaskListType->{
                    stringResource(MR.strings.update_task_list_types)
                }


            }

        BaseScreen(viewModel = viewModel, title = stringResource(MR.strings.settings), topBar = {
            MenuItemsTopBar(stringResource(MR.strings.settings)) {
                navigator.pop()
            }

        },
            bottomSheetTitle = bottomSheetTitle,

            bottomSheetContent = {
                when (events) {
                    SettingEvent.ChangeLanguage -> {
                        ChangeLanguageBottomSheetComponent(
                            list = viewModel.mutableChangeLanguageOptions,

                            onItemSelected = { index, selectableItem ->

                                scope.launch(Dispatchers.Main) {

                                    when (index) {
                                        0 -> {
                                            getSharedPref().put(Language, "en")

                                        }

                                        1 -> {
                                            getSharedPref().put(Language, "fa")

                                        }
                                    }

                                    getSharedPref().put(SelectLanguage, true)
                                    getSharedPref().put(isRunningGPS, false)


                                    delay(1000)
                                    IntentHandler(provideAppContext())


                                }
                            })
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }

                    SettingEvent.ChangeMode -> {

                        ChangeModeBottomSheetComponent(
                            list = viewModel.mutableChangeModeOptions,
                            onItemSelected = { index, selectableItem ->

                                scope.launch(Dispatchers.Main) {

                                    when (index) {
                                        0 -> {
                                            getSharedPref().put(ModeApp, "on")
                                            viewModel.events.value = SettingEvent.Default

                                        }

                                        1 -> {
                                            getSharedPref().put(ModeApp, "of")
                                            viewModel.events.value = SettingEvent.Default

                                        }

                                    }


                                }
                            })
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }

                    }
                    SettingEvent.UpdateTaskListType->{


                        ChangeUpdateTypeBottomSheetComponent(
                            list = viewModel.mutableChangeUpdateTypeOptions,
                            onItemSelected = { index, selectableItem ->

                                scope.launch(Dispatchers.Main) {

                                    when (index) {
                                        0 -> {
                                            getSharedPref().put(UpdateType, UpdateTaskListTypes.Auto)
                                            viewModel.events.value = SettingEvent.Default

                                        }

                                        1 -> {
                                            getSharedPref().put(UpdateType, UpdateTaskListTypes.Manual)
                                            viewModel.events.value = SettingEvent.Default

                                        }

                                    }


                                }
                            })
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
            }, content = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    val tagSelectLanguage = if (getSharedPref().getString(Language) == "fa") MR.strings.farsi
                    else MR.strings.english
                    ItemComponent(itemComponentModel = ItemComponentModel(
                        text = stringResource(MR.strings.language),
                        hasTag = true,
                        color = Color.Transparent,
                        textTag = stringResource(tagSelectLanguage),
                        textTagColor = textBrand,
                        tagColor = subtleDefault
                    ), modifier = Modifier.clickable {
                        viewModel.events.value = SettingEvent.ChangeLanguage
                    })
             /*       val tagSelectMode = if (getSharedPref().getString(ModeApp) == "on") MR.strings.online
                    else MR.strings.offline
                    ItemComponent(itemComponentModel = ItemComponentModel(
                        text = stringResource(MR.strings.connection_status),
                        hasTag = true,
                        color = Color.Transparent,
                        textTag = stringResource(tagSelectMode),
                        textTagColor = textBrand,
                        tagColor = subtleDefault
                    ), modifier = Modifier.clickable {
                        viewModel.events.value = SettingEvent.ChangeMode
                    })*/

                    val tagSelectUpdateType = if (getSharedPref().getString(UpdateType) == UpdateTaskListTypes.Auto) MR.strings.auto
                    else MR.strings.manual
                    ItemComponent(itemComponentModel = ItemComponentModel(
                        text = stringResource(MR.strings.update_task_list_types),
                        hasTag = true,
                        color = Color.Transparent,
                        textTag = stringResource(tagSelectUpdateType),
                        textTagColor = textBrand,
                        tagColor = subtleDefault
                    ), modifier = Modifier.clickable {
                        viewModel.events.value = SettingEvent.UpdateTaskListType
                    })

                }

            }, scaffoldState = scaffoldState,
            onBackPressed = {

                if (viewModel.events.value == SettingEvent.Default) {
                    navigator.pop()

                } else {
                    viewModel.events.value = SettingEvent.Default

                }


            }
        )

    }

}


@Composable
fun ChangeLanguageBottomSheetComponent(
    list: MutableList<SelectableItemStringResource>,
    onItemSelected: (Int, SelectableItemStringResource) -> Unit
) {
    SelectableSingleLanguageItemComponentStringResource(
        selectableItems = list,
        itemSelected = getSharedPref().getString(Language) ?: "",
        onOptionSelected = { index, selectableItem ->
            onItemSelected(index, selectableItem)
        })
}

@Composable
fun ChangeModeBottomSheetComponent(
    list: MutableList<SelectableItemStringResource>,
    onItemSelected: (Int, SelectableItemStringResource) -> Unit
) {
    SelectableSingleModeAppItemComponentStringResource(
        selectableItems = list,
        itemSelected = getSharedPref().getString(ModeApp) ?: "",
        onOptionSelected = { index, selectableItem ->
            onItemSelected(index, selectableItem)
        })
}

@Composable
fun ChangeUpdateTypeBottomSheetComponent(
    list: MutableList<SelectableItemStringResource>,
    onItemSelected: (Int, SelectableItemStringResource) -> Unit
) {
    SelectableSingleUpdateTypeItemComponentStringResource(
        selectableItems = list,
        itemSelected = getSharedPref().getString(UpdateType) ?: "",
        onOptionSelected = { index, selectableItem ->
            onItemSelected(index, selectableItem)
        })
}