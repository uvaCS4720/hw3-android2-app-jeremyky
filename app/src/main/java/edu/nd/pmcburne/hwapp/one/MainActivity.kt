package edu.nd.pmcburne.hwapp.one

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.nd.pmcburne.hwapp.one.data.ScoreboardRepository
import edu.nd.pmcburne.hwapp.one.ui.ScoreboardScreen
import edu.nd.pmcburne.hwapp.one.ui.ScoreboardViewModel
import edu.nd.pmcburne.hwapp.one.ui.ScoreboardViewModelFactory
import edu.nd.pmcburne.hwapp.one.ui.theme.HWStarterRepoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = ScoreboardRepository(applicationContext)
        setContent {
            HWStarterRepoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: ScoreboardViewModel = viewModel(
                        factory = ScoreboardViewModelFactory(repository)
                    )
                    ScoreboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}