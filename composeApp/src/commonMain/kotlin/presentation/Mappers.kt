package presentation

import domain.cities.CityDomain
import domain.cities.RegionDomain
import domain.weather.WeatherDomain
import presentation.cities.CityUi
import presentation.cities.RegionUi
import presentation.weather.WeatherUi

fun RegionDomain.toRegionUi(): RegionUi =
    RegionUi(id, name, areas.map { it.toCityUi() })

fun CityDomain.toCityUi(): CityUi =
    CityUi(id, name)

fun WeatherDomain.toWeatherUi(): WeatherUi =
    WeatherUi(country, name, localtime, temp.toInt())