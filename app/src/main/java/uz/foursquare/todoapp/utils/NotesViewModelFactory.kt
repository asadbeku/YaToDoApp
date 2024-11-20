package uz.foursquare.todoapp.utils

import ToDoListRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.foursquare.todoapp.ui.note.NotesViewModel

class NotesViewModelFactory(private val repository: ToDoListRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}