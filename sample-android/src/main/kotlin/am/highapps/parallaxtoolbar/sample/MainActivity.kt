package am.highapps.parallaxtoolbar.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

/**
 * Opens the interactive playground by default. A fixed sample screen or a playground preset can be
 * launched with:
 *   adb shell am start -n am.highapps.parallaxtoolbar.sample/.MainActivity --es screen <name>
 *   adb shell am start -n am.highapps.parallaxtoolbar.sample/.MainActivity --es preset <name>
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val screen = intent.getStringExtra("screen") ?: "playground"
        val preset = intent.getStringExtra("preset") ?: "default"
        setContent { SampleApp(screen, preset) }
    }
}
