package eu.fluffici.dashy.ui.activities.auth

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showDrawable
import com.github.razir.progressbutton.showProgress
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.saadahmedsoft.popupdialog.PopupDialog
import com.saadahmedsoft.popupdialog.Styles
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.LoginEntity
import eu.fluffici.dashy.events.auth.LoginFailed
import eu.fluffici.dashy.events.auth.LoginRequest
import eu.fluffici.dashy.events.common.FirebaseSetup
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.common.ErrorScreen
import eu.fluffici.dashy.utils.Storage
import eu.fluffici.dashy.utils.newIntent
import okhttp3.OkHttpClient
import okhttp3.Request
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LoginActivity : AppCompatActivity() {
    private var mBus = EventBus.getDefault()
    private var mClient = OkHttpClient()

    private lateinit var mEmail: EditText;
    private lateinit var mPassword: EditText;
    private lateinit var mRememberMe: CheckBox;
    private lateinit var mLoginButton: Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        this.mBus.register(this)

        this.mEmail = findViewById(R.id.email)
        this.mPassword = findViewById(R.id.password)
        this.mRememberMe = findViewById(R.id.rememberme)

        this.mLoginButton = findViewById(R.id.login)
        bindProgressButton(this.mLoginButton)
        this.mLoginButton.attachTextChangeAnimator()

        this.mLoginButton.setOnClickListener {
            if(this.mEmail.text.toString().trim().isEmpty() || this.mPassword.text.toString().trim().isEmpty()) {
                this.mEmail.error = "The email or password is incorrect.";
            }

            this.mLoginButton.showProgress {
                progressColor = Color.WHITE
            }

            if (!this.mEmail.text.toString().trim().endsWith("@fluffici.eu")) {
                this.mEmail.error = "Only @fluffici.eu emails are accepted."

                return@setOnClickListener
            }

            this.mBus.post(LoginRequest(
                this.mEmail.text.toString(),
                this.mPassword.text.toString(),
                this.mRememberMe.isActivated
            ))
        }

        this.mRememberMe.setOnCheckedChangeListener { _, isChecked -> Storage.setRememberMe(applicationContext, isChecked)}

        val help = findViewById<TextView>(R.id.help)
        help.setOnClickListener {
            PopupDialog.getInstance(this)
                .setStyle(Styles.STANDARD)
                .setHeading("Help")
                .setDescription("You have some troubles to login?\n Click on reset to change your password.")
                .setCancelable(false)
                .setLottieRepeatCount(1)
                .setPopupDialogIcon(R.drawable.lock_open_svg)
                .setPositiveButtonText("Reset.")
                .setNegativeButtonText("Go back.")
                .showDialog(object : OnDialogButtonClickListener() {
                    override fun onPositiveClicked(dialog: Dialog?) {
                        super.onPositiveClicked(dialog)
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://auth.fluffici.eu/recovery?email=${this@LoginActivity.mEmail.text}&track=1")))
                    }

                    override fun onNegativeClicked(dialog: Dialog?) {
                        super.onNegativeClicked(dialog)
                        dialog?.dismiss()
                    }
                })
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        this.mBus.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onLogin(event: LoginRequest) {
        val request = Request.Builder()
            .url("https://api.fluffici.eu/api/login")
            .post(event.toJSON())
            .build()
        val response = this.mClient.newCall(request).execute()
        val body = Gson().fromJson(response.body?.string(), LoginEntity::class.java)
        if (response.isSuccessful) {
            if (body.status) {

                runOnUiThread {
                    this.mLoginButton.hideProgress(R.string.sign_in)
                }

                Storage.setUser(applicationContext, body.user.toJSON().toString())
                Storage.setAccessToken(applicationContext, body.token)

                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                        return@OnCompleteListener
                    }

                    Storage.setMessagingToken(applicationContext, task.result)

                    this.mBus.post(FirebaseSetup(task.result))
                })

                val i = Intent(applicationContext, MainActivity::class.java)
                newIntent(i)
            } else {
                this.mBus.post(LoginFailed(
                    body.error,
                    body.message
                ))
                this.setButtonError()
            }
        } else {
            this.mEmail.error = "Unable to contact Fluffici servers."
            this.setButtonError()
        }
    }

    @Subscribe()
    fun onLoginFailed(event: LoginFailed) {
        runOnUiThread {
            if (event.error == "ACCOUNT_TERMINATED")
                this.mEmail.error = "Your account is terminated."
            else
                if (event.message?.isNotEmpty() == true)
                    this.mEmail.error = event.message
                else
                    this.mEmail.error = "Unable to login."
        }

        this.setButtonError()
    }

    private fun setButtonError() {
       runOnUiThread {
           val animatedDrawable = ContextCompat.getDrawable(this, R.drawable.alert_octagon_svg)
           animatedDrawable?.setBounds(0, 0, 40, 40)

           if (animatedDrawable != null) {
               this.mLoginButton.showDrawable(animatedDrawable) {
                   buttonTextRes = R.string.failed
               }
           }
       }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onFirebase(event: FirebaseSetup) {
        val request = Request.Builder()
            .url("https://api.fluffici.eu/api/user/@me/update-firebase")
            .addHeader("Authorization", "Bearer ${Storage.getAccessToken(applicationContext)}")
            .patch(event.toJSON())
            .build()

        val response = this.mClient.newCall(request).execute()
        if (response.isSuccessful) {
            val data = Gson().fromJson(response.body?.string(), JsonObject::class.java);
            if (!data.get("status").asBoolean) {

                val i = Intent(applicationContext, ErrorScreen::class.java)
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.putExtra("title", "Firebase error")
                i.putExtra("description", data.get("message").asString)
                eu.fluffici.dashy.utils.startActivity(i)
            }
        }
    }
}