package dev.vsukharew.websocketarchitecturesample.combine

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
import dev.vsukharew.websocketarchtecturesample.databinding.ActivityShareTradingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ShareTradingActivity : AppCompatActivity() {
    private val viewModel: ShareTradingViewModel by viewModels {
        ShareTradingViewModel.factory
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityShareTradingBinding.inflate(layoutInflater)
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
        viewModel: ShareTradingViewModel,
        binding: ActivityShareTradingBinding
    ) {
        lifecycleScope.apply {
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiState
                        .map { it.isProgressVisible }
                        .collect(binding.progress::isVisible::set)
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiState
                        .map { it.isRetryVisible }
                        .collect(binding.retry::isVisible::set)
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiState
                        .map { it.isContentVisible }
                        .collect(binding.contentGroup::isVisible::set)
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiState
                        .map { it.shareTrading }
                        .filterIsInstance<ShareTrading>()
                        .collect {
                            binding.apply {
                                shareName.text = it.shareName
                                sharePrice.text = formatPrice(it.price)
                                asks.text = formatAsks(it.asks)
                                bids.text = formatBids(it.bids)
                            }
                        }
                }
            }
        }
    }

    private fun formatPrice(price: Double): String = "price: %.2f$".format(price)

    private fun formatAsks(asks: Int): String = "asks: $asks"

    private fun formatBids(bids: Int) = "bids: $bids"
}