package com.example.financetracker
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import com.example.financetracker.data.FinanceDatabase
import com.example.financetracker.repository.FinanceRepository
import com.example.financetracker.ui.FinanceViewModel
import com.example.financetracker.ui.FinanceViewModelFactory
import com.example.financetracker.ui.MainScreen
import com.example.financetracker.theme.FinanceTrackerTheme

class MainActivity : ComponentActivity() {

    private val db by lazy { FinanceDatabase.getDatabase(this) }
    private val repository by lazy { FinanceRepository(applicationContext, db.transactionDao(), db.categoryDao(), db.budgetDao(), db.goalDao()) }
    private val viewModel: FinanceViewModel by viewModels(
        factoryProducer = { FinanceViewModelFactory(repository) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        

        val prefs = getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        val openAddDialog = intent.getBooleanExtra("openAddDialog", false)
        
        setContent {
            var themeMode by remember { mutableStateOf(prefs.getString("theme_mode", "System Default") ?: "System Default") }
            var themeColorInt by remember { mutableStateOf(prefs.getInt("theme_color", 0)) }
            var isHapticsEnabled by remember { mutableStateOf(prefs.getBoolean("haptics_enabled", true)) }
            
            // Listen to changes
            DisposableEffect(Unit) {
                val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                    if (key == "theme_mode") {
                        themeMode = sharedPreferences.getString("theme_mode", "System Default") ?: "System Default"
                    } else if (key == "theme_color") {
                        themeColorInt = sharedPreferences.getInt("theme_color", 0)
                    } else if (key == "haptics_enabled") {
                        isHapticsEnabled = sharedPreferences.getBoolean("haptics_enabled", true)
                    }
                }
                prefs.registerOnSharedPreferenceChangeListener(listener)
                onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
            }
            
            val isDark = when (themeMode) {
                "Dark Mode" -> true
                "Light Mode" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            
            val primaryColor = if (themeColorInt != 0) Color(themeColorInt) else null

            FinanceTrackerTheme(darkTheme = isDark, primaryColor = primaryColor) {
                androidx.compose.runtime.CompositionLocalProvider(com.example.financetracker.theme.LocalHapticEnabled provides isHapticsEnabled) {

                val view = androidx.compose.ui.platform.LocalView.current
                var lastY by remember { mutableStateOf(0f) }
                Surface(
                    modifier = Modifier.fillMaxSize()
                        .pointerInput(isHapticsEnabled) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent(androidx.compose.ui.input.pointer.PointerEventPass.Initial)
                                    val change = event.changes.firstOrNull()
                                    if (change != null) {
                                        if (change.changedToDownIgnoreConsumed()) {
                                            view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                                            if (isHapticsEnabled) {
                                                view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                                            }
                                            lastY = change.position.y
                                        }
                                    }
                                }
                            }
                        }, 
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSplash by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(!openAddDialog) }
                    
                    if (showSplash) {
                        SplashScreen(onTimeout = { showSplash = false })
                    } else {
                        MainScreen(viewModel = viewModel, startWithAddDialog = openAddDialog)
                    }
                }
            }
        }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val scale = remember { Animatable(0f) }
    
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        )
        delay(1000)
        onTimeout()
    }
    
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_trakr_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp).scale(scale.value)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "trackr",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.alpha(scale.value)
            )
        }
    }
}
