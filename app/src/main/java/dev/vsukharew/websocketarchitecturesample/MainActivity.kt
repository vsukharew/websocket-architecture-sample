package dev.vsukharew.websocketarchitecturesample

import android.content.Intent
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
import dev.vsukharew.websocketarchitecturesample.combine.ShareTradingActivity
import dev.vsukharew.websocketarchitecturesample.fold.Conference
import dev.vsukharew.websocketarchitecturesample.fold.ConferenceActivity
import dev.vsukharew.websocketarchitecturesample.fold.ConferenceViewModel
import dev.vsukharew.websocketarchtecturesample.R
import dev.vsukharew.websocketarchtecturesample.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.runningFold.setOnClickListener {
            startActivity(Intent(this, ConferenceActivity::class.java))
        }
        binding.combineInstantly.setOnClickListener {
            startActivity(Intent(this, ShareTradingActivity::class.java))
        }
    }
}