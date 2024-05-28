package data

import data.model.Area
import data.model.AreaX
import domain.CityDomain
import domain.RegionDomain

fun Area.toRegionDomain(): RegionDomain =
    RegionDomain(id, name, areas.map { it.toCityDomain() })

fun AreaX.toCityDomain(): CityDomain =
    CityDomain(id, name)