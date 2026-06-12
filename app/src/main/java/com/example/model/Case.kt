package com.example.model

data class Case(
    val id: Int,
    val title: String,
    val difficulty: CaseDifficulty,
    val description: String,
    val backstory: String,
    val suspects: List<String>,
    val weapons: List<String>,
    val locations: List<String>,
    val clues: List<String>,
    val testimonies: List<Testimony> = emptyList(),
    val solutionSuspect: String,
    val solutionWeapon: String,
    val solutionLocation: String,
    val solutionLiar: String? = null // For lie twists
)

enum class CaseDifficulty {
    BEGINNER, INTERMEDIATE, ADVANCED
}

data class Testimony(
    val suspect: String,
    val quote: String
)
