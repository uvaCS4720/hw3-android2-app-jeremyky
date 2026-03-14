package edu.nd.pmcburne.hwapp.one.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.nd.pmcburne.hwapp.one.data.Game
import edu.nd.pmcburne.hwapp.one.data.ScoreboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ScoreboardUiState(
    val games: List<Game> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val isMenSelected: Boolean = true,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
)

class ScoreboardViewModel(private val repository: ScoreboardRepository) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _isMenSelected = MutableStateFlow(true)
    private val _isLoading = MutableStateFlow(false)
    private val _isRefreshing = MutableStateFlow(false)
    private val _games = MutableStateFlow<List<Game>>(emptyList())

    val uiState: StateFlow<ScoreboardUiState> = combine(
        _selectedDate,
        _isMenSelected,
        _isLoading,
        _isRefreshing,
        _games
    ) { date, isMen, loading, refreshing, games ->
        ScoreboardUiState(
            games = games,
            selectedDate = date,
            isMenSelected = isMen,
            isLoading = loading,
            isRefreshing = refreshing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScoreboardUiState()
    )

    init {
        viewModelScope.launch {
            combine(_selectedDate, _isMenSelected) { date, isMen -> Pair(date, isMen) }
                .distinctUntilChanged()
                .collect { (date, isMen) ->
                    val gender = if (isMen) "men" else "women"
                    _isLoading.value = true
                    repository.refresh(gender, date)
                    repository.getGames(gender, date).collect { games ->
                        _games.value = games
                        _isLoading.value = false
                    }
                }
        }
    }

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun onGenderToggle(isMen: Boolean) {
        _isMenSelected.value = isMen
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val gender = if (_isMenSelected.value) "men" else "women"
            repository.refresh(gender, _selectedDate.value)
            _isRefreshing.value = false
        }
    }
}

class ScoreboardViewModelFactory(private val repository: ScoreboardRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScoreboardViewModel::class.java)) {
            return ScoreboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
