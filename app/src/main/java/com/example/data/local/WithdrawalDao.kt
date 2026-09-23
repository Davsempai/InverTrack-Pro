package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.WithdrawalTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WithdrawalDao {
    @Query("SELECT * FROM withdrawals WHERE projectId = :projectId ORDER BY date DESC")
    fun getWithdrawalsForProject(projectId: Long): Flow<List<WithdrawalTransaction>>

    @Query("SELECT * FROM withdrawals ORDER BY date DESC")
    fun getAllWithdrawals(): Flow<List<WithdrawalTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(withdrawal: WithdrawalTransaction): Long

    @Update
    suspend fun updateWithdrawal(withdrawal: WithdrawalTransaction)

    @Delete
    suspend fun deleteWithdrawal(withdrawal: WithdrawalTransaction)

    @Query("DELETE FROM withdrawals WHERE id = :id")
    suspend fun deleteWithdrawalById(id: Long)
}
