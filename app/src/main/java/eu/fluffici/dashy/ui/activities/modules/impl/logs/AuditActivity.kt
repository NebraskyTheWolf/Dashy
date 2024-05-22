package eu.fluffici.dashy.ui.activities.modules.impl.logs

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.utils.newIntent

class AuditActivity : Module(
    "auditlog",
    "platform.audit.read",
    false,
    R.drawable.clipboard_data_svg,
    R.string.settings
) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()

        setContent {
            AuditLogList(onParentClick = {
                this.newIntent(Intent(this.applicationContext, MainActivity::class.java))
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.destroy()
    }
}