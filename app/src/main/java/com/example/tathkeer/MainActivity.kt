package com.example.tathkeer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.tathkeer.data.Preferences
import com.example.tathkeer.ui.MainScreen
import com.example.tathkeer.viewmodel.MainViewModel
import com.example.tathkeer.widget.WidgetUpdateWorker
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var prefs: Preferences
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = Preferences(this)
        viewModel = MainViewModel(prefs)

        if (intent?.action == "INCREMENT_FROM_WIDGET") {
            lifecycleScope.launch {
                viewModel.increment()
            }
        }

        setContent {
            TathkeerTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        WidgetUpdateWorker.updateWidget(this)
    }
}
