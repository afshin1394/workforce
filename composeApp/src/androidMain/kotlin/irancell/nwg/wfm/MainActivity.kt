package irancell.nwg.wfm

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import cafe.adriel.voyager.navigator.Navigator
import data.GeneralLocationRepositoryImpl
import org.koin.compose.KoinApplication
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import presentation.screens.splash.compose.SplashScreen

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
          App()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopKoin()
    }
}


@Preview
@Composable
fun AppAndroidPreview() {
    App()
}