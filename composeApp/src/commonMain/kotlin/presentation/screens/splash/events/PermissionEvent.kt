package presentation.screens.splash.events

sealed interface PermissionEvent {
    data object IsGranted : PermissionEvent

    data object RequestPermission : PermissionEvent

    data object DeniedException : PermissionEvent

    data object DeniedAlwaysException : PermissionEvent
}