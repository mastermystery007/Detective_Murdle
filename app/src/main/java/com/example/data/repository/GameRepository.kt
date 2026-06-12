package com.example.data.repository

import com.example.data.database.CaseProgress
import com.example.data.database.CaseProgressDao
import com.example.model.Case
import com.example.model.CaseDifficulty
import com.example.model.Testimony
import kotlinx.coroutines.flow.Flow

class GameRepository(private val caseProgressDao: CaseProgressDao) {

    val staticCases: List<Case> = listOf(
        Case(
            id = 1,
            title = "The Vanishing Ink",
            difficulty = CaseDifficulty.BEGINNER,
            description = "The head librarian was found lifeless at his desk in a pool of mysterious cyan-glowing ink. Can you find the culprit?",
            backstory = "The air in the library is thick with dust and ancient paper. At exactly 8:00 PM, a scream echoed from the archives. The head librarian is dead. It is up to you, Detective, to inspect the premises, cross-reference the logs, and find the killer.",
            suspects = listOf("Lord Gray", "Countess Crimson", "Chef Mustard"),
            weapons = listOf("Cyanide Ink", "Silver Letter Opener", "Heavy Book"),
            locations = listOf("Rare Archives", "Reading Lounge", "Garden Patio"),
            clues = listOf(
                "The suspect with the Silver Letter Opener was in the Garden Patio.",
                "Lord Gray was seen browsing books in the Rare Archives.",
                "The Heavy Book was NOT used in the Reading Lounge.",
                "Chef Mustard was spending his break in the Reading Lounge."
            ),
            solutionSuspect = "Chef Mustard",
            solutionWeapon = "Cyanide Ink",
            solutionLocation = "Reading Lounge"
        ),
        Case(
            id = 2,
            title = "Midnight Train Alibi",
            difficulty = CaseDifficulty.BEGINNER,
            description = "A passenger has been murdered on the Midnight Express. The suspect statements contradict each other. One of them is lying!",
            backstory = "The train ground to a sudden halt on the frozen bridge. Lord Ashford lay slain in his berth. Three suspects remain under guard. Exactly one suspect is lying. Identify who is telling a lie to crack the alibi!",
            suspects = listOf("Professor Plum", "Lady Lavender", "Butler Bleach"),
            weapons = listOf("Pocket Revolver", "Silk Noose", "Arsenic Poison"),
            locations = listOf("Dining Car", "Engine Room", "Private Cabin"),
            clues = listOf(
                "The suspect with the Silk Noose was in the Engine Room.",
                "The murder happened in the Private Cabin using the Pocket Revolver.",
                "Professor Plum was in the Dining Car."
            ),
            testimonies = listOf(
                Testimony("Professor Plum", "I did not have the Arsenic Poison."),
                Testimony("Lady Lavender", "Butler Bleach was in the Private Cabin."),
                Testimony("Butler Bleach", "Professor Plum had the Arsenic Poison.")
            ),
            solutionSuspect = "Professor Plum",
            solutionWeapon = "Arsenic Poison",
            solutionLocation = "Dining Car",
            solutionLiar = "Professor Plum"
        ),
        Case(
            id = 3,
            title = "The Poisoned Toast",
            difficulty = CaseDifficulty.INTERMEDIATE,
            description = "A high-society dinner at the Vintage Bistro was cut short. Deduce the exact links across 4 categories of clues.",
            backstory = "Jazz music played softly as local politicians and VIPs clinked their glasses. Suddenly, a choked gasp silences the room. A poisonous dosage has been served. Inspect the suspect logs in full across VIP lounges and kitchens.",
            suspects = listOf("Mayor Gold", "Countess Crimson", "Chef Mustard", "Madam Orchid"),
            weapons = listOf("Poisoned Wine", "Steak Knife", "Heavy Frying Pan", "Garrote Wire"),
            locations = listOf("VIP Lounge", "Kitchen", "Wine Cellar", "Garden Terrace"),
            clues = listOf(
                "The suspect with the Steak Knife was hiding in the Wine Cellar.",
                "Chef Mustard was on culinary duty in the Kitchen.",
                "The Heavy Frying Pan was used in the Kitchen.",
                "Mayor Gold did not use the Steak Knife.",
                "Countess Crimson was sipping tea in the Garden Terrace.",
                "The suspect who used the Poisoned Wine was in the VIP Lounge."
            ),
            solutionSuspect = "Mayor Gold",
            solutionWeapon = "Poisoned Wine",
            solutionLocation = "VIP Lounge"
        ),
        Case(
            id = 4,
            title = "The Clockmaker's Last Hour",
            difficulty = CaseDifficulty.ADVANCED,
            description = "The gears stopped turning at midnight. Cross-reference 4 suspects, weapons, and locations in this dark gothic riddle.",
            backstory = "A cold fog envelops the Clock Tower. The old clockmaker was found tied to the pendulum. Inspecting the clockworks revealed strange brass adjustments. The killer has manipulated the gears. Follow the clocks!",
            suspects = listOf("Lord Gray", "Professor Plum", "Lady Lavender", "Butler Bleach"),
            weapons = listOf("Pocket Knife", "Cyanide Drops", "Brass Gears", "Clock Spring"),
            locations = listOf("Clock Tower", "Workshop", "Attic Study", "Grand Hall"),
            clues = listOf(
                "The suspect in the Clock Tower had the Brass Gears.",
                "The suspect in the Grand Hall was not Lord Gray.",
                "Professor Plum was up working in the Clock Tower.",
                "The Clock Spring was used in the quiet Attic Study.",
                "The Cyanide Drops were used in the sprawling Grand Hall.",
                "Butler Bleach was in the Workshop, but did not carry the Cyanide Drops.",
                "Lady Lavender did not have the Pocket Knife."
            ),
            solutionSuspect = "Lady Lavender",
            solutionWeapon = "Cyanide Drops",
            solutionLocation = "Grand Hall"
        ),
        Case(
            id = 5,
            title = "Museum Heist Backfire",
            difficulty = CaseDifficulty.ADVANCED,
            description = "The ultimate heist turned into a double-cross crime. Detect testimonies and solve which suspect is lying!",
            backstory = "An ancient exhibit was broken into at 2:00 AM, but instead of fleeing with the loot, one thief silenced their partner. We have recovered the crime scene alibis. Precisely one thief is lying to protect themselves. Find them!",
            suspects = listOf("Mayor Gold", "Chef Mustard", "Countess Crimson", "Madam Orchid"),
            weapons = listOf("Ancient Dagger", "Golden Chalice", "Rope", "Poison Dart"),
            locations = listOf("Exhibition Hall", "Crypt", "Vault", "Rooftop"),
            clues = listOf(
                "A museum security guard saw the Rope being tied to the Rooftop.",
                "The Ancient Dagger was used in the Exhibition Hall.",
                "Mayor Gold was seen scale-climbing toward the Rooftop."
            ),
            testimonies = listOf(
                Testimony("Mayor Gold", "Chef Mustard had the Poison Dart."),
                Testimony("Chef Mustard", "I was in the Vault."),
                Testimony("Countess Crimson", "The suspect in the Crypt had the Poison Dart."),
                Testimony("Madam Orchid", "I was in the Vault with the Golden Chalice.")
            ),
            solutionSuspect = "Chef Mustard",
            solutionWeapon = "Poison Dart",
            solutionLocation = "Crypt",
            solutionLiar = "Chef Mustard"
        )
    )

    fun getAllProgressFlow(): Flow<List<CaseProgress>> = caseProgressDao.getAllProgress()

    fun getProgressForCaseFlow(caseId: Int): Flow<CaseProgress?> = caseProgressDao.getProgressForCaseFlow(caseId)

    suspend fun getProgressForCase(caseId: Int): CaseProgress? = caseProgressDao.getProgressForCase(caseId)

    suspend fun saveProgress(progress: CaseProgress) {
        caseProgressDao.insertOrUpdateProgress(progress)
    }

    suspend fun clearAllProgress() {
        caseProgressDao.clearAllProgress()
    }
}
