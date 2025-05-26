package com.example.roomdatabase.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Note::class],
    version = 1
)
abstract class NotesDatabase: RoomDatabase() {
    // dao variable
    abstract val dao:NoteDao
}