package com.familymailbox

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY id DESC")
    fun getAllMessages(): LiveData<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: Message)
    
    @Query("DELETE FROM messages")
    suspend fun deleteAll()
}
