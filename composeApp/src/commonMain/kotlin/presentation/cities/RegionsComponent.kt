package presentation.cities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import domain.LoadResult
import domain.cities.CitiesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import presentation.ErrorContent
import presentation.LoadingContent
import presentation.toRegionUi
import share.composeapp.generated.resources.Res
import share.composeapp.generated.resources.input_city

interface RegionsComponent {
    val model: Value<Model>

    fun action(event: Event)

    fun onCityClicked(city: String)

    data class Model(
        val regions: List<RegionUi> = emptyList(),
        val cities: List<CityUi> = emptyList(),
        val searchMode: Boolean = false,
        val inputCity: String = "",
        val isLoading: Boolean = false,
        val hasError: Boolean = false
    )
}

sealed interface Event {
    class InputCity(val city: String) : Event
    class Retry : Event
    class ShowCities(val indexRegion: Int) : Event
}

class DefaultRegionsComponent(
    componentContext: ComponentContext,
    private val repository: CitiesRepository,
    private val onClick: (String) -> Unit,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : RegionsComponent, ComponentContext by componentContext {

    private val scope = coroutineScope()

    private var initRegions = mutableListOf<RegionUi>()
    private val _model = MutableValue(RegionsComponent.Model(isLoading = true))
    override val model: Value<RegionsComponent.Model> = _model

    init {
        fetchCities()
    }

    override fun action(event: Event) {
        scope.launch(dispatcher) {
            when (event) {
                is Event.InputCity -> inputCity(event.city)
                is Event.Retry -> fetchCities()
                is Event.ShowCities -> showCities(event.indexRegion)
            }
        }
    }

    override fun onCityClicked(city: String) {
        onClick(city)
    }

    private fun fetchCities() {
        scope.launch(dispatcher) {
            when (val regions = repository.fetchCities()) {
                is LoadResult.Success -> {
                    val temp = regions.data.map { it.toRegionUi() }
                    initRegions = temp.toMutableList()
                    _model.value = RegionsComponent.Model(regions = temp)
                }

                is LoadResult.Error -> {
                    _model.value = RegionsComponent.Model(hasError = true)
                }
            }
        }
    }

    private fun showCities(indexRegion: Int) {
        val regions = _model.value.regions.toMutableList()
        val region = regions[indexRegion]
        regions[indexRegion] = region.copy(showCities = !region.showCities)
        _model.update { it.copy(regions = regions) }
    }

    private fun inputCity(city: String) {
        _model.update { it.copy(inputCity = city) }
        if (city.isEmpty()) _model.update { it.copy(cities = emptyList(), searchMode = false) }
        else {
            val temp = mutableListOf<CityUi>()
            initRegions.forEach {
                it.areas.forEach {
                    if (city in it.name) temp.add(it)
                }
            }
            _model.update { it.copy(cities = temp, searchMode = true) }
        }
    }
}

@Composable
fun RegionsContent(component: RegionsComponent) {
    val model by component.model.subscribeAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (model.isLoading) LoadingContent()
        else if (model.hasError) ErrorContent(remember { { component.action(Event.Retry()) } })
        else {
            SuccessRegionsContent(
                model,
                remember { { component.action(Event.InputCity(it)) } },
                remember { { component.action(Event.ShowCities(it)) } },
                remember { { component.onCityClicked(it) } })
        }
    }
}

@Composable
fun SuccessRegionsContent(
    model: RegionsComponent.Model,
    onInput: (String) -> Unit,
    onRegionClick: (Int) -> Unit,
    onCityClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (model.searchMode) CitiesContent(model, onCityClick, Modifier.fillMaxWidth().weight(1f))
        else RegionsAndCitiesContent(model, onRegionClick, onCityClick, Modifier.fillMaxWidth().weight(1f))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().weight(0.1f),
            value = model.inputCity,
            onValueChange = onInput,
            placeholder = {
                Text(text = stringResource(Res.string.input_city))
            })
    }
}

@Composable
fun RegionsAndCitiesContent(
    model: RegionsComponent.Model,
    onRegionClick: (Int) -> Unit,
    onCityClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(model.regions, key = { _, item -> item.id }) { index, item ->
            Text(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        onRegionClick(index)
                    }
                ).padding(start = 4.dp), text = item.name
            )
            AnimatedVisibility(item.showCities) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                ) {
                    items(item.areas, key = { it.id }) {
                        Text(modifier = Modifier.padding(start = 12.dp).clickable { onCityClick(it.name) }, text = it.name)
                    }
                }
            }
        }
    }
}

@Composable
fun CitiesContent(
    model: RegionsComponent.Model,
    onCityClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(model.cities, key = { it.id }) {
            Text(
                modifier = Modifier.padding(start = 12.dp).clickable { onCityClick(it.name) },
                text = it.name
            )
        }
    }
}