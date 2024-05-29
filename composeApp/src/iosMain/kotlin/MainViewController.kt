import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import di.commonAppModule
import domain.cities.CitiesRepository
import domain.weather.WeatherRepository
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
            val citiesRepository = koinInject<CitiesRepository>()
            val weatherRepository = koinInject<WeatherRepository>()
            val root = remember {
                DefaultRootComponent(DefaultComponentContext(LifecycleRegistry()), citiesRepository, weatherRepository)
            }
            App(root)
        }
    }
}