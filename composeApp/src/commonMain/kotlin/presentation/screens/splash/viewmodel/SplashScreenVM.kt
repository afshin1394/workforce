package presentation.screens.splash.viewmodel

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.subscribe
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import presentation.screens.splash.events.PermissionEvent


class SplashScreenVM(
    val permissionsController: PermissionsController
) : ViewModel()  {

    private val _permissionState  = MutableStateFlow<PermissionEvent>(PermissionEvent.RequestPermission)
    val permissionState = _permissionState.asStateFlow()

    private val _numberOfRequests = MutableStateFlow(0)
    val numberOfRequests = _numberOfRequests.asStateFlow()


    val permissions = listOf<Permission>(
        Permission.CAMERA,
        Permission.COARSE_LOCATION,
        Permission.LOCATION,
    )

     fun checkPermissions(process : () -> Unit) {
                 viewModelScope.launch {
                     val hasAllPermissions = permissions.all() { permissionsController.isPermissionGranted(it) }
                     if(hasAllPermissions) {
                         _permissionState.update { PermissionEvent.IsGranted }
                         Napier.log(LogLevel.ASSERT, "checkPermissions", message = "hasAllPermissions")
                         process()
                     }
                     else if (_numberOfRequests.value < 2){
                         try {
                             Napier.log(LogLevel.ASSERT, "checkPermissions", message = "providePermission")
                             permissions.map{
                                 permissionsController.providePermission(it)
                             }
                             // Permission has been granted successfully.
                         } catch(deniedAlways: DeniedAlwaysException) {
                             Napier.log(LogLevel.ASSERT, "checkPermissions", message = "DeniedAlwaysException"+_numberOfRequests.value )

                             _permissionState.update{ PermissionEvent.OpenAppSettings }


                             // Permission is always denied.
                         } catch(denied: DeniedException) {
                             Napier.log(LogLevel.ASSERT, "checkPermissions", message = "DeniedException")

                             _permissionState.update{ PermissionEvent.ShowRational }

                             // Permission was denied.
                         }

                         Napier.log(LogLevel.ASSERT, "checkPermissions", message = "request")
                         _numberOfRequests.update { it+1 }

                     }else{
                         Napier.log(LogLevel.ASSERT, "checkPermissions", message = "openAppSettings")

                         _permissionState.update{ PermissionEvent.OpenAppSettings }
                     }

                 }
    }



}