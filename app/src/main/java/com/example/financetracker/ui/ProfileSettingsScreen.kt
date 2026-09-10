package com.example.financetracker.ui
import androidx.compose.foundation.border

import androidx.compose.ui.graphics.toArgb
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.financetracker.theme.bounceClick
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(onBack: () -> Unit, onSave: () -> Unit) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    
    var name by remember { mutableStateOf(prefs.getString("profile_name", "User") ?: "User") }
    var salary by remember { mutableStateOf(prefs.getString("salary_per_month", "") ?: "") }
    var hapticsEnabled by remember { mutableStateOf(prefs.getBoolean("haptics_enabled", true)) }
    var themeMode by remember { mutableStateOf(prefs.getString("theme_mode", "System Default") ?: "System Default") }
    var themeColorInt by remember { mutableStateOf(prefs.getInt("theme_color", 0)) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        CenterAlignedTopAppBar(
            title = { Text("Profile Settings", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
            }
        )

        val scrollState = rememberScrollState()
        Column(modifier = Modifier.padding(horizontal = 16.dp).weight(1f).verticalScroll(scrollState)) {
            // Profile Icon Area
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = "Profile", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = salary,
                onValueChange = { salary = it },
                label = { Text("Monthly Salary") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Salary is automatically added as a Credit on the 1st of each month.", fontSize = 12.sp, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Enable Haptics", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.weight(1f))
                Switch(checked = hapticsEnabled, onCheckedChange = { hapticsEnabled = it })
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Appearance", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            val themeOptions = listOf("System Default", "Light Mode", "Dark Mode")
            themeOptions.forEach { opt ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().bounceClick {
                        themeMode = opt
                    }.padding(vertical = 8.dp)
                ) {
                    RadioButton(selected = themeMode == opt, onClick = { themeMode = opt })
                    Text(opt, modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Color Palette", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("(Select a color to preview, save to apply)", fontSize = 12.sp, color = Color.Gray)
            Row(modifier = Modifier.padding(top = 8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf(Color(0xFF01D475), Color(0xFF6200EA), Color(0xFFE91E63), Color(0xFF2196F3), Color(0xFFFF9800)).forEach { col ->
                    val isSelected = themeColorInt == col.toArgb()
                    Box(modifier = Modifier
                        .size(40.dp)
                        .background(col, CircleShape)
                        .then(if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.onBackground, CircleShape) else Modifier)
                        .bounceClick {
                            themeColorInt = col.toArgb()
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }

        // Save Button at bottom
        Button(
            onClick = {
                prefs.edit()
                    .putString("profile_name", name)
                    .putString("salary_per_month", salary)
                    .putBoolean("haptics_enabled", hapticsEnabled)
                    .putString("theme_mode", themeMode)
                    .putInt("theme_color", themeColorInt)
                    .apply()
                Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                onSave()
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("Update", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
