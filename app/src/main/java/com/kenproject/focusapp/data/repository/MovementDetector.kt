package com.kenproject.focusapp.data.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.kenproject.focusapp.domain.model.DetectionThresholds
import com.kenproject.focusapp.domain.model.DistractionSignal
import com.kenproject.focusapp.domain.repository.DistractionDetector
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import kotlin.math.sqrt

class MovementDetector @Inject constructor(
    @ApplicationContext private val context: Context,
    private val thresholds: DetectionThresholds
) : DistractionDetector {

    companion object {
        private const val MAX_ACCELERATION = 4f * 9.8f
    }

    private val _signals = MutableSharedFlow<DistractionSignal>(extraBufferCapacity = 8)
    override val signals: Flow<DistractionSignal> = _signals.asSharedFlow()

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private var lastEmitTime = 0L

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val now = System.currentTimeMillis()
            if (now - lastEmitTime < thresholds.movementDebounceMs) return

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val magnitude = sqrt(x * x + y * y + z * z)
            val normalized = (magnitude / MAX_ACCELERATION).coerceIn(0f, 1f)

            _signals.tryEmit(DistractionSignal.Movement(normalized))
            lastEmitTime = now
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
    }

    override fun start() {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            ?: return

        sensorManager.registerListener(
            sensorListener,
            sensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun stop() {
        sensorManager.unregisterListener(sensorListener)
    }
}