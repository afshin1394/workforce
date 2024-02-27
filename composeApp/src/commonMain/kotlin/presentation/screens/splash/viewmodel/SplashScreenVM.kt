package presentation.screens.splash.viewmodel


import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.InternalStorage
import irancell.nwg.wfm.LifecycleEvent
import irancell.nwg.wfm.OnLifecycleEvent
import irancell.nwg.wfm.checkPermission
import irancell.nwg.wfm.provideAppContext

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import presentation.screens.splash.events.PermissionEvent
import utils.BaseViewModel
import utils.ViewStates


class SplashScreenVM(

) : BaseViewModel() {

    private val _permissionState =
        MutableStateFlow<PermissionEvent>(PermissionEvent.RequestPermission)
    val permissionState = _permissionState.asStateFlow()

    private val _lifeCycleEvent = MutableStateFlow(LifecycleEvent.ON_ANY)
    val lifeCycleEvent = _lifeCycleEvent.asStateFlow()

    init {
        InternalStorage.initWFMImages(provideAppContext())
        InternalStorage.initProcessImages(provideAppContext())
        InternalStorage.initSuspendImages(provideAppContext())


    }

   fun updateLifeCycleEventState( event : LifecycleEvent){
       _lifeCycleEvent.update { event }
   }

    val permissions = arrayListOf<Permission>(
        Permission.CAMERA,
        Permission.LOCATION,
    )
    fun updatePermissionState(permissionEvent: PermissionEvent){
        _permissionState.update { permissionEvent }
    }


//    fun checkPermissions(process: () -> Unit) {
//
//
//
//        viewModelScope.launch {
//
//
//            val hasAllPermissions =
//                permissions.all() { permissionsController.isPermissionGranted(it) }
//            if (hasAllPermissions) {
//                _permissionState.update { PermissionEvent.IsGranted }
//                Napier.log(LogLevel.ASSERT, "checkPermissions", message = "hasAllPermissions")
//                process()
//            } else {
//                try {
//                    Napier.log(
//                        LogLevel.ASSERT,
//                        "checkPermissions",
//                        message = "providePermission"
//                    )
//                    permissions.map {
//                        permissionsController.providePermission(it)
//                    }
//                    _permissionState.update { PermissionEvent.IsGranted }
//
//                } catch (deniedAlways: DeniedAlwaysException) {
//
//                    _permissionState.update { PermissionEvent.DeniedAlwaysException }
//                    return@launch
//
//                } catch (denied: DeniedException) {
//                    Napier.log(LogLevel.ASSERT, "checkPermissions", message = "DeniedException")
//                    _permissionState.update { PermissionEvent.DeniedException }
//                    return@launch
//
//                }
//                Napier.log(LogLevel.ASSERT, "checkPermissions", message = "request")
//
//
//            }
//
//
//        }
//
//    }

    fun changeStateDenied(){
        _permissionState.update { PermissionEvent.CheckPermission }

    }


}


