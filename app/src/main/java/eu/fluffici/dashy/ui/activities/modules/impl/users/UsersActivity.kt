package eu.fluffici.dashy.ui.activities.modules.impl.users

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.utils.newIntent


class UsersActivity : Module(
    "users",
    "platform.systems.users",
    false,
    R.drawable.users_group_svg,
    R.string.order
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()

        setContent {
            UsersList(
                onParentClick = {
                    this.newIntent(Intent(this.applicationContext, MainActivity::class.java))
                },
                onUserClick = {}
            )
        }
    }
}