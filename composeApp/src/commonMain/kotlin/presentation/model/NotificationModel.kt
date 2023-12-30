package com.irancell.nwg.wfm.presentation.model

data class NotificationModel(
    val ticketId : Int = 1,
    val ticketType : Int = Notification.NewTicket.type,
    val ticketTypeName : String = "NewTicket",
    val raiser : String = "Faez Rezvani",
    val body : String = "has assigned a ticket to you",
    val date : String = "11/25/2025",
    val time : String = "12:34:28",
    val isNew : Boolean = true
)

sealed class Notification(val type: Int){
    object NewTicket :  Notification(1)
    object Suspend :  Notification(2)
    object Canceled :  Notification(3)
    object NewUpdate :  Notification(4)
}


