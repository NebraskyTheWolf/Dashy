package eu.fluffici.dashy.ui.activities.modules.impl.scanner

import android.annotation.SuppressLint
import android.content.Intent
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import eu.fluffici.dashy.events.module.OrderScannerEvent
import org.greenrobot.eventbus.EventBus

class BarcodeAnalyzer(private val eventBus: EventBus, val intent: Intent) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_DATA_MATRIX)
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image
            ?.let { image ->
                scanner.process(
                    InputImage.fromMediaImage(
                        image, imageProxy.imageInfo.rotationDegrees
                    )
                ).addOnSuccessListener { barcode ->
                    barcode?.takeIf { it.isNotEmpty() }
                        ?.mapNotNull { it.rawValue }
                        ?.joinToString(",")
                        ?.let {
                            if (this.intent.extras?.containsKey("isVoucher") == true) {
                                eventBus.post(OrderScannerEvent(it, "VOUCHER"))
                            }

                            if (this.intent.extras?.containsKey("isOrder") == true) {
                                eventBus.post(OrderScannerEvent(it, "ORDER"))
                            }
                        }
                }.addOnCompleteListener {
                    imageProxy.close()
                }
            }
    }
}