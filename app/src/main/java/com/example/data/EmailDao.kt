package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EmailDao {
    @Query("SELECT * FROM emails WHERE accountOwner = :account ORDER BY timestamp DESC")
    fun getEmailsForAccount(account: String): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE accountOwner = :account AND folder = :folder ORDER BY timestamp DESC")
    fun getEmailsForAccountAndFolder(account: String, folder: String): Flow<List<EmailEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmail(email: EmailEntity)

    @Update
    suspend fun updateEmail(email: EmailEntity)

    @Delete
    suspend fun deleteEmail(email: EmailEntity)

    @Query("DELETE FROM emails WHERE id = :id")
    suspend fun deleteEmailById(id: Int)
}
