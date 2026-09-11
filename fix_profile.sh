sed -i '15i import androidx.compose.runtime.collectAsState' app/src/main/java/com/example/financetracker/ui/ProfileSettingsScreen.kt
sed -i '16i import androidx.compose.material.icons.filled.Delete' app/src/main/java/com/example/financetracker/ui/ProfileSettingsScreen.kt
sed -i 's/androidx.compose.material.icons.Icons.Default.Delete/Icons.Default.Delete/g' app/src/main/java/com/example/financetracker/ui/ProfileSettingsScreen.kt
