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
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY startDate DESC")
    fun getAllProjects(): Flow<List<InvestmentProject>>

    @Transaction
    @Query("SELECT * FROM projects ORDER BY startDate DESC")
    fun getAllProjectsWithWithdrawals(): Flow<List<ProjectWithWithdrawals>>

    @Transaction
    @Query("SELECT * FROM projects WHERE id = :projectId")
    fun getProjectWithWithdrawalsById(projectId: Long): Flow<ProjectWithWithdrawals?>

    @Query("SELECT * FROM projects WHERE id = :projectId")
    suspend fun getProjectById(projectId: Long): InvestmentProject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: InvestmentProject): Long

    @Update
    suspend fun updateProject(project: InvestmentProject)

    @Delete
    suspend fun deleteProject(project: InvestmentProject)

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: Long)

    @Query("SELECT COUNT(*) FROM projects")
    suspend fun getProjectCount(): Int
}
