package irancell.nwg.wfm

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

    val permissions = listOf<String>(
        android.Manifest.permission.RECEIVE_SMS,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION

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