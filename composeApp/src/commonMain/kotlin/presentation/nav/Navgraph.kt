package presentation.nav
import cafe.adriel.voyager.core.registry.screenModule
import presentation.screens.auth.compose.LoginScreen
import presentation.screens.auth.compose.VerifyScreen
import presentation.screens.main.compose.MainScreen
import presentation.screens.main.compose.AccountScreen
import com.irancell.nwg.wfm.presentation.screens.main.compose.NotificationScreen
import presentation.screens.splash.compose.SplashScreen
import presentation.screens.ticket_process.compose.TicketInfoScreen
import presentation.screens.main.compose.AboutScreen
import presentation.screens.main.compose.FormViewerScreen
import presentation.screens.main.compose.GpsTrackingReportScreen
import presentation.screens.main.compose.SettingsScreen
import presentation.screens.ticket_process.compose.TicketProcessScreen

//@file:OptIn(ExperimentalMaterialApi::class)
//package com.irancell.nwg.wfm.presentation.nav
//
//import android.app.Activity
//import android.content.Context
//import android.telephony.TelephonyManager
//import androidx.compose.animation.AnimatedContentTransitionScope
//import androidx.compose.animation.EnterTransition
//import androidx.compose.animation.ExitTransition
//import androidx.compose.animation.core.tween
//import androidx.compose.material.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.platform.LocalContext
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.*
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.navigation
//import androidx.navigation.compose.rememberNavController
//import com.irancell.nwg.wfm.ui.compose.AboutScreen
//import presentation.screens.main.compose.SettingsScreen
//import presentation.screens.auth.compose.LoginScreen
//import presentation.screens.auth.compose.VerifyScreen
//import presentation.screens.main.compose.MainScreen
//import presentation.screens.splash.compose.SplashScreen
//
//import com.irancell.nwg.wfm.presentation.screens.main.compose.AccountScreen
//import com.irancell.nwg.wfm.presentation.screens.main.compose.NotificationScreen
//import com.irancell.nwg.wfm.presentation.screens.main.viewmodel.presentation.screens.main.viewmodel.AboutScreenVM
//import presentation.screens.main.viewmodel.MainScreenVM
//import com.irancell.nwg.wfm.presentation.screens.main.viewmodel.SettingScreenVM
//import com.irancell.nwg.wfm.presentation.screens.ticket_process.compose.TicketInfoScreen
//import com.irancell.nwg.wfm.presentation.screens.ticket_process.viewModel.TicketProcessVM
//
//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun NestedNavigation() {
//
//
//
//    val navHostController = rememberNavController()
//    val scaffoldState =  rememberBottomSheetScaffoldState(
//        bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
//    )
//    NavHost(
//        navController = navHostController,
//        startDestination = Screen.Splash.route
//    ) {
//
//
//        composable(
//            Screen.Splash.route,
//            enterTransition = {
//               enterTransition()
//            },
//            exitTransition = {
//                exitTransition()
//            }) {
//            SplashScreen(navHostController)
//        }
//        authGraph(navHostController)
//
//        mainGraph(scaffoldState,navHostController)
//
//        ticketProcessGraph(scaffoldState,navHostController)
//
//    }
//}
//
//fun ticketProcessGraph(scaffoldState: BottomSheetScaffoldState,navHostController: NavHostController) {
//
//
//}
//
//
//private fun NavGraphBuilder.authGraph(navHostController : NavHostController) {
//    navigation(startDestination = Screen.Auth.Login.route, route = Screen.Auth.route) {
//        composable(
//            Screen.Auth.Login.route,
//            enterTransition = {
//                enterTransition()
//            },
//            exitTransition = {
//                exitTransition()
//            }
//            ) {
//            LoginScreen(navHostController)
//        }
//        composable(
//            Screen.Auth.Verify.route+"/{phoneNumber}",
//            enterTransition = {
//                slideIntoContainer(
//                    AnimatedContentTransitionScope.SlideDirection.Left,
//                    animationSpec = tween(700)
//                )
//            },
//            exitTransition = {
//                slideOutOfContainer(
//                    AnimatedContentTransitionScope.SlideDirection.Right,
//                    animationSpec = tween(700)
//                )
//            },
//            arguments = listOf(
//                navArgument("phoneNumber") {
//                    type = NavType.StringType
//                    defaultValue = ""
//                }
//            )
//        ) { backStackEntry->
//            VerifyScreen(navHostController,
//                backStackEntry.arguments?.getString("phoneNumber") ?: ""
//            )
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterialApi::class)
//private fun NavGraphBuilder.mainGraph(scaffoldState: BottomSheetScaffoldState,navHostController: NavHostController) {
//    navigation(startDestination = Screen.Main.MyTickets.route, route = Screen.Main.route) {
//        composable(
//            Screen.Main.MyTickets.route,
//            enterTransition = {
//               enterTransition()
//            },
//            exitTransition = {
//                exitTransition()
//            }) {
//
//            MainScreen(scaffoldState,navHostController, Screen.Main.MyTickets.route)
//        }
//        composable(
//            Screen.Main.AccountInfo.route,
//            enterTransition = {
//              enterTransition()
//            },
//            exitTransition = {
//               exitTransition()
//            }) {
//            val viewModel  = it.SharedViewModel<MainScreenVM>(navController = navHostController)
//
//            AccountScreen(viewModel,scaffoldState,navHostController, Screen.Main.AccountInfo.route)
//        }
//
//        composable(
//            Screen.Main.Settings.route,
//            enterTransition = {
//               enterTransition()
//            },
//            exitTransition = {
//               exitTransition()
//            }) {
//            val viewModel  = it.SharedViewModel<SettingScreenVM>(navController = navHostController)
//
//            SettingsScreen(viewModel,scaffoldState,navHostController, Screen.Main.Settings.route)
//        }
//        composable(
//            Screen.Main.About.route,
//            enterTransition = {
//
//                enterTransition()
//            },
//            exitTransition = {
//                exitTransition()
//            }) {
//            val viewModel  = it.SharedViewModel<presentation.screens.main.viewmodel.AboutScreenVM>(navController = navHostController)
//
//            AboutScreen(viewModel,scaffoldState,navHostController, Screen.Main.About.route)
//        }
//
//        composable(
//            Screen.Main.Notification.route,
//            enterTransition = {
//               enterTransition()
//            },
//            exitTransition = {
//                exitTransition()
//            }) {
//            NotificationScreen(scaffoldState,navHostController, Screen.Main.Notification.route)
//        }
//    }
//}
//
//
//
//@OptIn(ExperimentalMaterialApi::class)
//private fun NavGraphBuilder.ticketProcessGraph(scaffoldState: BottomSheetScaffoldState,navHostController: NavHostController) {
//    navigation(startDestination = Screen.TicketProcess.TicketInfo.route, route = Screen.TicketProcess.route) {
//        composable(
//            Screen.TicketProcess.TicketInfo.route,
//            enterTransition = {
//                enterTransition()
//            },
//            exitTransition = {
//                exitTransition()
//            }) {
//            val viewModel  = it.SharedViewModel<TicketProcessVM>(navController = navHostController)
//
//            TicketInfoScreen(viewModel,scaffoldState,navHostController, Screen.TicketProcess.TicketInfo.route)
//
//        }
//        composable(
//            Screen.TicketProcess.HSECheck1.route,
//            enterTransition = {
//                enterTransition()
//            },
//            exitTransition = {
//                exitTransition()
//            }) {
//
//        }
//
//        composable(
//            Screen.TicketProcess.Routing.route,
//            enterTransition = {
//                enterTransition()
//            },
//            exitTransition = {
//                exitTransition()
//            }) {
//
//        }
//        composable(
//            Screen.TicketProcess.HSECheck2.route,
//            enterTransition = {
//
//                enterTransition()
//            },
//            exitTransition = {
//                exitTransition()
//            }) {
//
//        }
//
//        composable(
//            Screen.TicketProcess.JobReport.route,
//            enterTransition = {
//                enterTransition()
//            },
//            exitTransition = {
//                exitTransition()
//            }) {
//        }
//
//        composable(
//            Screen.TicketProcess.WaitingForApproval.route,
//            enterTransition = {
//                enterTransition()
//            },
//            exitTransition = {
//                exitTransition()
//            }) {
//        }
//    }
//}
//
//
//
//@Composable
//inline fun <reified T : ViewModel> NavBackStackEntry.SharedViewModel(navController: NavController): T {
//    val navGraphRoute = destination.parent?.route ?: return viewModel()
//    val parentEntry = remember(this) {
//        navController.getBackStackEntry(navGraphRoute)
//    }
//    return viewModel(parentEntry)
//
//}
//
//
//internal fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition() : EnterTransition {
//   return this.slideIntoContainer(
//        AnimatedContentTransitionScope.SlideDirection.Left,
//        animationSpec = tween(700)
//    )
//}
//
//internal fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition() : ExitTransition {
//    return this.slideOutOfContainer(
//        AnimatedContentTransitionScope.SlideDirection.Left,
//        animationSpec = tween(700)
//    )
//}
//
//



   val featurePostsScreenModule =

        screenModule {
            register<Screen.Splash> {
                SplashScreen()
            }

            register<Screen.Auth.Login> {
                LoginScreen()
            }
            register<Screen.Auth.Verify> { provider ->
                VerifyScreen(provider.phoneNumber)
            }
            register<Screen.Main.Menu.MyTickets> {
                MainScreen()
            }
            register<Screen.Main.Menu.About> {
                AboutScreen()
            }
            register<Screen.Main.AccountInfo> {
                AccountScreen()
            }
            register<Screen.Main.Menu.Settings> {
                SettingsScreen()
            }
            register<Screen.Main.Menu.FormViewer> {
                FormViewerScreen()
            }
            register<Screen.Main.Menu.GpsTrackingReport> {
                GpsTrackingReportScreen()
            }
            register<Screen.Main.Notification> {
                NotificationScreen()
            }
            register<Screen.TicketProcess.TicketInfo> { provider->
                TicketInfoScreen(provider.ticketNumber)
            }
            register<Screen.TicketProcess.TicketProcessScreen> { provider->
                TicketProcessScreen(provider.ticketNumber)
            }
        }



