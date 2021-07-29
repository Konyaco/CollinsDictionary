import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val primaryColor = Color(0xFFC8382E)

private val darkColors = darkColors(primary = primaryColor, onPrimary = Color.White)
private val lightColors = lightColors(primary = primaryColor, onPrimary = Color.White)

@Composable
fun MyTheme(content: @Composable () -> Unit) {
    DesktopMaterialTheme(
        colors = if (isSystemInDarkTheme()) darkColors else lightColors,
        content = content
    )
}