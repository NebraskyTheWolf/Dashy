package eu.fluffici.dashy.ui.activities.modules.impl.users

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.google.android.gms.common.util.ArrayUtils
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.ui.core.fragments.TableFragment
import eu.fluffici.dashy.utils.newIntent


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
            UsersList(
                onParentClick = {
                    this.newIntent(Intent(this.applicationContext, MainActivity::class.java))
                },
                onUserClick = {
                    this.newIntent(Intent(this.applicationContext, UserProfileActivity::class.java).apply {
                        putExtra("user", it)
                        putExtra("userBadges", it.iconBadges?.toIntArray())
                    })
                }
            )
        }
    }
}