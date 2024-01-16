package presentation.screens.splash.events

sealed interface PermissionEvent {
    data object IsGranted : PermissionEvent

    data object RequestPermission : PermissionEvent

    data object ShowRational : PermissionEvent

    data object OpenAppSettings : PermissionEvent
}