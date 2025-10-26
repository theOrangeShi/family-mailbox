package com.familymailbox

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val attachmentPath: String? = null,
    val attachmentType: String? = null  // "image" 或 "file"
)
