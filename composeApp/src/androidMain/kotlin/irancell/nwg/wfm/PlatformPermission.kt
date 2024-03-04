package irancell.nwg.wfm

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale


@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun checkPermission(granted : () -> Unit, showRational : () -> Unit) {
    val externalPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE


    val permissions = listOf<String>(
        android.Manifest.permission.RECEIVE_SMS,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        externalPermission
    )
    val allPermissionState = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(allPermissionState) {
        if (allPermissionState.permissions.all { it.status.isGranted }) {
            granted()
        } else {
            if (allPermissionState.permissions.any { it.status.shouldShowRationale }) {
                showRational()
            } else {
                allPermissionState.launchMultiplePermissionRequest()
            }
        }
    }
}