package eu.fluffici.dashy.ui.activities.modules.impl.users

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import eu.fluffici.calendar.shared.User
import eu.fluffici.dashy.ui.activities.MainActivity
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.utils.newIntent

class UserProfileActivity : Module(
    "user_profile_act",
    "platform.systems.users"
) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.performCheck()

        val user = intent.extras?.getParcelable<User>("user")
        val userBadges = intent.extras?.getIntArray("userBadges")?.toList()

        user?.iconBadges = userBadges;

        setContent {
            UserProfileScreen(
                user = user!!,
                lastLogins = listOf(),
                onParentClick = {
                    this.newIntent(Intent(this.applicationContext, MainActivity::class.java))
                }
            )
        }
    }
}