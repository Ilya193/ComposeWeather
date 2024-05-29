package presentation.weather

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import domain.LoadResult
import domain.weather.WeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import presentation.ErrorContent
import presentation.LoadingContent
import presentation.toWeatherUi
import share.composeapp.generated.resources.Res
import share.composeapp.generated.resources.country
import share.composeapp.generated.resources.locality
import share.composeapp.generated.resources.localtime
import share.composeapp.generated.resources.temp
import share.composeapp.generated.resources.unit

interface WeatherComponent {
    val model: Value<Model>

    fun pop()

    fun action(event: Event)

    data class Model(
        val weatherUi: WeatherUi? = null,
        val isLoading: Boolean = false,
        val hasError: Boolean = false
    )
}

sealed interface Event {
    class Retry : Event
}

class DefaultWeatherComponent(
    componentContext: ComponentContext,
    private val weatherRepository: WeatherRepository,
    private val city: String,
    private val back: () -> Unit,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : WeatherComponent, ComponentContext by componentContext {

    private val scope = coroutineScope()

    private val _model = MutableValue(WeatherComponent.Model(isLoading = true))
    override val model: Value<WeatherComponent.Model> = _model

    init {
        fetchWeather()
    }

    private fun fetchWeather() {
        scope.launch(dispatcher) {
            when (val weather = weatherRepository.fetchWeather(city)) {
                is LoadResult.Success -> _model.value =
                    WeatherComponent.Model(weatherUi = weather.data.toWeatherUi())

                is LoadResult.Error -> _model.value = WeatherComponent.Model(hasError = true)
            }
        }
    }

    override fun pop() = back()

    override fun action(event: Event) {
        scope.launch(dispatcher) {
            when (event) {
                is Event.Retry -> fetchWeather()
            }
        }
    }
}

@Composable
fun WeatherContent(component: WeatherComponent) {
    val model by component.model.subscribeAsState()

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(backgroundColor = MaterialTheme.colors.background, title = {
            Image(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { component.pop() },
                contentDescription = null
            )
        })
    }) {
        Box(modifier = Modifier.fillMaxSize().padding(it)) {
            if (model.isLoading) LoadingContent()
            else if (model.hasError) ErrorContent(remember { { component.action(Event.Retry()) } })
            else SuccessWeatherContent(model.weatherUi)
        }
    }
}

@Composable
fun SuccessWeatherContent(weatherUi: WeatherUi?) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp, start = 8.dp)
    ) {
        weatherUi?.let {
            Text(text = stringResource(Res.string.country) + " " + it.country)
            Text(text = stringResource(Res.string.locality) + " " + it.name)
            Text(text = stringResource(Res.string.localtime) + " " + it.localtime)
            Text(
                text = stringResource(Res.string.temp) + " " + it.temp.toString() + stringResource(
                    Res.string.unit
                )
            )
        }
    }
}