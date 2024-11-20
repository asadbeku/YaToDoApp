package uz.foursquare.todoapp.ui.note

import ToDoListRepository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import uz.foursquare.todoapp.ui.todolist.components.TodoItem

class NotesViewModel(
    private val repository: ToDoListRepository
) : ViewModel() {

    private val _task = MutableStateFlow<TodoItem?>(null)
    val taskStateFlow: StateFlow<TodoItem?> = _task.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val scope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        _errorMessage.value = "Failed to fetch task"
        _isLoading.value = false
        Log.e("NotesViewModel", "Coroutine exception", throwable)
    }

    fun addTask(task: TodoItem) {
        scope.launch {
            _isLoading.value = true
            try {
                repository.addTask(task)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTask(id: String) {
        scope.launch {
            _isLoading.value = true
            try {
                Log.d("NotesViewModel", "Deleting task with id: $id")
                repository.deleteTask(id)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getTaskById(id: String) {
        scope.launch {
            _isLoading.value = true
            try {
                _task.value = repository.getTaskById(id)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTask(task: TodoItem) {
        scope.launch {
            _isLoading.value = true
            try {
                repository.updateTask(task)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}