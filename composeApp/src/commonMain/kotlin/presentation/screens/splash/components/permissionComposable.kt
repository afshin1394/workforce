//package com.irancell.nwg.wfm.presentation.screens.splash.components
//
//import android.Manifest
//import android.app.Activity
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.provider.Settings
//import android.util.Log
//import androidx.activity.compose.ManagedActivityResultLauncher
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalLifecycleOwner
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
//import androidx.core.content.ContextCompat
//import androidx.core.content.ContextCompat.startActivity
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleEventObserver
//import androidx.lifecycle.LifecycleOwner
//import kotlinx.coroutines.launch
//
//@Composable
//fun PermissionComposable(lifecycleOwner : LifecycleOwner,activity: Activity,content : @Composable ( ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>> , Boolean ,Array<String>,Boolean) -> Unit,process :  ()-> Unit) {
//    var locationPermissionsGranted by remember { mutableStateOf(areLocationPermissionsAlreadyGranted(activity)) }
//    var shouldShowPermissionRationale by remember {
//        mutableStateOf(
//            shouldShowRequestPermissionRationale(
//                activity,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) && shouldShowRequestPermissionRationale(activity,Manifest.permission.ACCESS_FINE_LOCATION)
//                    && shouldShowRequestPermissionRationale(activity,Manifest.permission.READ_SMS)
//                    && shouldShowRequestPermissionRationale(activity,Manifest.permission.CAMERA)
//        )
//    }
//
//    var shouldDirectUserToApplicationSettings by remember {
//        mutableStateOf(false)
//    }
//
//    var currentPermissionsStatus by remember {
//        mutableStateOf(
//            decideCurrentPermissionStatus(
//                locationPermissionsGranted,
//                shouldShowPermissionRationale
//            )
//        )
//    }
//
//    val permissions = arrayOf(
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//        Manifest.permission.CAMERA,
//        Manifest.permission.READ_SMS,
//    )
//
//    val locationPermissionLauncher  = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestMultiplePermissions(),
//        onResult = { permissions ->
//            locationPermissionsGranted = permissions.values.reduce { acc, isPermissionGranted ->
//                acc && isPermissionGranted
//            }
//
//
//            if (!locationPermissionsGranted) {
//                shouldShowPermissionRationale =
//                    shouldShowRequestPermissionRationale(
//                        activity,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    ) || shouldShowRequestPermissionRationale(activity,Manifest.permission.ACCESS_FINE_LOCATION)
//                            || shouldShowRequestPermissionRationale(activity,Manifest.permission.READ_SMS)
//                            || shouldShowRequestPermissionRationale(activity,Manifest.permission.CAMERA)
//            }else{
//                process()
//            }
//            shouldDirectUserToApplicationSettings =
//                !shouldShowPermissionRationale && !locationPermissionsGranted
//            currentPermissionsStatus = decideCurrentPermissionStatus(
//                locationPermissionsGranted,
//                shouldShowPermissionRationale
//            )
//        })
//
//
//
//
//    DisposableEffect(key1 = lifecycleOwner, effect = {
//        val observer = LifecycleEventObserver { _, event ->
//
//            if (event == Lifecycle.Event.ON_START &&
//                !locationPermissionsGranted &&
//                !shouldShowPermissionRationale
//            ) {
//                locationPermissionLauncher.launch(permissions)
//            }
//        }
//        lifecycleOwner.lifecycle.addObserver(observer)
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer)
//        }
//    }
//    )
//
//    content(locationPermissionLauncher,shouldShowPermissionRationale,permissions,shouldDirectUserToApplicationSettings)
//
//
//}
//
//
//private fun areLocationPermissionsAlreadyGranted(activity: Activity): Boolean {
//    return ContextCompat.checkSelfPermission(
//        activity,
//        Manifest.permission.ACCESS_FINE_LOCATION
//    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
//        activity,
//        Manifest.permission.ACCESS_COARSE_LOCATION
//    ) == PackageManager.PERMISSION_GRANTED
//            && ContextCompat.checkSelfPermission(
//        activity,
//        Manifest.permission.SEND_SMS
//    ) == PackageManager.PERMISSION_GRANTED &&
//    ContextCompat.checkSelfPermission(
//        activity,
//        Manifest.permission.CAMERA
//    ) == PackageManager.PERMISSION_GRANTED
//}
//
// fun openApplicationSettings(activity: Activity) {
//    Intent(
//        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//        Uri.fromParts("package",activity.packageName , null)
//    ).also {
//        startActivity(activity,it,null)
//    }
//}
//
//private fun decideCurrentPermissionStatus(
//    locationPermissionsGranted: Boolean,
//    shouldShowPermissionRationale: Boolean
//): String {
//    return if (locationPermissionsGranted) "Granted"
//    else if (shouldShowPermissionRationale) "Rejected"
//    else "Denied"
//}