package eu.fluffici.dashy.ui.activities.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import eu.fluffici.calendar.clickable
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.PartialAuth
import eu.fluffici.dashy.ui.activities.common.DashboardTitle
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.common.appFontFamily
import eu.fluffici.dashy.ui.base.PDAAppCompatActivity
import eu.fluffici.dashy.utils.Storage
import eu.fluffici.dashy.utils.newIntent

class LockScreen : PDAAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isCaptureMode = this.intent.getBooleanExtra("capture", false)
        val isConfirmMode = this.intent.getBooleanExtra("confirm", false)
        val user = Storage.getUser(applicationContext)

        var text: String = if (isCaptureMode) {
            "Please set a new pin-code."
        } else {
            "Welcome back, ${user.username}"
        }

        if (isConfirmMode)
            text = "Please authenticate to continue."

        setContent {
            val maxAttempts = remember { mutableIntStateOf(3) }
            val failedAttempts = remember { mutableIntStateOf(0) }

            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(5.dp)) {

                Column {
                    DashboardTitle(text = text, icon = R.drawable.lock_bolt_svg) {}

                    BiometricLoginScreen(
                        isCaptureMode = isCaptureMode,
                        isConfirmMode = isConfirmMode,
                        onSuccess = {
                            newIntent(Intent(applicationContext, MainActivity::class.java).apply {
                                putExtra("isAuthentified", true)
                            })
                        },
                        onPinEntered = {
                            val authentication: PartialAuth = Storage.getUserAuthentication(applicationContext)
                            if  (it == authentication.pinCode) {
                                newIntent(Intent(applicationContext, MainActivity::class.java).apply {
                                    putExtra("isAuthentified", true)
                                })
                            } else {
                                runOnUiThread {
                                    Toast.makeText(applicationContext, "Login failed, wrong pin code. Attempt ${failedAttempts.intValue} of ${maxAttempts.intValue}.", Toast.LENGTH_LONG).show()
                                }
                                failedAttempts.intValue++
                            }
                        },
                        onPinEnteredCapture = {
                            Storage.setUserAuth(applicationContext, PartialAuth(it).toJSON().toString())
                            runOnUiThread {
                                Toast.makeText(applicationContext, "New pin code saved!", Toast.LENGTH_LONG).show()
                            }
                            newIntent(Intent(applicationContext, MainActivity::class.java))
                        },
                        onPinEnteredConfirm = {
                            val action: String? = intent.getStringExtra("action")
                            val actionId: String? = intent.getStringExtra("actionId")
                            val authentication: PartialAuth = Storage.getUserAuthentication(applicationContext)
                            if  (it == authentication.pinCode) {
                                newIntent(Intent(applicationContext, MainActivity::class.java).apply {
                                    putExtra("isConfirmed", true)
                                    putExtra("confirmedAction", action)
                                    putExtra("actionId", actionId)
                                })
                            } else {
                                runOnUiThread {
                                    Toast.makeText(applicationContext, "Confirmation failed, wrong pin code.", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        isLocked = (failedAttempts.intValue >= maxAttempts.intValue)
                    )
                }
            }
        }
    }
}

@Composable
fun BiometricAuthentication(
    onAuthenticationResult: (Boolean, String?) -> Unit
) {
    val context = LocalContext.current
    val activity = LocalContext.current as FragmentActivity
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val canAuthenticate = BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)

    if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
        val biometricPromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Login")
            .setSubtitle("Use your fingerprint to login")
            .setNegativeButtonText("Cancel")
            .build()

        val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onAuthenticationResult(true, null)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onAuthenticationResult(false, errString.toString())
                errorMessage = errString.toString()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onAuthenticationResult(false, "Authentication failed")
                errorMessage = "Authentication failed"
            }
        }

        LaunchedEffect(Unit) {
            val biometricPrompt = BiometricPrompt(
                activity,
                ContextCompat.getMainExecutor(context),
                authenticationCallback
            )
            biometricPrompt.authenticate(biometricPromptInfo)
        }
    } else {
        onAuthenticationResult(false, "Biometric authentication not available")
    }

    if (errorMessage != null) {
        Text(
            text = errorMessage!!,
            color = Color.Red,
            modifier = Modifier.padding(16.dp),
            fontFamily = appFontFamily
        )
    }
}

@Composable
fun CustomNumpad(onPinEntered: (String) -> Unit) {
    var pin by remember { mutableStateOf("") }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(), // Dark background,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(end = 64.dp)
        ) {
            Text(
                text = "*".repeat(pin.length),
                fontSize = 32.sp,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
            val numbers = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("", "0", "⌫")
            )

            for (row in numbers) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (number in row) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.DarkGray, shape = RoundedCornerShape(12.dp))
                                .clickable {
                                    if (number == "⌫") {
                                        if (pin.isNotEmpty()) {
                                            pin = pin.dropLast(1)
                                        }
                                    } else if (number.isNotEmpty()) {
                                        pin += number
                                        if (pin.length == 4) {
                                            onPinEntered(pin)
                                            pin = ""
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = number,
                                fontSize = 24.sp,
                                color = Color.White,
                                fontFamily = appFontFamily
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BiometricLoginScreen(
    isCaptureMode: Boolean,
    isConfirmMode: Boolean,
    onSuccess: () -> Unit = {},
    onPinEntered: (String) -> Unit,
    onPinEnteredCapture: (String) -> Unit,
    onPinEnteredConfirm: (String) -> Unit,
    isLocked: Boolean = false
) {
    var useBiometric by remember { mutableStateOf(!isCaptureMode && !isConfirmMode) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (useBiometric) {
        BiometricAuthentication() { isAuthenticated, error ->
            if (isAuthenticated) {
                onSuccess()
                errorMessage = null
            } else {
                useBiometric = false
                errorMessage = error
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLocked) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.h4
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Too much attempts.",
                color = Color.Red,
                fontFamily = appFontFamily
            )
        } else {
            Text(
                text = "Login",
                style = MaterialTheme.typography.h4
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (useBiometric) {
                Text(
                    text = "Please authenticate using biometrics.",
                    color = Color.White,
                    fontFamily = appFontFamily
                )
            } else {
                CustomNumpad { pin ->
                    if (pin.length >= 4) {
                        if (isConfirmMode) {
                            onPinEnteredConfirm(pin)
                        } else {
                            if (isCaptureMode) {
                                onPinEnteredCapture(pin)
                            } else {
                                onPinEntered(pin)
                            }
                        }
                    }
                }
            }
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(16.dp),
                fontFamily = appFontFamily
            )
        }
    }
}