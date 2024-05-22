package eu.fluffici.dashy.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import eu.fluffici.calendar.StatusBarColorUpdateEffect
import eu.fluffici.calendar.pages.AkceCalendar
import eu.fluffici.calendar.pages.toolbarColor
import eu.fluffici.calendar.shared.declineOtp
import eu.fluffici.calendar.shared.getLatestPendingOTP
import eu.fluffici.calendar.shared.grantOtp
import eu.fluffici.dashy.R
import eu.fluffici.dashy.entities.PermissionEntity
import eu.fluffici.dashy.events.auth.OTPRequest
import eu.fluffici.dashy.events.module.CardClickEvent
import eu.fluffici.dashy.events.module.PermissionCheckEvent
import eu.fluffici.dashy.ui.activities.auth.LockScreen
import eu.fluffici.dashy.ui.activities.common.CrashAlertScreen
import eu.fluffici.dashy.ui.activities.common.DashboardUI
import eu.fluffici.dashy.ui.activities.common.ErrorScreen
import eu.fluffici.dashy.ui.activities.common.HomePage
import eu.fluffici.dashy.ui.activities.common.RequestPermissionScreen
import eu.fluffici.dashy.ui.activities.common.Settings
import eu.fluffici.dashy.ui.activities.common.appFontFamily
import eu.fluffici.dashy.ui.activities.components.DottedBackground
import eu.fluffici.dashy.ui.activities.experiment.IAuthentication
import eu.fluffici.dashy.ui.activities.experiment.LoginConfirmation
import eu.fluffici.dashy.ui.activities.modules.impl.ProfileActivity
import eu.fluffici.dashy.ui.activities.modules.impl.calendar.CalendarActivity
import eu.fluffici.dashy.ui.activities.modules.impl.logs.AuditActivity
import eu.fluffici.dashy.ui.activities.modules.impl.orders.activities.OrdersActivity
import eu.fluffici.dashy.ui.activities.modules.impl.otp.activities.OTPActivity
import eu.fluffici.dashy.ui.activities.modules.impl.product.activities.ProductActivity
import eu.fluffici.dashy.ui.activities.modules.impl.support.SupportActivity
import eu.fluffici.dashy.ui.activities.modules.impl.users.UsersActivity
import eu.fluffici.dashy.ui.activities.settings.PrivacySettings
import eu.fluffici.dashy.ui.activities.settings.SecuritySettings
import eu.fluffici.dashy.ui.activities.theme.Shapes
import eu.fluffici.dashy.ui.base.PDAAppCompatActivity
import eu.fluffici.dashy.utils.Storage
import eu.fluffici.dashy.utils.newIntent
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.Request
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MainActivity : PDAAppCompatActivity() {
    private val mBus = EventBus.getDefault()
    private var mClient = OkHttpClient()
    private var mSafeGuard: Boolean = false

    var executor = Executors.newScheduledThreadPool(10)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        this.mBus.register(this)

        if (this.intent.hasExtra("isAuthentified")) {
            Storage.isAuthentified = this.intent.getBooleanExtra("isAuthentified", false)
        }

        if (!Storage.isAuthentified && Storage.hasAuthentication(applicationContext)) {
            newIntent(Intent(applicationContext, LockScreen::class.java))
            return
        }

        if (this.intent.hasExtra("isConfirmed") && !this.mSafeGuard) {
            this.mSafeGuard = true
            when (this.intent.getStringExtra("confirmedAction")) {
                "otp_accepted" -> {
                    mBus.post(OTPRequest(
                        requestId = this.intent.getStringExtra("actionId")!!,
                        status = "otp_accepted"
                    ))
                }
                "otp_declined" -> {
                    mBus.post(OTPRequest(
                        requestId = this.intent.getStringExtra("actionId")!!,
                        status = "otp_declined"
                    ))
                }
            }
        }

        if (Storage.hasAuthentication(applicationContext)) {
            this.executor.scheduleWithFixedDelay({
                this.mBus.post(CardClickEvent("fetch_latest_otp"))
            }, 2, 10, TimeUnit.SECONDS)
        } else {
            Toast.makeText(applicationContext, "Please setup a pin-code before accepting your OTP request(s).", Toast.LENGTH_SHORT).show()
        }

        setContent {
            RequestPermissionScreen(
                permissionName = "Notifications",
                permission = Manifest.permission.POST_NOTIFICATIONS,
                onChecking = {
                    SplashScreen(mBus = this.mBus, isCycling = true)
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Checking permissions registries.", Toast.LENGTH_SHORT).show()
                    }
                },
                onGranted = {
                    BottomNavBarTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colors.background
                        ) {
                            DottedBackground()
                            if (Storage.isLoaded) {
                                MainScreen(context = applicationContext, mBus = this.mBus)
                            } else {
                                SplashScreen(mBus = this.mBus)
                                Storage.isLoaded = true
                            }
                        }
                    }
                }
            )
        }
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

            // Settings

            "privacy" -> {
                newIntent(Intent(applicationContext, PrivacySettings::class.java))
            }
            "security" -> {
                newIntent(Intent(applicationContext, SecuritySettings::class.java))
            }

            // Automated action
            "fetch_latest_otp" -> {
                val latestPendingOTP: IAuthentication? = getLatestPendingOTP()
                if (latestPendingOTP != null) {
                    newIntent(Intent(applicationContext, LoginConfirmation::class.java).apply {
                        putExtra("requestId", latestPendingOTP.requestId)

                        flags = Intent.FLAG_ACTIVITY_NO_HISTORY or
                                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                    })
                }
            }
        }

        this.mBus.removeStickyEvent(event);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Subscribe(sticky = true,threadMode = ThreadMode.ASYNC)
    fun onClick(event: OTPRequest) {
        when (event.status) {
            "otp_accepted" -> {
                grantOtp(event.requestId)

                runOnUiThread {
                    Toast.makeText(applicationContext, "You granted your OTP request.", Toast.LENGTH_LONG).show()
                }
            }

            "otp_declined" -> {
                declineOtp(event.requestId)

                runOnUiThread {
                    Toast.makeText(applicationContext, "You declined your OTP request.", Toast.LENGTH_LONG).show()
                }
            }
        }

        this.mBus.removeStickyEvent(event);
    }

    override fun onDestroy() {
        super.onDestroy()
        this.mBus.unregister(this)
        this.mSafeGuard = false
    }

    override fun onStop() {
        super.onStop()

        this.mBus.unregister(this)
        this.mSafeGuard = false
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
                    val i = Intent(this, ErrorScreen::class.java)
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.putExtra("title", "Account termination.")
                    i.putExtra("description", "Your account has been terminated.")
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Continue with the action.
        } else {
            requestNotificationPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is granted
            }
            else -> {
                // Request the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
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
        Screen.Calendar,
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(navController: NavHostController, context: Context, mBus: EventBus) {
    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(applicationContext = context, mBus = mBus) }
        composable(Screen.Calendar.route) { CalendarScreen(applicationContext = context, mBus = mBus) }
        composable(Screen.Search.route) { SearchScreen(applicationContext = context, mBus = mBus) }
        composable(Screen.Settings.route) { SettingsScreen(applicationContext = context, mBus = mBus) }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(applicationContext: Context, mBus: EventBus) {
    StatusBarColorUpdateEffect(toolbarColor)
    HomePage()
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(applicationContext: Context, mBus: EventBus) {
    StatusBarColorUpdateEffect(toolbarColor)
    AkceCalendar()
}

@Composable
fun SearchScreen(applicationContext: Context, mBus: EventBus) {
    StatusBarColorUpdateEffect(toolbarColor)
    DashboardUI(context = applicationContext, eventBus = mBus)
}

@Composable
fun SettingsScreen(applicationContext: Context, mBus: EventBus) {
    StatusBarColorUpdateEffect(toolbarColor)
    Settings(mBus = mBus)
}

sealed class Screen(val route: String, val title: String, val icon: Int) {
    data object Home : Screen("home", "Home", R.drawable.home_2_svg)
    data object Calendar : Screen("calendar", "Calendar", R.drawable.calendar_event_svg)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SplashScreen(mBus: EventBus, isCycling: Boolean = false) {
    var startMainScreen by remember { mutableStateOf(false) }
    var isConnected by remember { mutableStateOf(true) }

    val context = LocalContext.current

    if (!isCycling) {
        LaunchedEffect(Unit) {
            delay(3000)
            isConnected = checkNetworkConnectivity(context)
            startMainScreen = true
        }
    }

    if (startMainScreen) {
        if (isConnected) {
            MainScreen(context = context, mBus = mBus)
        } else {
            CrashAlertScreen(
                title = "Connectivity issues detected.",
                description = "You seem to be offline. Please check your internet settings."
            )
        }
    } else {
        SplashContent()
    }
}

@Composable
fun SplashContent() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.fluffici_logo),
                contentDescription = "Brand Logo",
                modifier = Modifier.size(300.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Skvělá komunita čeká na tebe!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = appFontFamily,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(30.dp))
            CircularProgressIndicator(
                progress = progress,
                color = Color.Red,
                strokeWidth = 4.dp
            )
        }
    }
}
fun checkNetworkConnectivity(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        else -> false
    }
}

