package com.kenproject.focusapp.ui.session

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kenproject.focusapp.R
import com.kenproject.focusapp.ui.session.composables.DistractionLog
import com.kenproject.focusapp.ui.session.composables.DistractionSummaryCard
import com.kenproject.focusapp.ui.session.composables.SensorLevelRow
import com.kenproject.focusapp.ui.session.composables.StartStopButton
import com.kenproject.focusapp.ui.session.composables.TimerDisplay

@Composable
fun SessionScreen(
    viewModel: SessionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isInitializing) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = if (uiState.isSessionActive)
                stringResource(R.string.session_title_active)
            else
                stringResource(R.string.session_title_ready),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (uiState.isSessionActive)
                stringResource(R.string.session_subtitle_active)
            else
                stringResource(R.string.session_subtitle_ready),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        TimerDisplay(
            durationSeconds = uiState.sessionDurationSeconds,
            isActive = uiState.isSessionActive
        )

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = uiState.isSessionActive,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            SensorLevelRow(
                noiseLevel = uiState.currentNoiseLevel,
                movementLevel = uiState.currentMovementLevel
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(visible = uiState.isSessionActive) {
            DistractionSummaryCard(count = uiState.currentDistractionCount)
        }

        Spacer(modifier = Modifier.height(32.dp))

        StartStopButton(
            isActive = uiState.isSessionActive,
            isLoading = uiState.isLoadingStart,
            onStart = viewModel::startFocusSession,
            onStop = viewModel::stopFocusSession
        )

        Spacer(modifier = Modifier.height(28.dp))

        AnimatedVisibility(visible = uiState.currentSessionEvents.isNotEmpty()) {
            DistractionLog(
                title = stringResource(R.string.log_title_this_session),
                events = uiState.currentSessionEvents
            )
        }

        AnimatedVisibility(
            visible = !uiState.isSessionActive && uiState.allHistoricEvents.isNotEmpty()
        ) {
            DistractionLog(
                title = stringResource(R.string.log_title_past_sessions),
                events = uiState.allHistoricEvents
            )
        }

        uiState.errorResId?.let { resId ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = resId, uiState.paramError ?: ""),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}