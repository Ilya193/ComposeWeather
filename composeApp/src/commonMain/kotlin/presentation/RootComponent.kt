package presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import domain.CitiesRepository
import kotlinx.serialization.Serializable

interface RootComponent {

    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        class WeatherChild(val component: WeatherComponent) : Child()
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val repository: CitiesRepository
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Weather,
            handleBackButton = true,
            childFactory = ::child,
        )

    private fun child(config: Config, componentContext: ComponentContext): RootComponent.Child =
        when (config) {
            is Config.Weather -> RootComponent.Child.WeatherChild(weatherComponent(componentContext))
        }

    private fun weatherComponent(componentContext: ComponentContext): WeatherComponent =
        DefaultWeatherComponent(
            componentContext = componentContext,
            repository
        )

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Weather : Config
    }
}

@Composable
fun RootContent(component: RootComponent, modifier: Modifier = Modifier) {
    Children(
        stack = component.stack,
        modifier = modifier,
    ) {
        when (val child = it.instance) {
            is RootComponent.Child.WeatherChild -> WeatherContent(component = child.component)
        }
    }
}