package eu.fluffici.dashy.ui.activities.common

import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import eu.fluffici.dashy.R

@Composable
fun RequestPermissionScreen(
    permissionName: String,
    permission: String,
    onGranted: @Composable () -> Unit,
    onChecking: @Composable () -> Unit,
) {
    val context = LocalContext.current

    val permissionState = remember {
        mutableStateOf<PermissionStatus>(PermissionStatus.Checking)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            permissionState.value = PermissionStatus.Granted
        } else {
            val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission)
            permissionState.value = PermissionStatus.Denied(shouldShowRationale)
        }
    }

    LaunchedEffect(Unit) {
        val currentStatus = if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            PermissionStatus.Granted
        } else {
            val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission)
            PermissionStatus.Denied(shouldShowRationale)
        }
        permissionState.value = currentStatus
    }

    when (permissionState.value) {
        is PermissionStatus.Granted -> {
            onGranted()
        }
        is PermissionStatus.Denied -> {
            val textToShow = if ((permissionState.value as PermissionStatus.Denied).shouldShowRationale) {
                "$permissionName permission is needed for this feature. Please grant the permission."
            } else {
                "$permissionName permission required. Please grant the permission from settings."
            }

            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.info_triangle_svg),
                        contentDescription = "Location Icon",
                        modifier = Modifier.size(68.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(textToShow, textAlign = TextAlign.Center, fontFamily = appFontFamily, color = Color.White, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { launcher.launch(permission) },
                        colors = buttonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Request Permission")
                    }
                }
            }
        }
        is PermissionStatus.Checking -> {
            onChecking()
        }
    }
}

sealed class PermissionStatus {
    data object Granted : PermissionStatus()
    data class Denied(val shouldShowRationale: Boolean) : PermissionStatus()
    data object Checking : PermissionStatus()
}
