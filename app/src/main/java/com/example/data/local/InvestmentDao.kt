package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.InvestmentProject
import com.example.data.model.ProjectWithWithdrawals
import com.example.data.model.WithdrawalTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestmentDao {
    @Transaction
    @Query("SELECT * FROM projects ORDER BY startDate DESC")
    fun getAllProjectsWithWithdrawals(): Flow<List<ProjectWithWithdrawals>>

    @Transaction
    @Query("SELECT * FROM projects WHERE id = :id")
    fun getProjectWithWithdrawals(id: Long): Flow<ProjectWithWithdrawals?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: InvestmentProject): Long

    @Update
    suspend fun updateProject(project: InvestmentProject)

    @Delete
    suspend fun deleteProject(project: InvestmentProject)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(withdrawal: WithdrawalTransaction): Long

    @Delete
    suspend fun deleteWithdrawal(withdrawal: WithdrawalTransaction)

    @Query("DELETE FROM withdrawals WHERE id = :id")
    suspend fun deleteWithdrawalById(id: Long)

    @Query("SELECT * FROM withdrawals WHERE projectId = :projectId ORDER BY date DESC")
    fun getWithdrawalsForProject(projectId: Long): Flow<List<WithdrawalTransaction>>

    @Query("SELECT * FROM withdrawals ORDER BY date DESC")
    fun getAllWithdrawals(): Flow<List<WithdrawalTransaction>>

    @Query("SELECT COUNT(*) FROM projects")
    suspend fun getProjectCount(): Int

    @Query("DELETE FROM projects")
    suspend fun deleteAllProjects()

    @Query("DELETE FROM withdrawals")
    suspend fun deleteAllWithdrawals()

    @Query("DELETE FROM projects WHERE name IN (:names)")
    suspend fun deleteProjectsByNames(names: List<String>)
}
