package com.kenproject.focusapp.data.repository

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import com.kenproject.focusapp.domain.model.DetectionThresholds
import com.kenproject.focusapp.domain.model.DistractionSignal
import com.kenproject.focusapp.domain.repository.DistractionDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sqrt

class NoiseDetector @Inject constructor(
    private val thresholds: DetectionThresholds
) : DistractionDetector {

    companion object {
        private const val SAMPLE_RATE = 16_000
        private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    }

    private val _signals = MutableSharedFlow<DistractionSignal>(extraBufferCapacity = 8)
    override val signals: Flow<DistractionSignal> = _signals.asSharedFlow()

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun start() {
        if (recordingJob?.isActive == true) return

        val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
            .coerceAtLeast(4096)

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize
        ).also { it.startRecording() }

        recordingJob = scope.launch {
            val buffer = ShortArray(bufferSize / 2)
            while (isActive) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: break
                if (read > 0) {
                    val rms = calculateRms(buffer, read)
                    val normalized = (rms / Short.MAX_VALUE.toFloat()).coerceIn(0.0, 1.0)
                    _signals.tryEmit(DistractionSignal.Noise(normalized.toFloat()))
                }
                delay(thresholds.noisePollIntervalMs)
            }
        }
    }

    override fun stop() {
        recordingJob?.cancel()
        recordingJob = null
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    private fun calculateRms(buffer: ShortArray, length: Int): Double {
        var sum = 0.0
        for (i in 0 until length) {
            sum += buffer[i].toDouble() * buffer[i]
        }
        return sqrt(sum / length)
    }
}