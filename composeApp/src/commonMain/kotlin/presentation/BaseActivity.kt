//package com.irancell.nwg.wfm.presentation
//
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavBackStackEntry
//import androidx.navigation.NavController
//import com.irancell.nwg.wfm.presentation.components.CustomSearchBar
//import presentation.components.CustomTopAppBar
//import com.irancell.nwg.wfm.presentation.model.View
//import com.irancell.nwg.wfm.presentation.nav.NestedNavigation
//
//abstract class BaseActivity(private val view: View) : ComponentActivity() {
//    open fun onSearch(searchQuery: String) {
//
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//           NestedNavigation()
//        }
//    }
//
//    @Composable
//    fun ActionBar(
//        onMenuClick: () -> Unit
//    ) {
//        if (view.hasAppBar == true)
//            CustomTopAppBar() {
//                onMenuClick()
//            }
//    }
//
//
//    @Composable
//    fun search() {
//        if (view.hasSearch == true) {
//            CustomSearchBar(textFieldState = view.searchState, updatedText = {
//                onSearch(searchQuery = it)
//            })
//        }
//    }
//
//    @Composable
//    inline fun <reified T : ViewModel> NavBackStackEntry.SharedViewModel(navController: NavController): T {
//        val navGraphRoute = destination.parent?.route ?: return viewModel()
//        val parentEntry = remember(this) {
//            navController.getBackStackEntry(navGraphRoute)
//        }
//        return viewModel(parentEntry)
//
//    }
//
//
//}
//
