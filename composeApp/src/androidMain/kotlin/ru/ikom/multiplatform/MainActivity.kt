package ru.ikom.multiplatform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arkivanov.decompose.defaultComponentContext
import domain.cities.CitiesRepository
import domain.weather.WeatherRepository
import org.koin.compose.koinInject
import presentation.App
import presentation.DefaultRootComponent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val citiesRepository: CitiesRepository = koinInject()
            val weatherRepository: WeatherRepository = koinInject()
            val root = DefaultRootComponent(
                componentContext = defaultComponentContext(),
                citiesRepository,
                weatherRepository
            )
            App(root)
        }
    }
}