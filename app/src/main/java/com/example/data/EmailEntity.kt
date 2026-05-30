package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emails")
data class EmailEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String,
    val senderAddress: String,
    val recipient: String,
    val subject: String,
    val snippet: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val isStarred: Boolean = false,
    val accountOwner: String, // "raoboss@bharatmail.in" or "raoboss.ff.gaming@gmail.com"
    val folder: String = "inbox", // "inbox", "sent", "archive"
    val label: String = "General" // "Gaming", "Security", "General", "AI Drafted"
)
