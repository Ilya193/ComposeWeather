package data

import data.cities.model.Area
import data.cities.model.AreaX
import data.weather.model.WeatherDTO
import domain.cities.CityDomain
import domain.cities.RegionDomain
import domain.weather.WeatherDomain

fun Area.toRegionDomain(): RegionDomain =
    RegionDomain(id, name, areas.map { it.toCityDomain() })

fun AreaX.toCityDomain(): CityDomain =
    CityDomain(id, name)

fun WeatherDTO.toWeatherDomain(): WeatherDomain =
    WeatherDomain(location.country, location.name, location.localtime, current.temp_c)