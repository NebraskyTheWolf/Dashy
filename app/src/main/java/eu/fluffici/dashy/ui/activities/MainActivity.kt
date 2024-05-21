package eu.fluffici.dashy.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.PermissionEntity
import eu.fluffici.dashy.events.module.CardClickEvent
import eu.fluffici.dashy.events.module.PermissionCheckEvent
import eu.fluffici.dashy.ui.activities.common.ErrorView
import eu.fluffici.dashy.ui.activities.modules.impl.ProfileActivity
import eu.fluffici.dashy.ui.activities.modules.impl.calendar.CalendarActivity
import eu.fluffici.dashy.ui.activities.modules.impl.logs.AuditActivity
import eu.fluffici.dashy.ui.activities.modules.impl.orders.activities.OrdersActivity
import eu.fluffici.dashy.ui.activities.modules.impl.otp.activities.OTPActivity
import eu.fluffici.dashy.ui.activities.modules.impl.product.activities.ProductActivity
import eu.fluffici.dashy.ui.activities.modules.impl.support.SupportActivity
import eu.fluffici.dashy.ui.activities.modules.impl.users.UsersActivity
import eu.fluffici.dashy.ui.activities.theme.Shapes
import eu.fluffici.dashy.ui.base.PDAAppCompatActivity
import eu.fluffici.dashy.utils.Storage
import eu.fluffici.dashy.utils.newIntent
import okhttp3.OkHttpClient
import okhttp3.Request
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : PDAAppCompatActivity() {
    private val mBus = EventBus.getDefault()
    private var mClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)

        setContent {
            BottomNavBarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(context = applicationContext, mBus = this.mBus)
                }
            }
        }

        this.mBus.register(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Subscribe(sticky = true,threadMode = ThreadMode.ASYNC)
    fun onClick(event: CardClickEvent) {
        when (event.viewId) {
            "users" -> {
              newIntent(Intent(applicationContext, UsersActivity::class.java))
            }
            "support" -> {
                newIntent(Intent(applicationContext, SupportActivity::class.java))
            }
            "calendar" -> {
                newIntent(Intent(applicationContext, CalendarActivity::class.java))
            }
            "auditlog" -> {
                newIntent(Intent(applicationContext, AuditActivity::class.java))
            }
            "orders" -> {
                newIntent(Intent(applicationContext, OrdersActivity::class.java))
            }
            "profile" -> {
                newIntent(Intent(applicationContext, ProfileActivity::class.java))
            }
            "products" -> {
                newIntent(Intent(applicationContext, ProductActivity::class.java))
            }
            "otp" -> {
                newIntent(Intent(applicationContext, OTPActivity::class.java))
            }
            "parent" -> {
                newIntent(this.intent)
            }
        }

        this.mBus.removeStickyEvent(event);
    }

    override fun onDestroy() {
        super.onDestroy()
        this.mBus.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onPermissiveCheck(event: PermissionCheckEvent) {
        val request = Request.Builder()
            .url("https://api.fluffici.eu/api/user/@me/permission-check")
            .addHeader("Authorization", "Bearer ${Storage.getAccessToken(applicationContext)}")
            .post(event.toJSON())
            .build()
        val response = this.mClient.newCall(request).execute()
        val body = Gson().fromJson(response.body?.string(), PermissionEntity::class.java)
        if (response.isSuccessful) {
            if (body.error !== null) {
                if (body.error === "ACCOUNT_TERMINATED") {
                    val i = Intent(this, ErrorView::class.java)
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.putExtra("title", "Uh-Oh")
                    i.putExtra("message", "Your account has been terminated.")
                    return newIntent(i)
                }
            }

            if (!body.isGranted) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Permission denied.", Toast.LENGTH_LONG).show()
                }
                newIntent(this.intent)
            } else {
                if (this.isRestricted) {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "W.I.P", Toast.LENGTH_LONG).show()
                    }
                    newIntent(this.intent)
                }
            }
        } else {
            runOnUiThread {
                Toast.makeText(applicationContext, "Unable to check permissions.", Toast.LENGTH_LONG).show()
            }
            newIntent(this.intent)
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(context: Context, mBus: EventBus) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) {
        NavigationGraph(navController = navController, context = context, mBus = mBus)
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Search,
        Screen.Settings
    )
    BottomNavigation(
        backgroundColor = Color.Black,
        contentColor = Color.White
    ) {
        val currentRoute = currentRoute(navController)
        items.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(painter = painterResource(id = screen.icon), contentDescription = null) },
                label = { Text(text = screen.title) },
                selected = currentRoute == screen.route,
                selectedContentColor = Color.Red,
                unselectedContentColor = Color.Gray,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            // Pop up to the start destination to avoid building up a large stack of destinations
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry = navController.currentBackStackEntry
    return navBackStackEntry?.destination?.route
}

@Composable
fun NavigationGraph(navController: NavHostController, context: Context, mBus: EventBus) {
    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(applicationContext = context, mBus = mBus) }
        composable(Screen.Search.route) { SearchScreen(applicationContext = context, mBus = mBus) }
        composable(Screen.Settings.route) { SettingsScreen(applicationContext = context, mBus = mBus) }
    }
}

@Composable
fun HomeScreen(applicationContext: Context, mBus: EventBus) {
    HomePage()
}

@Composable
fun SearchScreen(applicationContext: Context, mBus: EventBus) {
    DashboardUI(context = applicationContext, eventBus = mBus)
}

@Composable
fun SettingsScreen(applicationContext: Context, mBus: EventBus) {
    Settings()
}

sealed class Screen(val route: String, val title: String, val icon: Int) {
    data object Home : Screen("home", "Home", R.drawable.home_2_svg)
    data object Search : Screen("dashboard", "Dashboard", R.drawable.apps_filled_svg)
    data object Settings : Screen("settings", "Settings", R.drawable.adjustments_plus_svg)
}

@SuppressLint("ConflictingOnColor")
private val DarkColorPalette = darkColors(
    primary = Color.Cyan,
    primaryVariant = Color.Cyan,
    secondary = Color.Cyan,
    background = Color.Black,
    surface = Color.Black,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun BottomNavBarTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        lightColors(
            primary = Color.Cyan,
            primaryVariant = Color.Cyan,
            secondary = Color.Cyan
        )
    }

    MaterialTheme(
        colors = colors,
        shapes = Shapes,
        content = content
    )
}