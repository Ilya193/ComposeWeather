package ru.ikom.multiplatform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arkivanov.decompose.defaultComponentContext
import domain.CitiesRepository
import org.koin.compose.koinInject
import presentation.App
import presentation.DefaultRootComponent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val repository: CitiesRepository = koinInject()
            val root = DefaultRootComponent(
                componentContext = defaultComponentContext(),
                repository
            )
            App(root)
        }
    }
}