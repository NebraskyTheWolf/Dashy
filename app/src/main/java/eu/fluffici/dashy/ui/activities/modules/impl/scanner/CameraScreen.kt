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
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val previewView = PreviewView(context)
                val preview = Preview.Builder().build()
                val selector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                preview.setSurfaceProvider(previewView.surfaceProvider)
                val camProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P)

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

        // Draw the DataMatrix overlay
        DataMatrixOverlay()
    }
}
@Composable
fun DataMatrixOverlay() {
    var alpha by remember { mutableFloatStateOf(1f) }
    val animatedAlpha by animateFloatAsState(
        targetValue = if (alpha == 1f) 0f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    LaunchedEffect(Unit) {
        while (true) {
            alpha = if (alpha == 1f) 0f else 1f
            kotlinx.coroutines.delay(2000)
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val rectSize = 600f
        val rectLeft = (width - rectSize) / 2
        val rectTop = (height - rectSize) / 2

        drawRoundRect(
            color = Color(0x88000000),
            topLeft = Offset(rectLeft, rectTop),
            size = androidx.compose.ui.geometry.Size(rectSize, rectSize),
            cornerRadius = CornerRadius(16f, 16f),
            style = Stroke(width = 4f)
        )

        val gridSize = 20
        val cellSize = rectSize / gridSize

        for (i in 0..gridSize) {
            val pos = rectLeft + i * cellSize
            drawLine(
                color = Color.White.copy(alpha = animatedAlpha),
                start = Offset(pos, rectTop),
                end = Offset(pos, rectTop + rectSize),
                strokeWidth = 2f
            )
            drawLine(
                color = Color.White.copy(alpha = animatedAlpha),
                start = Offset(rectLeft, rectTop + i * cellSize),
                end = Offset(rectLeft + rectSize, rectTop + i * cellSize),
                strokeWidth = 2f
            )
        }
    }
}
