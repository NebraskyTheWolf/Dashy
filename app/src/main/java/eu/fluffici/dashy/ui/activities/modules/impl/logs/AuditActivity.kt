package eu.fluffici.dashy.ui.activities.modules.impl.logs

import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import eu.fluffici.dashy.R
import eu.fluffici.dashy.events.module.PaginateCurrentPageEvent
import eu.fluffici.dashy.events.module.PaginateNextPageEvent
import eu.fluffici.dashy.events.module.PaginatePrevPageEvent
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.ui.core.fragments.AuditTableFragment
import eu.fluffici.dashy.ui.core.fragments.TableFragment
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

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
            AuditLogList()
        }
    }
}