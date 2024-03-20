package eu.fluffici.dashy.ui.activities.modules.impl.scanner

import android.content.Intent
import android.media.CamcorderProfile
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import org.greenrobot.eventbus.EventBus

@Composable
fun CameraScreen(analyzerType: AnalyzerType, eventBus: EventBus, intent: Intent) {
    val localContext = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(localContext)
    }
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            val previewView = PreviewView(context)
            val preview = Preview.Builder().build()
            val selector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            preview.setSurfaceProvider(previewView.surfaceProvider)
            val camProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);

            val imageAnalysis = ImageAnalysis
                .Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetResolution(Size(camProfile.videoFrameHeight, camProfile.videoFrameWidth))
                .build()
            imageAnalysis.setAnalyzer(
                ContextCompat.getMainExecutor(context),
                if (analyzerType == AnalyzerType.BARCODE) {
                    BarcodeAnalyzer(eventBus, intent)
                } else {
                    TextAnalyzer(context)
                }
            )

            runCatching {
                cameraProviderFuture.get().bindToLifecycle(
                    lifecycleOwner,
                    selector,
                    preview,
                    imageAnalysis
                )
            }.onFailure {
                Log.e("CAMERA", "Camera bind error ${it.localizedMessage}", it)
            }
            previewView
        }
    )
}