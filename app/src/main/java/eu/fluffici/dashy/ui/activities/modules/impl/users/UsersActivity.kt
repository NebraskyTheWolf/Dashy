package eu.fluffici.dashy.ui.activities.modules.impl.users

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.ui.core.fragments.TableFragment


class UsersActivity : Module(
    "users",
    "platform.systems.users",
    false,
    R.drawable.users_group_svg,
    R.string.order
) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.users_activity)
        this.performCheck()

        setContent {
            UsersList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.destroy()
    }
}