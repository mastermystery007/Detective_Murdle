package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "case_progress")
data class CaseProgress(
    @PrimaryKey val caseId: Int,
    val isCompleted: Boolean = false,
    val isSuccess: Boolean = false,
    val elapsedTimeSec: Long = 0L,
    val strikeCluesString: String = "", // Comma-separated list of struck clue indices, e.g. "0,2,3"
    val gridStateString: String = ""    // Coordinates state: "row:col=mark;row2:col2=mark2;"
)
