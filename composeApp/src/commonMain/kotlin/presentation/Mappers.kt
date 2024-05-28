package presentation

import domain.CityDomain
import domain.RegionDomain

fun RegionDomain.toRegionUi(): RegionUi =
    RegionUi(id, name, areas.map { it.toCityUi() })

fun CityDomain.toCityUi(): CityUi =
    CityUi(id, name)