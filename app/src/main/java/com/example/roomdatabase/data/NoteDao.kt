package com.example.roomdatabase.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Upsert // this is work on update and insert
    suspend fun upsertNote(note: Note) // use suspend keyword to make our dun synchronize

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM note ORDER BY dateAdded")
    fun getNotesOrderByDateAdded():Flow<List<Note>>

    @Query("SELECT * FROM note ORDER BY title ASC")
    fun getNoteOrderByTitle():Flow<List<Note>>
}