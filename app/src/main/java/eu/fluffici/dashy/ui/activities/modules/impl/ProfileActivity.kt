package eu.fluffici.dashy.ui.activities.modules.impl

import android.os.Bundle
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.imageview.ShapeableImageView
import eu.fluffici.dashy.R
import eu.fluffici.dashy.ui.activities.modules.Module
import eu.fluffici.dashy.utils.Storage

class ProfileActivity : Module(
    "profile",
    "platform.systems.dashboard"
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.performCheck()
        setContentView(R.layout.profile_activity)

        val avatar = findViewById<ShapeableImageView>(R.id.avatar)

        if (Storage.getUser(applicationContext).avatar == 0) {
            Glide.with(this@ProfileActivity)
                .load("https://ui-avatars.com/api/?name=${Storage.getUser(applicationContext).username}&background=0D8ABC&color=fff")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(com.saadahmedsoft.popupdialog.R.raw.failed)
                .into(avatar);
        } else {
            Glide.with(this@ProfileActivity)
                .load("https://autumn.fluffici.eu/avatars/${Storage.getUser(applicationContext).avatarId}")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(com.saadahmedsoft.popupdialog.R.raw.failed)
                .into(avatar);
        }

        val name = findViewById<TextView>(R.id.name)
        name.text = Storage.getUser(applicationContext).username

        val email = findViewById<TextView>(R.id.email)
        email.text = Storage.getUser(applicationContext).email

        val role = findViewById<TextView>(R.id.role)
        role.text = Storage.getUser(applicationContext).roles
    }

    override fun onDestroy() {
        super.onDestroy()

        this.destroy()
    }
}