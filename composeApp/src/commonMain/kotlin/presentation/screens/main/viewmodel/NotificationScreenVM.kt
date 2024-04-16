package presentation.screens.main.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.irancell.nwg.wfm.presentation.model.Notification
import com.irancell.nwg.wfm.presentation.model.NotificationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import utils.BaseViewModel
import utils.NotificationState

class NotificationScreenVM : BaseViewModel() {


    val notificationItems = arrayListOf(
        NotificationModel(
            ticketId = 2,
            ticketType = Notification.Suspend.type,
            "Suspended ticket",
            body = "has suspended ticket",
            status = "Read",
            statusId = NotificationState.Read.id
        ),
        NotificationModel(
            ticketId = 3,
            ticketType = Notification.Canceled.type,
            "Canceled ticket",
            body = "has canceled the ticket",
            status = "Unread",
            statusId = NotificationState.Read.id
        ),
        NotificationModel(
            ticketId = 4,
            ticketType = Notification.NewUpdate.type,
            "New update",
            body = "A new version of the app is released",
            status = "Read",
            statusId = NotificationState.Read.id
        ),
        NotificationModel(ticketId = 5,
            ticketType = Notification.NewUpdate.type,
            "New update",
            body = "A new version of the app is released",
            status = "Read",
            statusId = NotificationState.Read.id),
        NotificationModel(ticketId = 6,
            ticketType = Notification.NewUpdate.type,
            "New update",
            body = "A new version of the app is released",
            status = "Unread",
            statusId = NotificationState.UnRead.id),
        NotificationModel(ticketId = 7,
            ticketType = Notification.NewUpdate.type,
            "New update",
            body = "A new version of the app is released",
            status = "Read",
            statusId = 3),
        NotificationModel(ticketId = 8,
            ticketType = Notification.NewUpdate.type,
            "New update",
            body = "A new version of the app is released",
            status = "Read",
            statusId = NotificationState.UnRead.id),
        NotificationModel(ticketId = 9,
            ticketType = Notification.NewUpdate.type,
            "New update",
            body = "A new version of the app is released",
            status = "Unread",
            statusId = NotificationState.UnRead.id),
        NotificationModel(ticketId = 10,
            ticketType = Notification.NewUpdate.type,
            "New update",
            body = "A new version of the app is released",
            status = "Read",
            statusId = NotificationState.UnRead.id),
        NotificationModel(ticketId = 11,
            ticketType = Notification.NewUpdate.type,
            "New update",
            body = "A new version of the app is released",
            status = "Read",
            statusId = NotificationState.UnRead.id),
        NotificationModel(ticketId = 12,
            ticketType = Notification.NewUpdate.type,
            "New update",
            body = "A new version of the app is released",
            status = "Read",
            statusId = NotificationState.Read.id),


        )
     val notificationItemsState = mutableStateListOf<NotificationModel>()
     private val _selectState = MutableStateFlow("")
     val selectState = _selectState.asStateFlow()



    init {
        notificationItemsState.clear()
        notificationItemsState.addAll(notificationItems)
    }

    fun updateSelectState(selected : String){
        _selectState.update { selected }
    }

}