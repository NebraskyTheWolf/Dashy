package eu.fluffici.dashy.utils

import android.R
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.multidex.MultiDexApplication
import eu.fluffici.dashy.PDAApplication
import eu.fluffici.dashy.getDeviceInfo
import eu.fluffici.dashy.ui.base.PDAAppCompatActivity


fun PDAAppCompatActivity.newIntent(intent: Intent) {
    if (this.applicationContext.getDeviceInfo().isPDADevice) {
        intent.setFlags(intent.flags)
    } else {
        intent.setFlags(intent.flags
                or FLAG_ACTIVITY_CLEAR_TOP
                or FLAG_ACTIVITY_NEW_TASK
                or FLAG_ACTIVITY_CLEAR_TASK
        )
    }

    val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext,
        R.anim.fade_in, R.anim.fade_out).toBundle()

    startActivity(intent, bundle)
}

fun AppCompatActivity.newIntent(intent: Intent) {
    if (this.applicationContext.getDeviceInfo().isPDADevice) {
        intent.setFlags(intent.flags)
    } else {
        intent.setFlags(intent.flags
                or FLAG_ACTIVITY_CLEAR_TOP
                or FLAG_ACTIVITY_NEW_TASK
                or FLAG_ACTIVITY_CLEAR_TASK
        )
    }

    val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext,
        R.anim.fade_in, R.anim.fade_out).toBundle()

    startActivity(intent, bundle)
}

fun MultiDexApplication.newIntent(intent: Intent) {
    if (this.applicationContext.getDeviceInfo().isPDADevice) {
        intent.setFlags(intent.flags)
    } else {
        intent.setFlags(intent.flags
                or FLAG_ACTIVITY_CLEAR_TOP
                or FLAG_ACTIVITY_NEW_TASK
                or FLAG_ACTIVITY_CLEAR_TASK
        )
    }

    val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext,
        R.anim.fade_in, R.anim.fade_out).toBundle()

    startActivity(intent, bundle)
}

fun startActivity(intent: Intent) {
    intent.setFlags(intent.flags
            or FLAG_ACTIVITY_CLEAR_TOP
            or FLAG_ACTIVITY_NEW_TASK
            or FLAG_ACTIVITY_CLEAR_TASK
    )

    startActivity(intent)
}
