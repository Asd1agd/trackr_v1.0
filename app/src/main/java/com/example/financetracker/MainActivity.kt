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
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import com.example.financetracker.data.FinanceDatabase
import com.example.financetracker.repository.FinanceRepository
import com.example.financetracker.ui.FinanceViewModel
import com.example.financetracker.ui.FinanceViewModelFactory
import com.example.financetracker.ui.MainScreen
import com.example.financetracker.theme.FinanceTrackerTheme
import com.example.financetracker.theme.PresetPalettes
import com.example.financetracker.theme.ColorPalette

class MainActivity : ComponentActivity() {

    private val db by lazy { FinanceDatabase.getDatabase(this) }
    private val repository by lazy { FinanceRepository(applicationContext, db.transactionDao(), db.categoryDao(), db.budgetDao(), db.goalDao(), db.importLogDao()) }
    private val viewModel: FinanceViewModel by viewModels(
        factoryProducer = { FinanceViewModelFactory(repository) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        val openAddDialog = intent.getBooleanExtra("openAddDialog", false)

        setContent {
            val context = androidx.compose.ui.platform.LocalContext.current
            LaunchedEffect(intent) {
                if (intent?.action == android.content.Intent.ACTION_VIEW) {
                    intent.data?.let { uri ->
                        try {
                            val text = contentResolver.openInputStream(uri)?.use { it.bufferedReader().readText() }
                            if (text != null) {
                                val filename = documentName(contentResolver, uri)
                                val success = viewModel.importYamlData(text, filename)
                                if (success) {
                                    android.widget.Toast.makeText(context, "File imported successfully", android.widget.Toast.LENGTH_SHORT).show()
                                } else {
                                    android.widget.Toast.makeText(context, "file is not in proper spacing or format of requred yaml ckeck that format in the yaml input section", android.widget.Toast.LENGTH_LONG).show()
                                }
                            }
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(context, "file is not in proper spacing or format of requred yaml ckeck that format in the yaml input section", android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            var themeMode by remember { mutableStateOf(prefs.getString("theme_mode", "System Default") ?: "System Default") }
            var paletteIndex by remember { mutableStateOf(prefs.getInt("palette_index", 0)) }
            var isHapticsEnabled by remember { mutableStateOf(prefs.getBoolean("haptics_enabled", true)) }
            // Custom palette colors
            var customC1 by remember { mutableStateOf(prefs.getInt("custom_c1", 0xFF01D475.toInt())) }
            var customC2 by remember { mutableStateOf(prefs.getInt("custom_c2", 0xFF29B6F6.toInt())) }
            var customC3 by remember { mutableStateOf(prefs.getInt("custom_c3", 0xFF00BFA5.toInt())) }
            var customC4 by remember { mutableStateOf(prefs.getInt("custom_c4", 0xFF69F0AE.toInt())) }

            // Listen to changes
            DisposableEffect(Unit) {
                val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
                    when (key) {
                        "theme_mode" -> themeMode = sp.getString("theme_mode", "System Default") ?: "System Default"
                        "palette_index" -> paletteIndex = sp.getInt("palette_index", 0)
                        "haptics_enabled" -> isHapticsEnabled = sp.getBoolean("haptics_enabled", true)
                        "custom_c1" -> customC1 = sp.getInt("custom_c1", 0xFF01D475.toInt())
                        "custom_c2" -> customC2 = sp.getInt("custom_c2", 0xFF29B6F6.toInt())
                        "custom_c3" -> customC3 = sp.getInt("custom_c3", 0xFF00BFA5.toInt())
                        "custom_c4" -> customC4 = sp.getInt("custom_c4", 0xFF69F0AE.toInt())
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

            val palette = if (paletteIndex in PresetPalettes.indices) {
                PresetPalettes[paletteIndex]
            } else {
                ColorPalette("Custom", Color(customC1), Color(customC2), Color(customC3), Color(customC4))
            }

            FinanceTrackerTheme(darkTheme = isDark, palette = palette) {
                androidx.compose.runtime.CompositionLocalProvider(com.example.financetracker.theme.LocalHapticEnabled provides isHapticsEnabled) {

                val view = androidx.compose.ui.platform.LocalView.current
                var lastY by remember { mutableStateOf(0f) }
                Surface(
                    modifier = Modifier.fillMaxSize()
                        .pointerInput(isHapticsEnabled) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Initial)
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

private fun documentName(contentResolver: android.content.ContentResolver, uri: android.net.Uri): String {
    var name = "imported_file"
    try {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = cursor.getString(nameIndex)
                }
            }
        }
    } catch (e: Exception) {
        // ignore
    }
    return name
}
