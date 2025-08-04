
import androidx.compose.runtime.Composable

import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import di.httpModule
import di.repositoryModule
import di.useCaseModule
import di.viewModelModule
import irancell.nwg.wfm.MR
import irancell.nwg.wfm.platformModule
import org.jetbrains.compose.resources.ExperimentalResourceApi

import org.koin.core.context.startKoin
import presentation.screens.splash.compose.SplashScreen
import irancell.nwg.wfm.initializeLogging

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    
    initializeLogging()
    
    startKoin {
        modules(  httpModule(),
            repositoryModule(), useCaseModule(), viewModelModule(), platformModule)
    }
    Navigator(screen = SplashScreen(), content = { navigator ->
        SlideTransition(navigator)
    })

//    MaterialTheme {
//        var showContent by remember { mutableStateOf(false) }
//        val greeting = remember { Greeting().greet() }
//        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//            Button(onClick = { showContent = !showContent }) {
//                Text("Click me!")
//            }
//            AnimatedVisibility(showContent) {
//                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//                    Image(painterResource("compose-multiplatform.xml"), null)
//                    Text("Compose: $greeting")
//                }
//            }
//        }
//    }
}