package com.kenproject.focusapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.kenproject.focusapp.ui.session.SessionScreen
import com.kenproject.focusapp.ui.theme.FocusAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FocusActivity : ComponentActivity() {

    private val requiredPermissions: List<String>
        get() = buildList {
            add(Manifest.permission.RECORD_AUDIO)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val allGranted = result.values.all { it }
        if (allGranted) {
            showContent()
        } else {
            val denied = result.entries
                .filter { !it.value }
                .joinToString(", ") { it.key.toReadableName() }
            Toast.makeText(
                this,
                getString(R.string.error_permissions_denied, denied),
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (allPermissionsGranted()) {
            showContent()
        } else {
            permissionLauncher.launch(requiredPermissions.toTypedArray())
        }
    }

    private fun allPermissionsGranted(): Boolean =
        requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }

    private fun showContent() {
        setContent {
            FocusAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SessionScreen()
                }
            }
        }
    }

    private fun String.toReadableName(): String = when (this) {
        Manifest.permission.RECORD_AUDIO -> getString(R.string.permission_name_microphone)
        Manifest.permission.POST_NOTIFICATIONS -> getString(R.string.permission_name_notifications)
        else -> substringAfterLast('.')
    }
}