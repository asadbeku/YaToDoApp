package uz.foursquare.todoapp.data

import uz.foursquare.todoapp.ui.todolist.components.TodoItem

interface ToDoListDataSource {
    suspend fun getTasks(): List<TodoItem>
    suspend fun addTask(note: TodoItem)
    suspend fun deleteTask(id: String)
    suspend fun updateTask(note: TodoItem)
    suspend fun getTaskById(id: String): TodoItem?
}