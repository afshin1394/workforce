package irancell.nwg.wfm

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import cafe.adriel.voyager.navigator.Navigator
import di.appModule
import org.koin.core.context.startKoin
import presentation.screens.splash.compose.SplashScreen

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            startKoin{
                modules(appModule())
            }
            Navigator(SplashScreen())
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}