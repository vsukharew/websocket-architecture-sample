package dev.vsukharew.websocketarchitecturesample.fold

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.vsukharew.websocketarchtecturesample.R
import dev.vsukharew.websocketarchtecturesample.databinding.ActivityConferenceBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ConferenceActivity : AppCompatActivity() {
    private val viewModel: ConferenceViewModel by viewModels { ConferenceViewModel.factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityConferenceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.retry.setOnClickListener { viewModel.retry() }
        collectUiState(lifecycleScope, viewModel, binding)
    }

    private fun collectUiState(
        lifecycleScope: CoroutineScope,
        viewModel: ConferenceViewModel,
        binding: ActivityConferenceBinding
    ) {
        lifecycleScope.apply {
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiState
                        .map { it.isProgressVisible }
                        .collect { binding.progress.isVisible = it }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiState
                        .map { it.isRetryVisible }
                        .collect { binding.retry.isVisible = it }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiState
                        .map { it.isContentVisible }
                        .collect { binding.conference.isVisible = it }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiState
                        .map { it.conference }
                        .filterIsInstance<Conference>()
                        .collect { binding.conference.text = formatConference(it) }
                }
            }
        }
    }

    private fun formatConference(conference: Conference): String {
        return "Conference: ${conference.name}.\n" +
                "Participants: \n ${
                    conference.participants.mapIndexed { index, participant -> "${index + 1}: $participant" }
                        .joinToString(separator = "\n ")
                }"
    }
}