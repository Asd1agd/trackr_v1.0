package com.example.financetracker.ui

import androidx.compose.foundation.border
import androidx.compose.ui.graphics.toArgb
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.financetracker.theme.bounceClick
import com.example.financetracker.theme.PresetPalettes
import com.example.financetracker.theme.ColorPalette
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(viewModel: FinanceViewModel, onBack: () -> Unit, onSave: () -> Unit) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var name by remember { mutableStateOf(prefs.getString("profile_name", "User") ?: "User") }
    var salary by remember { mutableStateOf(prefs.getString("salary_per_month", "") ?: "") }
    var hapticsEnabled by remember { mutableStateOf(prefs.getBoolean("haptics_enabled", true)) }
    var themeMode by remember { mutableStateOf(prefs.getString("theme_mode", "System Default") ?: "System Default") }
    var selectedPaletteIndex by remember { mutableStateOf(prefs.getInt("palette_index", 0)) }
    // Custom palette colors stored as 4 ints
    var customColors by remember {
        mutableStateOf(
            listOf(
                Color(prefs.getInt("custom_c1", 0xFF01D475.toInt())),
                Color(prefs.getInt("custom_c2", 0xFF29B6F6.toInt())),
                Color(prefs.getInt("custom_c3", 0xFF00BFA5.toInt())),
                Color(prefs.getInt("custom_c4", 0xFF69F0AE.toInt()))
            )
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // ── Top Bar ──
        CenterAlignedTopAppBar(
            title = { Text("Settings", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            // ── Profile Card ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(44.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = salary,
                        onValueChange = { salary = it },
                        label = { Text("Monthly Salary (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                    Text(
                        "Auto-credited on the 1st of each month",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Haptics Toggle ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp).fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Haptic Feedback", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Text(
                            "Vibration on scroll & tap",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = hapticsEnabled, onCheckedChange = { hapticsEnabled = it })
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Appearance Section ──
            Text(
                "Appearance",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    val themeOptions = listOf(
                        "System Default" to "Follow system theme",
                        "Light Mode" to "Always light",
                        "Dark Mode" to "Always dark"
                    )
                    themeOptions.forEachIndexed { index, (opt, desc) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .bounceClick { themeMode = opt }
                                .padding(horizontal = 12.dp, vertical = 12.dp)
                        ) {
                            RadioButton(
                                selected = themeMode == opt,
                                onClick = { themeMode = opt }
                            )
                            Column(modifier = Modifier.padding(start = 12.dp)) {
                                Text(opt, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                                Text(desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        if (index < themeOptions.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Color Palette Section ──
            Text(
                "Color Palette",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            // 5 preset palettes + 1 custom
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PresetPalettes.forEachIndexed { index, palette ->
                    PaletteCard(
                        name = palette.name,
                        colors = listOf(palette.primary, palette.secondary, palette.tertiary, palette.accent),
                        isSelected = selectedPaletteIndex == index,
                        onClick = { selectedPaletteIndex = index }
                    )
                }
                // Custom palette (index 5)
                PaletteCard(
                    name = "Custom",
                    colors = customColors,
                    isSelected = selectedPaletteIndex == 5,
                    onClick = { selectedPaletteIndex = 5 }
                )
            }


            // ── Import History Section ──
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Import History",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
            
            val importLogs: List<com.example.financetracker.data.ImportLog> = viewModel.importLogs.collectAsState().value
            
            if (importLogs.isEmpty()) {
                Text("No imported files.", fontSize = 14.sp, color = androidx.compose.ui.graphics.Color.Gray, modifier = Modifier.padding(start = 4.dp))
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    importLogs.forEach { log ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(log.filename, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                                    val dateStr = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(log.timestamp))
                                    Text(dateStr, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { viewModel.deleteImportLog(log.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // ── Save Button ──
        Button(
            onClick = {
                prefs.edit()
                    .putString("profile_name", name)
                    .putString("salary_per_month", salary)
                    .putBoolean("haptics_enabled", hapticsEnabled)
                    .putString("theme_mode", themeMode)
                    .putInt("palette_index", selectedPaletteIndex)
                    .putInt("custom_c1", customColors[0].toArgb())
                    .putInt("custom_c2", customColors[1].toArgb())
                    .putInt("custom_c3", customColors[2].toArgb())
                    .putInt("custom_c4", customColors[3].toArgb())
                    .apply()
                Toast.makeText(context, "Settings saved ✓", Toast.LENGTH_SHORT).show()
                onSave()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Update", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PaletteCard(name: String, colors: List<Color>, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isSelected) Modifier.border(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(20.dp)
                ) else Modifier
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp).fillMaxWidth()
        ) {
            // 4 color circles
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                colors.forEach { col ->
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(col, CircleShape)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                name,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
