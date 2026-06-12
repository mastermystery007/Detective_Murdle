package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseProgressDao {
    @Query("SELECT * FROM case_progress")
    fun getAllProgress(): Flow<List<CaseProgress>>

    @Query("SELECT * FROM case_progress WHERE caseId = :caseId LIMIT 1")
    suspend fun getProgressForCase(caseId: Int): CaseProgress?

    @Query("SELECT * FROM case_progress WHERE caseId = :caseId LIMIT 1")
    fun getProgressForCaseFlow(caseId: Int): Flow<CaseProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: CaseProgress)

    @Query("DELETE FROM case_progress WHERE caseId = :caseId")
    suspend fun deleteProgressForCase(caseId: Int)

    @Query("DELETE FROM case_progress")
    suspend fun clearAllProgress()
}
