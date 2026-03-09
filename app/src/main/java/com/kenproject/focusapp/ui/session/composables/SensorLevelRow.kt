package com.kenproject.focusapp.ui.session.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kenproject.focusapp.R

@Composable
fun SensorLevelRow(
    noiseLevel: Float,
    movementLevel: Float
) {

    val noiseLabel = stringResource(id = R.string.sensor_noise_label)
    val movementLabel = stringResource(id = R.string.sensor_movement_label)

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        SensorLevelCard(
            label = noiseLabel,
            value = noiseLevel,
            icon = Icons.Default.Warning,
            modifier = Modifier.weight(1f),
            contentDescription = stringResource(
                id = R.string.sensor_level_description,
                noiseLabel,
                (noiseLevel * 100).toInt()
            )
        )
        SensorLevelCard(
            label = movementLabel,
            value = movementLevel,
            icon = Icons.Default.Warning,
            modifier = Modifier.weight(1f),
            contentDescription = stringResource(
                id = R.string.sensor_level_description,
                movementLabel,
                (movementLevel * 100).toInt()
            )
        )
    }
}