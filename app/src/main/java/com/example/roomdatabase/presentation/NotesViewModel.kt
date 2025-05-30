package com.example.roomdatabase.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomdatabase.data.Note
import com.example.roomdatabase.data.NoteDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotesViewModel(
    // val of note dao
    private val dao: NoteDao
):ViewModel() {

    private val isSortedByDateAdded = MutableStateFlow(true)

    private val notes =
        isSortedByDateAdded.flatMapLatest { sort->
            if (sort){
                dao.getNotesOrderByDateAdded()
            }else{
                dao.getNoteOrderByTitle()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val _state = MutableStateFlow(NoteState())
    val state =
        combine(_state, isSortedByDateAdded,notes){ state, isSortedByDateAdded,notes -> // when this are change then update the state
            state.copy(
                notes = notes
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),NoteState())


    fun onEvent(event: NotesEvent){
        when (event){
            is NotesEvent.DeleteNote ->{
                viewModelScope.launch {
                    dao.deleteNote(event.note)
                }

            }
            is NotesEvent.SaveNote ->{
                val note = Note(
                        title = state.value.title.value,
                        description = state.value.description.value,
                        dateAdded = System.currentTimeMillis()
                    )

                viewModelScope.launch {
                    dao.upsertNote(note)
                }

                _state.update {
                    it.copy(
                        title = mutableStateOf(""),
                        description = mutableStateOf("")
                    )
                }

            }
            NotesEvent.SortNotes -> {
                isSortedByDateAdded.value = !isSortedByDateAdded.value
            }
        }
    }
}