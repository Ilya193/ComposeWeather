import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import di.commonAppModule
import domain.CitiesRepository
import org.koin.compose.KoinApplication
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import presentation.App
import presentation.DefaultRootComponent

fun MainViewController() = ComposeUIViewController {
    KoinApplication(application = {
        modules(commonAppModule)
    }) {
        KoinContext {
            val repository = koinInject<CitiesRepository>()
            val root = remember {
                DefaultRootComponent(DefaultComponentContext(LifecycleRegistry()), repository)
            }
            App(root)
        }
    }
}