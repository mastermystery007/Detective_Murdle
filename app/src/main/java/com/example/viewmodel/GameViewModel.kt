package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.CaseProgress
import com.example.data.repository.GameRepository
import com.example.model.Case
import com.example.model.CaseDifficulty
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class GamePhase {
    WELCOME,
    CASE_SELECT,
    DOSSIER_INTRO,
    PLAYING,
    ACCUSATION,
    VICTORY,
    FAILURE
}

enum class GridMark {
    NONE,
    X,
    O
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    
    // UI state flows
    private val _gamePhase = MutableStateFlow(GamePhase.WELCOME)
    val gamePhase: StateFlow<GamePhase> = _gamePhase.asStateFlow()

    private val _activeCase = MutableStateFlow<Case?>(null)
    val activeCase: StateFlow<Case?> = _activeCase.asStateFlow()

    // Grid states: key is "row|col", value is "NONE", "X", or "O"
    private val _gridState = MutableStateFlow<Map<String, GridMark>>(emptyMap())
    val gridState: StateFlow<Map<String, GridMark>> = _gridState.asStateFlow()

    // Struck clues: indices of clues that are marked off
    private val _struckClues = MutableStateFlow<Set<Int>>(emptySet())
    val struckClues: StateFlow<Set<Int>> = _struckClues.asStateFlow()

    // Active Accusation variables
    private val _accusedSuspect = MutableStateFlow<String?>(null)
    val accusedSuspect: StateFlow<String?> = _accusedSuspect.asStateFlow()

    private val _accusedWeapon = MutableStateFlow<String?>(null)
    val accusedWeapon: StateFlow<String?> = _accusedWeapon.asStateFlow()

    private val _accusedLocation = MutableStateFlow<String?>(null)
    val accusedLocation: StateFlow<String?> = _accusedLocation.asStateFlow()

    private val _accusedLiar = MutableStateFlow<String?>(null) // For lie-detective levels
    val accusedLiar: StateFlow<String?> = _accusedLiar.asStateFlow()

    // Timer state
    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime.asStateFlow()

    // Solved case IDs (loaded from DB)
    private val _solvedCaseIds = MutableStateFlow<Set<Int>>(emptySet())
    val solvedCaseIds: StateFlow<Set<Int>> = _solvedCaseIds.asStateFlow()

    // Case statuses for list
    private val _caseCompletions = MutableStateFlow<Map<Int, CaseProgress>>(emptyMap())
    val caseCompletions: StateFlow<Map<Int, CaseProgress>> = _caseCompletions.asStateFlow()

    private var timerJob: Job? = null

    init {
        val database = AppDatabase.getDatabase(application)
        repository = GameRepository(database.caseProgressDao())
        
        // Observe progress from database
        viewModelScope.launch {
            repository.getAllProgressFlow().collect { progresses ->
                val solvedIds = progresses.filter { it.isCompleted && it.isSuccess }.map { it.caseId }.toSet()
                _solvedCaseIds.value = solvedIds
                _caseCompletions.value = progresses.associateBy { it.caseId }
            }
        }
    }

    val cases: List<Case>
        get() = repository.staticCases

    fun changePhase(phase: GamePhase) {
        _gamePhase.value = phase
        if (phase == GamePhase.PLAYING) {
            startTimer()
        } else {
            stopTimer()
        }
    }

    fun selectCase(case: Case) {
        _activeCase.value = case
        
        // Load existing progress for this case or initialize fresh
        viewModelScope.launch {
            val progress = repository.getProgressForCase(case.id)
            if (progress != null && !progress.isCompleted) {
                // Restore in-progress state
                _elapsedTime.value = progress.elapsedTimeSec
                _struckClues.value = parseStrikeClues(progress.strikeCluesString)
                _gridState.value = parseGridState(progress.gridStateString)
                // Reset accusation
                _accusedSuspect.value = null
                _accusedWeapon.value = null
                _accusedLocation.value = null
                _accusedLiar.value = null
            } else {
                // Initialize fresh
                _elapsedTime.value = 0L
                _struckClues.value = emptySet()
                _gridState.value = emptyMap()
                _accusedSuspect.value = null
                _accusedWeapon.value = null
                _accusedLocation.value = null
                _accusedLiar.value = null
            }
            changePhase(GamePhase.DOSSIER_INTRO)
        }
    }

    fun startCaseTimer() {
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _elapsedTime.value += 1
                saveCurrentProgressInBg()
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    // Toggle grid mark EMPTY -> X -> O -> EMPTY
    fun toggleGridMark(row: String, col: String) {
        val key = "$row|$col"
        val current = _gridState.value[key] ?: GridMark.NONE
        val next = when (current) {
            GridMark.NONE -> GridMark.X
            GridMark.X -> GridMark.O
            GridMark.O -> GridMark.NONE
        }

        val updatedMap = _gridState.value.toMutableMap()
        
        if (next == GridMark.O) {
            // Apply QOL Auto-Eliminate inside the same category bounds if possible.
            // Find which groups 'row' and 'col' belong to
            val active = _activeCase.value ?: return
            
            val rowGroup = getGroupOfItem(row, active)
            val colGroup = getGroupOfItem(col, active)
            
            if (rowGroup != null && colGroup != null) {
                // Cross out all other items in rowGroup for this col
                for (otherRow in rowGroup) {
                    if (otherRow != row) {
                        updatedMap["$otherRow|$col"] = GridMark.X
                    }
                }
                // Cross out all other items in colGroup for this row
                for (otherCol in colGroup) {
                    if (otherCol != col) {
                        updatedMap["$row|$otherCol"] = GridMark.X
                    }
                }
            }
        }
        
        updatedMap[key] = next
        _gridState.value = updatedMap
        saveCurrentProgressInBg()
    }

    // Help find which category an item belongs to for auto-cross-out
    private fun getGroupOfItem(item: String, active: Case): List<String>? {
        return when {
            active.suspects.contains(item) -> active.suspects
            active.weapons.contains(item) -> active.weapons
            active.locations.contains(item) -> active.locations
            else -> null
        }
    }

    // Toggle clue strike-out
    fun toggleClueStrike(index: Int) {
        val current = _struckClues.value
        val updated = if (current.contains(index)) {
            current - index
        } else {
            current + index
        }
        _struckClues.value = updated
        saveCurrentProgressInBg()
    }

    // Clear entire grid scribble for the active case
    fun clearActiveGrid() {
        _gridState.value = emptyMap()
        _struckClues.value = emptySet()
        saveCurrentProgressInBg()
    }

    // Accusation selection setters
    fun setAccusedSuspect(suspect: String?) {
        _accusedSuspect.value = suspect
    }

    fun setAccusedWeapon(weapon: String?) {
        _accusedWeapon.value = weapon
    }

    fun setAccusedLocation(location: String?) {
        _accusedLocation.value = location
    }

    fun setAccusedLiar(liar: String?) {
        _accusedLiar.value = liar
    }

    // Submit Detective Judgement Report
    fun submitAccusation() {
        val active = _activeCase.value ?: return
        stopTimer()

        val isSuspectCorrect = _accusedSuspect.value == active.solutionSuspect
        val isWeaponCorrect = _accusedWeapon.value == active.solutionWeapon
        val isLocationCorrect = _accusedLocation.value == active.solutionLocation
        
        // If there's a lie twist, check that too
        val isLiarCorrect = if (active.solutionLiar != null) {
            _accusedLiar.value == active.solutionLiar
        } else {
            true
        }

        val allCorrect = isSuspectCorrect && isWeaponCorrect && isLocationCorrect && isLiarCorrect

        viewModelScope.launch {
            val progress = CaseProgress(
                caseId = active.id,
                isCompleted = true,
                isSuccess = allCorrect,
                elapsedTimeSec = _elapsedTime.value,
                strikeCluesString = formatStrikeClues(_struckClues.value),
                gridStateString = formatGridState(_gridState.value)
            )
            repository.saveProgress(progress)
            
            _gamePhase.value = if (allCorrect) GamePhase.VICTORY else GamePhase.FAILURE
        }
    }

    // Restart the current case
    fun restartCase() {
        val active = _activeCase.value ?: return
        viewModelScope.launch {
            // Clear progress in database
            repository.saveProgress(CaseProgress(caseId = active.id))
            _elapsedTime.value = 0L
            _struckClues.value = emptySet()
            _gridState.value = emptyMap()
            _accusedSuspect.value = null
            _accusedWeapon.value = null
            _accusedLocation.value = null
            _accusedLiar.value = null
            changePhase(GamePhase.PLAYING)
        }
    }

    // Save playing board draft progress reactively
    private fun saveCurrentProgressInBg() {
        val active = _activeCase.value ?: return
        if (_gamePhase.value != GamePhase.PLAYING) return
        
        viewModelScope.launch {
            val progress = CaseProgress(
                caseId = active.id,
                isCompleted = false,
                isSuccess = false,
                elapsedTimeSec = _elapsedTime.value,
                strikeCluesString = formatStrikeClues(_struckClues.value),
                gridStateString = formatGridState(_gridState.value)
            )
            repository.saveProgress(progress)
        }
    }

    // Grid string utility formatters
    private fun formatGridState(grid: Map<String, GridMark>): String {
        return grid.entries.joinToString(";") { "${it.key}=${it.value.name}" }
    }

    private fun parseGridState(stateStr: String): Map<String, GridMark> {
        if (stateStr.isEmpty()) return emptyMap()
        return try {
            stateStr.split(";").filter { it.contains("=") }.associate {
                val parts = it.split("=")
                parts[0] to GridMark.valueOf(parts[1])
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun formatStrikeClues(clues: Set<Int>): String {
        return clues.joinToString(",")
    }

    private fun parseStrikeClues(strikeStr: String): Set<Int> {
        if (strikeStr.isEmpty()) return emptySet()
        return try {
            strikeStr.split(",").map { it.toInt() }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
