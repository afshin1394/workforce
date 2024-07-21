package presentation.nav

import cafe.adriel.voyager.core.registry.ScreenProvider
import dev.icerock.moko.resources.StringResource
import irancell.nwg.wfm.MR

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


sealed class Screen() : ScreenProvider{
    data object Splash : Screen()
    data object CRScreen : Screen()

    sealed class Main() : Screen() {
        sealed class Menu() : Main(){
            data object Settings : Menu()
            data object About : Menu()
            data object Logout : Menu()
            data object FormViewer : Menu()
            data object MyTickets : Menu()
            data object GpsTrackingReport : Menu()
        }
        data object AccountInfo : Main()
        data object Notification : Main()
    }


    sealed class  Auth() : Screen() {
        data object Login : Auth()
        data class Verify(val phoneNumber : String = "") : Auth()
    }

    sealed class  TicketProcess() : Screen(){
        data class TicketInfo(val ticketNumber: String ) : TicketProcess()
        data class TicketProcessScreen(val ticketNumber : String) : TicketProcess()
    }



}



