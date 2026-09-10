sed -i 's/androidx.compose.foundation.background(androidx.compose.ui.graphics.Color.Black)/background(androidx.compose.ui.graphics.Color.Black)/' app/src/main/java/com/example/financetracker/MainActivity.kt
sed -i 's/androidx.compose.ui.graphics.Color/Color/' app/src/main/java/com/example/financetracker/MainActivity.kt
sed -i '2i\
import androidx.compose.foundation.background\
import androidx.compose.ui.graphics.Color\
' app/src/main/java/com/example/financetracker/MainActivity.kt
