package presentation.screens.splash.viewmodel
import irancell.nwg.wfm.InternalStorage
import irancell.nwg.wfm.LifecycleEvent
import irancell.nwg.wfm.provideAppContext

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import presentation.screens.splash.events.PermissionEvent
import utils.BaseViewModel


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

    fun updatePermissionState(permissionEvent: PermissionEvent){
        _permissionState.update { permissionEvent }
    }


    fun changeStateDenied(){
        _permissionState.update { PermissionEvent.CheckPermission }
    }


}


