package uz.foursquare.todoapp.utils

import ToDoListRepository
import uz.foursquare.todoapp.ui.todolist.ToDoListViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ToDoListViewModelFactory(private val repository: ToDoListRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ToDoListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ToDoListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}