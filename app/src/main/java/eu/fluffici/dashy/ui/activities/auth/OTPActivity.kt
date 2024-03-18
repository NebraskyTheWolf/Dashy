package eu.fluffici.dashy.ui.activities.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.LoginEntity
import eu.fluffici.dashy.events.auth.OTPEvent
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.utils.Storage
import eu.fluffici.dashy.utils.newIntent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class OTPActivity : AppCompatActivity() {
    private var mClient = OkHttpClient()
    private var mBus = EventBus.getDefault()

    private lateinit var mOtpCode: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.otp_activity)

        this.mBus.register(this)
        this.mOtpCode = findViewById(R.id.code)

        val button = findViewById<Button>(R.id.confirm_otp)
        button.setOnClickListener {
            if (this.mOtpCode.text.toString().trim().isEmpty()) {
                this.mOtpCode.error = "OTP Code is invalid."
            }

            this.mBus.post(OTPEvent(this.mOtpCode.text.toString(), false))
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        this.mBus.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onOTP(event: OTPEvent) {
        val request = Request.Builder()
            .url("https://dashboard.fluffici.eu/api/login/otp-challenge")
            .post(event.toJSON().toString().toRequestBody("application/json".toMediaType()))
            .build()
        val response = this.mClient.newCall(request).execute()
        val data = Gson().fromJson(response.body?.string(), LoginEntity::class.java)
        if (response.isSuccessful) {
            if (data.status) {
                Storage.setUser(applicationContext, data.user.toJSON().toString())
                Storage.setAccessToken(applicationContext, data.token)

                val i = Intent(applicationContext, MainActivity::class.java)
                newIntent(i)
            } else {
                runOnUiThread {
                    this.mOtpCode.error = data.message
                }
            }
        } else {
            runOnUiThread {
                this.mOtpCode.error = "Unable to contact Fluffici servers."
            }
        }
    }
}