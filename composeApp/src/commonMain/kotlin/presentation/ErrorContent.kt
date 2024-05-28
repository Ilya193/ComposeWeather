package presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import share.composeapp.generated.resources.Res
import share.composeapp.generated.resources.has_error
import share.composeapp.generated.resources.retry

@Composable
fun ErrorContent(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(Res.string.has_error))
        Button(onClick = onRetry) {
            Text(text = stringResource(Res.string.retry))
        }
    }
}