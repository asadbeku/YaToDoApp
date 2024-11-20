package uz.foursquare.todoapp.ui.todolist

import ToDoListRepository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import uz.foursquare.todoapp.ui.todolist.components.TodoItem

class ToDoListViewModel(
    private val repository: ToDoListRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<TodoItem>>(emptyList())
    val tasksStateFlow: StateFlow<List<TodoItem>> = _tasks.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val scope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        _errorMessage.value = throwable.message
        _isLoading.value = false
        Log.e("uz.foursquare.todoapp.app_ui.todolist.ToDoListViewModel", "Coroutine exception", throwable)
    }

    init {
        loadTasks()
    }

    fun loadTasks() {
        scope.launch {
            _isLoading.value = true
            try {
                repository.getTasks().collectLatest { tasks ->
                    _tasks.value = tasks
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addTask(task: TodoItem) {
        scope.launch {
            try {
                repository.addTask(task)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun deleteTask(id: String) {
        scope.launch {
            try {
                repository.deleteTask(id)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    private fun updateTask(task: TodoItem) {
        scope.launch {
            Log.d("uz.foursquare.todoapp.app_ui.todolist.ToDoListViewModel", "Updating task: $task")
            try {
                repository.updateTask(task)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun toggleTaskCompletion(task: TodoItem) {
        val updatedTask = task.copy(done = task.done)
        updateTask(updatedTask)
    }
}