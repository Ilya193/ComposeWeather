package presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import domain.cities.CitiesRepository
import domain.weather.WeatherRepository
import kotlinx.serialization.Serializable
import presentation.cities.DefaultRegionsComponent
import presentation.cities.RegionsComponent
import presentation.cities.RegionsContent
import presentation.weather.DefaultWeatherComponent
import presentation.weather.WeatherComponent
import presentation.weather.WeatherContent

interface RootComponent {

    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        class RegionsChild(val component: RegionsComponent) : Child()
        class WeatherChild(val component: WeatherComponent) : Child()
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val citiesRepository: CitiesRepository,
    private val weatherRepository: WeatherRepository
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Regions,
            handleBackButton = true,
            childFactory = ::child,
        )

    private fun child(config: Config, componentContext: ComponentContext): RootComponent.Child =
        when (config) {
            is Config.Regions -> RootComponent.Child.RegionsChild(regionsComponent(componentContext))
            is Config.Weather -> RootComponent.Child.WeatherChild(weatherComponent(componentContext, config))
        }

    private fun regionsComponent(componentContext: ComponentContext): RegionsComponent =
        DefaultRegionsComponent(
            componentContext = componentContext,
            repository = citiesRepository,
            onClick = {
                navigation.push(Config.Weather(it))
            }
        )

    private fun weatherComponent(componentContext: ComponentContext, config: Config.Weather): WeatherComponent =
        DefaultWeatherComponent(
            componentContext = componentContext,
            weatherRepository = weatherRepository,
            city = config.city,
            back = {
                navigation.pop()
            }
        )

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Regions : Config

        @Serializable
        data class Weather(val city: String) : Config
    }
}

@Composable
fun RootContent(component: RootComponent, modifier: Modifier = Modifier) {
    Children(
        stack = component.stack,
        modifier = modifier,
        animation = stackAnimation(slide())
    ) {
        when (val child = it.instance) {
            is RootComponent.Child.RegionsChild -> RegionsContent(component = child.component)
            is RootComponent.Child.WeatherChild -> WeatherContent(component = child.component)
        }
    }
}