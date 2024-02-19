package com.irancell.nwg.wfm.presentation.nav

import cafe.adriel.voyager.core.registry.ScreenProvider
import presentation.screens.main.events.MainEvent

//sealed class Screen(val route: String) {
//   data object Splash : Screen("splash")
//   data object Main : Screen("main") {
//         data object MyTickets : Screen("My Tickets")
//         data object AccountInfo : Screen("Account info")
//         data object Settings : Screen("settings")
//         data object About : Screen("about")
//         data object Notification : Screen("Notification")
//    }
//
//    data object Auth : Screen("auth") {
//        data object Login : Screen("login")
//        data object Verify : Screen("verify")
//    }
//
//    data object TicketProcess : Screen("ticket_process"){
//        data object TicketInfo : Screen("ticket_info")
//        data object HSECheck1 : Screen("hse_check_1")
//        data object Routing : Screen("routing")
//        data object HSECheck2 : Screen("hse_check_2")
//        data object JobReport : Screen("job_report")
//        data object WaitingForApproval : Screen("waiting_for_approval")
//    }
//}


sealed class Screen(val mainRoute: String) : ScreenProvider{
    data object Splash : Screen("splash")
    sealed class Main(val route : String) : Screen("main") {
        sealed class Menu(val subRoute : String) : Main("Menu"){
            data object Settings : Menu("settings")
            data object About : Menu("about")
            data object Logout : Menu("logout")
            data object MyTickets : Menu("My Tickets")
            data object GpsTrackingReport : Menu("GpsTrackingReport")
        }
        data object AccountInfo : Main("Account info")
        data object Notification : Main("Notification")
    }


    sealed class  Auth(val route : String) : Screen("auth") {
        data object Login : Auth("login")
        data class Verify(val phoneNumber : String = "") : Auth("verify")
    }

    sealed class  TicketProcess(val route: String) : Screen("ticket_process"){
        data object TicketInfo : TicketProcess("ticket_info")
        data object TicketProcessScreen : TicketProcess("ticket_process")
    }
}



