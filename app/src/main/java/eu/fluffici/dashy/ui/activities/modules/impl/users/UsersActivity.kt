package eu.fluffici.dashy.ui.activities.modules.impl.users

import android.os.Bundle
import android.view.View
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.users_activity)
        this.performCheck()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.activity_container, TableFragment(),
                TableFragment::class.java.getSimpleName()).commit()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.destroy()
    }
}