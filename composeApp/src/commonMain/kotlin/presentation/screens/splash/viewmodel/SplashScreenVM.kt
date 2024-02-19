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
import irancell.nwg.wfm.canReadExternalStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import presentation.screens.splash.events.PermissionEvent


class SplashScreenVM(
    val permissionsController: PermissionsController
) : ViewModel() {

    private val _permissionState =
        MutableStateFlow<PermissionEvent>(PermissionEvent.RequestPermission)
    val permissionState = _permissionState.asStateFlow()


    val permissions = arrayListOf<Permission>(
        Permission.CAMERA,
        Permission.LOCATION,
    )

    fun checkPermissions(process: () -> Unit) {



        viewModelScope.launch {


            val hasAllPermissions =
                permissions.all() { permissionsController.isPermissionGranted(it) }
            if (hasAllPermissions) {
                _permissionState.update { PermissionEvent.IsGranted }
                Napier.log(LogLevel.ASSERT, "checkPermissions", message = "hasAllPermissions")
                process()
            } else {
                try {
                    Napier.log(
                        LogLevel.ASSERT,
                        "checkPermissions",
                        message = "providePermission"
                    )
                    permissions.map {
                        permissionsController.providePermission(it)
                    }
                    _permissionState.update { PermissionEvent.IsGranted }

                } catch (deniedAlways: DeniedAlwaysException) {

                    _permissionState.update { PermissionEvent.DeniedAlwaysException }
                    return@launch

                } catch (denied: DeniedException) {
                    Napier.log(LogLevel.ASSERT, "checkPermissions", message = "DeniedException")
                    _permissionState.update { PermissionEvent.DeniedException }
                    return@launch

                }
                Napier.log(LogLevel.ASSERT, "checkPermissions", message = "request")


            }


        }

    }

    fun changeStateDenied(){
        _permissionState.update { PermissionEvent.CheckPermission }

    }


}